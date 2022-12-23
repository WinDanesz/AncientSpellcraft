package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.capability.DeathMarkCapability;
import com.windanesz.ancientspellcraft.util.ASUtils;
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
		super("death_mark", SpellActions.POINT, false);
		soundValues(0.7f, 1f, 1f);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		if (caster instanceof EntityLivingBase && target instanceof EntityLivingBase) {
			EntityLivingBase entity = (EntityLivingBase) target;
			DeathMarkCapability cap = DeathMarkCapability.get(entity);
			if (cap != null) {
				cap.setCasterId(caster.getUniqueID());
				ASUtils.sendMessage(caster, "spell.ancientspellcraft:death_mark.entity_death_message", false, target.getDisplayName());
			}
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
		ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(x, y, z).clr(0.2f, 0, 0.3f).spawn(world);
		ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(x, y, z).clr(0.1f, 0, 0).spawn(world);
		ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).time(12 + world.rand.nextInt(8)).clr(0.4f, 0, 0).spawn(world);
	}
}
