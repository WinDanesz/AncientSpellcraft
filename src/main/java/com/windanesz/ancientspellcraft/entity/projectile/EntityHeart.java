package com.windanesz.ancientspellcraft.entity.projectile;

import com.windanesz.ancientspellcraft.entity.living.EntitySkeletonMageMinion;
import com.windanesz.ancientspellcraft.registry.ASSounds;
import com.windanesz.ancientspellcraft.registry.ASSpells;
import electroblob.wizardry.entity.projectile.EntityMagicProjectile;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.MagicDamage.DamageType;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import static electroblob.wizardry.spell.Spell.HEALTH;

public class EntityHeart extends EntityMagicProjectile {
	protected float damage = 1;

	public EntityHeart(World world) {
		super(world);
		this.setSize(0.5f, 0.5f);
	}

	@Override
	public float getSeekingStrength(){
		return ASSpells.healing_heart.getProperty(Spell.SEEKING_STRENGTH).floatValue();
	}

	public void setDamage(float damage) {
		this.damage = damage;
	}

	public float getDamage() {
		return ASSpells.healing_heart.getProperty(Spell.DAMAGE).floatValue();
	}

	@Override
	protected void onImpact(RayTraceResult rayTrace) {
		if (world.isRemote) {ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(posX, posY, posZ).clr(250, 63, 63).spawn(world);}

		if (!world.isRemote) {
			Entity entityHit = rayTrace.entityHit;

			if (entityHit != null && entityHit instanceof EntityLivingBase && !(entityHit instanceof EntitySkeletonMageMinion)) {
				EntityLivingBase entityLivingBase = (EntityLivingBase) entityHit;

				if (entityLivingBase.isEntityUndead()) {
					entityLivingBase.attackEntityFrom(
							MagicDamage.causeIndirectMagicDamage(this, this.getThrower(), DamageType.MAGIC).setProjectile(),
							getDamage());
					playSound(ASSounds.ENTITY_HEALING_HEALTH_DAMAGES, 0.9F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
					if (world.isRemote)
						ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(posX, posY, posZ).clr(250, 63, 63).spawn(world);

				} else if (EntityUtils.isLiving(entityLivingBase)) {

					if (entityLivingBase.getHealth() < entityLivingBase.getMaxHealth() && entityLivingBase.getHealth() > 0) {

						entityLivingBase.heal(ASSpells.healing_heart.getProperty(HEALTH).floatValue());
						if (world.isRemote)
							ParticleBuilder.spawnHealParticles(world, entityLivingBase);
						playSound(ASSounds.ENTITY_HEALING_HEATH_HEALS, 0.9F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
						if (world.isRemote)
							ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(posX, posY, posZ).clr(250, 63, 63).spawn(world);
					}
				}
			} else {
				playSound(ASSounds.ENTITY_HEALING_HEALTH_DAMAGES, 0.9F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
				if (this.world.isRemote)
					ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(posX, posY, posZ).clr(250, 63, 63).spawn(world);
			}
		}
		this.setDead();
	}

	@Override
	public void onUpdate() {

		super.onUpdate();

		if (world.isRemote) {

			if (this.ticksExisted > 1) { // Don't spawn particles behind where it started!
				double x = posX - motionX / 2;
				double y = posY - motionY / 2;
				double z = posZ - motionZ / 2;
				ParticleBuilder.create(ParticleBuilder.Type.SPARKLE, rand, x, y, z, 0.03, true).clr(250, 63, 63).fade(0.7f, 0, 1)
						.time(20 + rand.nextInt(10)).spawn(world);
			}
		}
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public float getCollisionBorderSize() {
		return 1.0F;
	}

	@Override
	public boolean hasNoGravity() {
		return true;
	}

	@Override
	public boolean canRenderOnFire() {
		return false;
	}

	@Override
	public int getLifetime() {
		return 20;
	}
}
