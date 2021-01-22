package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ChannelPower extends SpellRay {

	/**
	 * The number by which this spell's damage is multiplied for undead entities.
	 */
	public ChannelPower(String modID, String name, EnumAction action, boolean isContinuous) {
		super(modID, name, SpellActions.POINT, true);
		addProperties(EFFECT_DURATION);
	}

	//cant be cast by wizards
	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) {
		return false;
	}

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) {
		return false;
	}

	// The following three methods serve as a good example of how to implement continuous spell sounds (hint: it's easy)

	@Override
	protected SoundEvent[] createSounds() {
		return this.createContinuousSpellSounds();
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
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		if (!world.isRemote) {
			if (EntityUtils.isLiving(target)) {
				if (target instanceof EntityLivingBase) {
					EntityLivingBase entityLivingBase = (EntityLivingBase) target;
					if (!entityLivingBase.isPotionActive(MobEffects.GLOWING)) {
						entityLivingBase.addPotionEffect(new PotionEffect(MobEffects.GLOWING, 60, 1));
					}
					if (ticksInUse > 40) {
						entityLivingBase.addPotionEffect(new PotionEffect(AncientSpellcraftPotions.unlimited_power,
								(int) (getProperty(EFFECT_DURATION).floatValue()))); //  * modifiers.get(WizardryItems.duration_upgrade)
					}
				}
			}
		}
		return true;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin,
			int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onMiss(World world, EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return true;
	}

	@Override
	protected void spawnParticleRay(World world, Vec3d origin, Vec3d direction, EntityLivingBase caster, double distance) {

		if (caster != null) {
			ParticleBuilder.create(ParticleBuilder.Type.BEAM).entity(caster).pos(origin.subtract(caster.getPositionVector()))
					.length(distance).clr(0.98f, 0.6f + 0.3f * world.rand.nextFloat(), 0.98f)
					.scale(MathHelper.sin(world.getTotalWorldTime() * 0.2f) * 0.1f + 1.4f).spawn(world);
		} else {
			ParticleBuilder.create(ParticleBuilder.Type.BEAM).pos(origin).target(origin.add(direction.scale(distance)))
					.clr(0.98f, 0.6f + 0.3f * world.rand.nextFloat(), 0.98f)
					.scale(MathHelper.sin(world.getTotalWorldTime() * 0.2f) * 0.1f + 1.4f).spawn(world);
		}
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
