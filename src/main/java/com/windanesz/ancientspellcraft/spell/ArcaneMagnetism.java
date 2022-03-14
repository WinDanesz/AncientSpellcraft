package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ArcaneMagnetism extends Spell {

	public ArcaneMagnetism() {
		super(AncientSpellcraft.MODID, "arcane_magnetism", EnumAction.BOW, true);
		addProperties(EFFECT_RADIUS);
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) {
		return false;
	}

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) {
		return true;
	}

	@Override
	protected SoundEvent[] createSounds() {
		return createContinuousSpellSounds();
	}

	@Override
	protected void playSound(World world, EntityLivingBase entity, int ticksInUse, int duration, SpellModifiers modifiers, String... sounds) {
		this.playSoundLoop(world, entity, ticksInUse);
	}

	@Override
	protected void playSound(World world, double x, double y, double z, int ticksInUse, int duration, SpellModifiers modifiers, String... sounds) {
		this.playSoundLoop(world, x, y, z, ticksInUse, duration);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		performEffect(world, caster.getPositionEyes(0).subtract(0, 0.1, 0), caster, modifiers, ticksInUse);
		this.playSound(world, caster, ticksInUse, -1, modifiers);
		return true;
	}

	@Override
	public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target, SpellModifiers modifiers) {
		performEffect(world, caster.getPositionEyes(0).subtract(0, 0.1, 0), caster, modifiers, ticksInUse);
		this.playSound(world, caster, ticksInUse, -1, modifiers);
		return true;
	}

	@Override
	public boolean cast(World world, double x, double y, double z, EnumFacing direction, int ticksInUse, int duration, SpellModifiers modifiers) {
		performEffect(world, new Vec3d(x, y, z), null, modifiers, ticksInUse);
		this.playSound(world, x - direction.getXOffset(), y - direction.getYOffset(), z - direction.getZOffset(), ticksInUse, duration, modifiers);
		return true;
	}

	private void performEffect(World world, Vec3d centre, @Nullable EntityLivingBase caster, SpellModifiers modifiers, int ticksInUse) {

		double radius = getProperty(EFFECT_RADIUS).floatValue() * modifiers.get(WizardryItems.blast_upgrade);

		List<EntityItem> targets = ASUtils.getEntityItemsWithinRadius(radius, centre.x, centre.y, centre.z, world);

		for (EntityItem entityItem : targets) {

			if (caster instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) caster;
				if (ItemArtefact.isArtefactActive(player, ASItems.ring_lodestone) && !player.getHeldItemOffhand().isEmpty()) {
					ItemStack stack = player.getHeldItemOffhand();
					if (!(ItemStack.areItemsEqualIgnoreDurability(entityItem.getItem(), stack))) {
						continue;
					}
				}
			}

			//			entityItem.motionX = ((caster.getPosition().getX() - entityItem.posX) / 20);
			entityItem.motionX = Math.abs(caster.getPosition().getX() - entityItem.posX) > 0.04 ? (caster.getPosition().getX() - entityItem.posX) / 20 : (caster.getPosition().getX() - entityItem.posX) / 10;
			//			entityItem.motionY = Math.abs(caster.getPosition().getY() - entityItem.posY) > 0.04 ?  (caster.getPosition().getY() - entityItem.posY) / 20 : (caster.getPosition().getY() - entityItem.posY) / 10;
			entityItem.motionY = Math.abs((caster.getPosition().getY() + 0.3) - entityItem.posY) > 0.02 ? (caster.getPosition().getY() - entityItem.posY) / 20 : (caster.getPosition().getY() - entityItem.posY) / 5;
			entityItem.motionZ = Math.abs(caster.getPosition().getZ() - entityItem.posZ) > 0.04 ? (caster.getPosition().getZ() - entityItem.posZ) / 20 : (caster.getPosition().getZ() - entityItem.posZ) / 10;
			//			entityItem.motionZ = (caster.getPosition().getZ() - entityItem.posZ) / 20;

			if (world.isRemote) {
				// Sparks
				if (ticksInUse % 5 == 0) {
					ParticleBuilder.create(Type.FLASH).entity(entityItem).pos(0, 0.3, 0).clr(137, 19, 235).scale(1).spawn(world);
				}
			}
		}
		if (world.isRemote) {
			// Sparks
			if (ticksInUse % 5 == 0) {
				ParticleBuilder.create(Type.FLASH).entity(caster).clr(137, 19, 235).scale(4)
						.pos(caster != null ? centre.subtract(caster.getPositionVector()) : centre).spawn(world);
			}
		}
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}