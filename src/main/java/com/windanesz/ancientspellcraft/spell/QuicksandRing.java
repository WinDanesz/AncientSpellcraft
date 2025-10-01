package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.block.ITemporaryBlock;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASSpells;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class QuicksandRing extends Spell {

	public QuicksandRing() {
		super(AncientSpellcraft.MODID, "quicksand_ring", SpellActions.SUMMON, false);
		addProperties(EFFECT_RADIUS, DURATION);

	}

	@Override
	public boolean requiresPacket() { return false; }

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) { return true; }

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) { return true; }

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		if (!summonQuickSandRing(world, caster, caster.getPosition(), modifiers))
			return false;
		this.playSound(world, caster, ticksInUse, -1, modifiers);
		return true;
	}

	@Override
	public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target, SpellModifiers modifiers) {
		if (!summonQuickSandRing(world, caster, caster.getPosition(), modifiers))
			return false;
		this.playSound(world, caster, ticksInUse, -1, modifiers);
		return true;
	}

	@Override
	public boolean cast(World world, double x, double y, double z, EnumFacing direction, int ticksInUse, int duration, SpellModifiers modifiers) {
		if (!summonQuickSandRing(world, null, new BlockPos(x, y, z).offset(direction), modifiers))
			return false;
		this.playSound(world, x, y, z, ticksInUse, duration, modifiers);
		return true;
	}

	//  from Thorns spell, by Electroblob
	public static boolean summonQuickSandRing(World world, @Nullable EntityLivingBase caster, BlockPos origin, SpellModifiers modifiers) {
		if (!world.isRemote) {

			List<BlockPos> ring = new ArrayList<>();
			ring.add(origin);
			ring.add(origin.add(1, 0, 0));
			ring.add(origin.add(-1, 0, 0));
			ring.add(origin.add(0, 0, 1));
			ring.add(origin.add(0, 0, -1));

			if (ring.isEmpty())
				return false;

			for (int i = 1; i < 3; i++) {

				for (BlockPos pos : ring) {

					pos = pos.offset(EnumFacing.DOWN, i);

					ITemporaryBlock.placeTemporaryBlock(caster, world, ASBlocks.QUICKSAND, pos, 800);
				}

				if (caster instanceof EntityPlayer && !ItemArtefact.isArtefactActive((EntityPlayer) caster, ASItems.ring_quicksand)) {
					break;
				}
			}
		}

		return true;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}