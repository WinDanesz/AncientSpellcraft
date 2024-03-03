package com.windanesz.ancientspellcraft.entity;

import electroblob.wizardry.entity.projectile.EntityMagicProjectile;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.MagicDamage.DamageType;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Collectors;

public class EntityChaosOrb extends EntityMagicProjectile {

	private boolean canSeeTarget = false;
	int generation  = 7;
	int redirections = 0;
	public EntityChaosOrb(World world) {
		super(world);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (world.isRemote && this.ticksExisted > 1) { // Don't spawn particles behind where it started!
			//			ParticleBuilder.create(Type.SPARKLE, rand, posX, posY, posZ, 0.03, true).clr(1, 1, 0.65f).fade(0.7f, 0, 1)
			//				.time(20 + rand.nextInt(10)).spawn(world);
			double x = posX - motionX / 2;
			double y = posY - motionY / 2;
			double z = posZ - motionZ / 2;
			ParticleBuilder.create(Type.SPARKLE, rand, x, y, z, 0.03, true).clr(0xfc0303).scale(0.3f).fade(0x400101)
					.time(20 + rand.nextInt(10)).spawn(world);
		}


		// Seeking
		if ((ticksExisted == 1)  || ticksExisted % 2 == 0) {
			this.motionX *= 1.1f;
			this.motionY *= 1.1f;
			this.motionZ *= 1.1f;
			List<EntityLivingBase> nearby = EntityUtils.getLivingWithinRadius(5, this.posX, this.posY, this.posZ, world);
			if (nearby.isEmpty()) {
				nearby = EntityUtils.getLivingWithinRadius(10, this.posX, this.posY, this.posZ, world);
			}
			List<EntityLivingBase> nearbyAllies = nearby.stream().filter(e -> e ==thrower
					|| AllyDesignationSystem.isAllied(thrower, e)).collect(Collectors.toList());
			nearby.removeAll(nearbyAllies);
			if (!nearby.isEmpty()) {


				if (!world.isRemote  && generation > 0) {
					this.generation--;
					EntityChaosOrb orb = new EntityChaosOrb(world);
					orb.setPosition(this.posX, this.posY, this.posZ);
					orb.thrower = this.thrower;
					orb.motionX = (this.motionX);
					orb.motionY = (this.motionY);
					orb.motionZ = (this.motionZ);
					orb.generation = this.generation;
					world.spawnEntity(orb);
				}

				EntityLivingBase target = nearby.get(0);
				if (nearby.size() > 1 && rand.nextBoolean()) {
					target = nearby.get(rand.nextInt(nearby.size()));
				}
				double d0 = target.posX - this.posX;
				double d1 = target.getEntityBoundingBox().minY + (double)(nearby.get(0).getEyeHeight() / 2.5F) - this.posY;
				double d2 = target.posZ - this.posZ;
				double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);
				if (redirections < 1) {
					this.shoot(d0, d1 + d3 * 0.20000000298023224D, d2, 0.7f, 0);
					redirections++;
				}
			}

		}
	}

	@Override
	protected void onImpact(RayTraceResult rayTrace) {

		Entity entityHit = rayTrace.entityHit;

		if (entityHit != null) {

			float damage = Spells.homing_spark.getProperty(Spell.DAMAGE).floatValue() * damageMultiplier;
			entityHit.attackEntityFrom(MagicDamage.causeIndirectMagicDamage(this, this.getThrower(),
					DamageType.SHOCK), damage);

		}

		this.playSound(WizardrySounds.ENTITY_HOMING_SPARK_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));

		// Particle effect
		if (world.isRemote) {
			for (int i = 0; i < 8; i++) {
				double x = this.posX + rand.nextDouble() - 0.5;
				double y = this.posY + this.height / 2 + rand.nextDouble() - 0.5;
				double z = this.posZ + rand.nextDouble() - 0.5;
				ParticleBuilder.create(Type.SPARK).pos(x, y, z).clr(0xfc0303).spawn(world);
			}
		}

		this.setDead();
	}

	@Override
	public float getSeekingStrength() {
		return 0f;
	}

	@Override
	public int getLifetime() {
		return 12;
	}

	@Override
	public boolean hasNoGravity() {
		return true;
	}

	@Override
	public boolean canRenderOnFire() {
		return false;
	}

	public void setGeneration(int generation) {
		this.generation = generation;
	}
}
