package com.windanesz.ancientspellcraft.spell;

import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellBuff;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class Singe extends SpellRayAS {

	public static final String MULTIPLIER_TAG = "damage_multiplier_on_fire";

	public Singe() {
		super("singe", SpellActions.POINT, false);
		addProperties(DAMAGE, MULTIPLIER_TAG);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {

		if (target instanceof EntityLivingBase) {

			EntityLivingBase targetEntity = (EntityLivingBase) target;
			int bonusAmplifier = SpellBuff.getStandardBonusAmplifier(modifiers.get(SpellModifiers.POTENCY));

			float damage = getProperty(DAMAGE).floatValue() * modifiers.get(SpellModifiers.POTENCY);

			boolean burning = targetEntity.isBurning();
			if (burning) { damage = damage * getProperty(MULTIPLIER_TAG).floatValue(); }

			if (MagicDamage.isEntityImmune(MagicDamage.DamageType.FIRE, target)) {
				if (!world.isRemote && caster instanceof EntityPlayer) {
					((EntityPlayer) caster).sendStatusMessage(
							new TextComponentTranslation("spell.resist", target.getName(), this.getNameForTranslationFormatted()), true);
				}
			} else {
				EntityUtils.attackEntityWithoutKnockback(target, MagicDamage.causeDirectMagicDamage(caster, MagicDamage.DamageType.FIRE), damage);
				if (burning) { targetEntity.extinguish(); }
			}

			return true;
		}
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onMiss(World world, @Nullable EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) { return true; }

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) { return true; }

	@Override
	protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz){
		world.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0, 0, 0);
	}
}
