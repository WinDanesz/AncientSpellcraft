package com.windanesz.ancientspellcraft.util;

import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.packet.PacketCastSpell;
import electroblob.wizardry.packet.WizardryPacketHandler;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import javax.annotation.Nullable;

public class SpellcastUtils {

	private SpellcastUtils() {} // no instances!

	public static boolean tryCastSpellAsMob(EntityLiving entityLiving, Spell spell, @Nullable EntityLivingBase target) {
		// if true, the spell cast fails
		if (MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Pre(SpellCastEvent.Source.WAND, spell, entityLiving, new SpellModifiers())))
			return false;

		if (spell.isContinuous)
			return false;

		if (spell.cast(entityLiving.world, entityLiving, EnumHand.MAIN_HAND, 0, target, new SpellModifiers())) {

			MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Post(SpellCastEvent.Source.OTHER, spell, entityLiving, new SpellModifiers()));

			if (spell.requiresPacket()) {
				// Sends a packet to all players in dimension to tell them to spawn particles.
				// Only sent if the spell succeeded, because if the spell failed, you wouldn't
				// need to spawn any particles!
				IMessage msg = new PacketCastSpell.Message(entityLiving.getEntityId(), null, spell, new SpellModifiers());
				WizardryPacketHandler.net.sendToDimension(msg, entityLiving.world.provider.getDimension());
			}
			return true;
		}

		return false;
	}

	@SuppressWarnings("Duplicates")
	public static boolean tryCastSpellAsPlayer(EntityPlayer player, Spell spell, EnumHand hand,
			SpellCastEvent.Source source, SpellModifiers modifiers, int duration) {

		WizardData data = WizardData.get(player);

		if (data == null)
			return false;

		// if true, the spell cast fails
		if (MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Pre(source, spell, player, modifiers)))
			return false;

		if (spell.isContinuous) {

			// Events/packets for continuous spell casting via commands are dealt with in WizardData.

			if (data.isCasting()) {
				data.stopCastingContinuousSpell(); // I think on balance this is quite a nice feature to leave in
			}
			data.startCastingContinuousSpell(spell, modifiers, duration);

			return true;

		} else {

			if (spell.cast(player.world, player, EnumHand.MAIN_HAND, 0, modifiers)) {

				MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Post(SpellCastEvent.Source.COMMAND, spell, player, modifiers));

				if (spell.requiresPacket()) {
					// Sends a packet to all players in dimension to tell them to spawn particles.
					// Only sent if the spell succeeded, because if the spell failed, you wouldn't
					// need to spawn any particles!
					IMessage msg = new PacketCastSpell.Message(player.getEntityId(), hand, spell, modifiers);
					WizardryPacketHandler.net.sendToDimension(msg, player.world.provider.getDimension());
				}
				return true;
			}
		}
		return false;
	}

	public static boolean tryCastSpellAtLocation(World world, BlockPos pos, EnumFacing direction, Spell spell, SpellModifiers modifiers) {
		if (spell == null)
			return false;

		if (!spell.canBeCastBy(new TileEntityDispenser()))
			return false;

		// if true, the spell cast fails
		if (MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Pre(SpellCastEvent.Source.OTHER, spell, world, pos.getX(), pos.getY(), pos.getZ(), direction, modifiers)))
			return false;

		if (spell.isContinuous)
			return false;

		if (spell.cast(world, pos.getX(), pos.getY(), pos.getZ(), direction, 0, 120, modifiers)) {

			MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Post(SpellCastEvent.Source.OTHER, spell, world, pos.getX(), pos.getY(), pos.getZ(), direction,modifiers));

			if (spell.requiresPacket()) {
				// Sends a packet to all players in dimension to tell them to spawn particles.
				// Only sent if the spell succeeded, because if the spell failed, you wouldn't
				// need to spawn any particles!
				// There should be a player nearby, otherwise no need for the particles anyways
				EntityPlayer player = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 30, false);
				if (player == null)
					return true;
				IMessage msg = new PacketCastSpell.Message(player.getEntityId(), null, spell, modifiers);
				WizardryPacketHandler.net.sendToDimension(msg, world.provider.getDimension());
			}
		}

		return true;
	}
}
