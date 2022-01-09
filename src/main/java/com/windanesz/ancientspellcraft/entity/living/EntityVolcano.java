package com.windanesz.ancientspellcraft.entity.living;

import com.windanesz.ancientspellcraft.entity.living.EntityFireAnt;
import electroblob.wizardry.entity.living.EntitySummonedCreature;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import java.util.UUID;

public class EntityVolcano extends EntitySummonedCreature {

	// Field implementations
	private int lifetime = -1;
	private UUID casterUUID;

	public EntityVolcano(World world) {
		super(world);
		this.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1D);
		this.isImmuneToFire = true;
	}


	@Override
	public boolean hasRangedAttack() {
		return false;
	}

	/**
	 * Returns true if this entity should push and be pushed by other entities when colliding.
	 */
	public boolean canBePushed() {
		return false;
	}

	protected void collideWithEntity(Entity entityIn) { }

	protected void collideWithNearbyEntities() {}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (this.rand.nextInt(4) == 0) {
			this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX, this.posY + 1.3D, this.posZ, 0.0D, 0.0D, 0.0D);
		}

		if (this.world.isRemote) {
			// horizontal particle, always visible
			ParticleBuilder.create(ParticleBuilder.Type.FLASH)
					.pos(this.posX, this.posY + 0.1, this.posZ)
					.face(EnumFacing.UP)
					.clr(252, 105, 0)
					.collide(false)
					.scale(4.3F)
					.time(10)
					.spawn(world);
		}

		for (int i = 0; i < 2; i++) {
			this.world.spawnParticle(EnumParticleTypes.FLAME,
					this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width,
					this.posY + this.height / 2 + this.rand.nextDouble() * (double) this.height / 2,
					this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width, 0.0D, -0.1D, 0.0D);
		}

		boolean f = false;
		if (this.rand.nextInt(60) == 0) {
			f = true;

			if (!world.isRemote) {
				EntityFireAnt minion = new EntityFireAnt(world);

				// In this case we don't care whether the minions can fly or not.
				minion.setPosition(this.posX, this.posY +1, this.posZ);
				minion.setLifetime(300);
				minion.setCaster(this);
				minion.setOrigCaster(this.getCaster());
				world.spawnEntity(minion);
				minion.addVelocity(this.rand.nextFloat() * 0.3 *(this.rand.nextBoolean() ? -1 : 1), this.rand.nextFloat() * 0.3, this.rand.nextFloat() * (this.rand.nextBoolean() ? -1 : 1) * 0.3);
			}
		}

		if (world.isRemote && (f || this.rand.nextInt(60) == 0)) {
			for (int i = 0; i < 8; i++) {
				world.spawnParticle(EnumParticleTypes.LAVA, this.posX + rand.nextFloat() - 0.5,
						this.posY + this.height / 2 + rand.nextFloat() - 0.5, this.posZ + rand.nextFloat() - 0.5, 0, 0, 0);
			}
		}

	}

	/**
	 * Called when a lightning bolt hits the entity.
	 */
	public void onStruckByLightning(EntityLightningBolt lightningBolt) {
	}

	public EnumPushReaction getPushReaction() {
		return EnumPushReaction.IGNORE;
	}

	@Override
	public boolean hasParticleEffect() {
		return false;
	}

	@Override
	public void onSpawn() {

	}

	@Override
	public void onDespawn() {

	}
}
