package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.capability.DeathMarkCapability;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class DeathMark extends SpellRayAS {

	public DeathMark() {
		super("death_mark", SpellActions.POINT, true);
		soundValues(0.7f, 1f, 1f);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		if (caster instanceof EntityLivingBase && target instanceof EntityLivingBase) {
			EntityLivingBase entity = (EntityLivingBase) target;
			DeathMarkCapability cap = DeathMarkCapability.get(entity);
			cap.setCasterId(caster.getUniqueID());
		}

		return true;
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
	protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
		ParticleBuilder.create(ParticleBuilder.Type.LEAF).pos(x, y, z).vel(vx, vy, vz).collide(true).spawn(world);
	}
}
