package com.windanesz.ancientspellcraft.entity.projectile;

import com.windanesz.ancientspellcraft.entity.living.EntityStoneGuardian;
import electroblob.wizardry.entity.projectile.EntityMagicProjectile;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityStoneGuardianShard extends EntityMagicProjectile {

	public EntityStoneGuardianShard(World world) {
		super(world);
		this.setSize(rand.nextFloat() + 0.1f, rand.nextFloat() + 0.1f);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox() {
		return null;//this.getEntityBoundingBox();
	}

	@Override
	public int getLifetime() { return -1; }

	@Override
	protected void onImpact(RayTraceResult result) {

		//		if(result.entityHit != null){
		//			result.entityHit.setFire(Spells.disintegration.getProperty(Spell.BURN_DURATION).intValue());
		//		}

		if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
			this.inGround = true;
			this.collided = true;
			if (result.sideHit.getAxis() == EnumFacing.Axis.X) { motionX = 0; }
			if (result.sideHit.getAxis() == EnumFacing.Axis.Y) {
				motionY = 0;
				this.collidedVertically = true;
			}
			if (result.sideHit.getAxis() == EnumFacing.Axis.Z) { motionZ = 0; }
		}
	}

	@Override
	public void applyEntityCollision(Entity entity) {

		super.applyEntityCollision(entity);

		if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).getHealth() > 0) {
			entity.setFire(Spells.disintegration.getProperty(Spell.BURN_DURATION).intValue());
		}
	}

	@Override
	public void onUpdate() {
		int minLifetime = 400;
		super.onUpdate();

		if (this.collidedVertically) {
			this.motionY += this.getGravityVelocity();
			this.motionX *= 0.5;
			this.motionZ *= 0.5;
		}

		//		world.getEntitiesInAABBexcluding(thrower, this.getEntityBoundingBox(), e -> e instanceof EntityLivingBase)
		//				.stream().filter(e -> !(e instanceof EntityLivingBase) || ((EntityLivingBase)e).getHealth() > 0)
		//				.forEach(e -> e.setFire(Spells.disintegration.getProperty(Spell.BURN_DURATION).intValue()));

		// Copied from ParticleLava
		if (this.rand.nextInt(minLifetime) * 2 < ticksExisted) {
			this.world.spawnParticle(EnumParticleTypes.PORTAL, this.posX, this.posY, this.posZ, this.motionX + rand.nextFloat() - 0.5, this.motionY, this.motionZ + rand.nextFloat() - 0.5);
		}

		if (!world.isRemote && this.ticksExisted > minLifetime && rand.nextInt(10) == 0) {
			EntityStoneGuardian guardian = new EntityStoneGuardian(this.world);
			guardian.setPosition(this.getPosition().getX() + 0.5, this.getPosition().getY() + 0.5, this.getPosition().getZ() + 0.5);
			guardian.setDropLoot(false);
			world.spawnEntity(guardian);
			for (Entity entity : EntityUtils.getEntitiesWithinRadius(3, this.posX, this.posY, this.posZ, this.world, EntityStoneGuardianShard.class)) {
				entity.setDead();
			}
		}
	}
}
