package com.windanesz.ancientspellcraft.entity.living;

import electroblob.wizardry.entity.living.ISummonedCreature;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;
/**
 * UNUSED
 * **/
public class EntitySkeletonMage extends AbstractSkeleton implements ISummonedCreature {
	public EntitySkeletonMage(World worldIn) {
		super(worldIn);
	}

	@Override
	protected SoundEvent getStepSound() {
		return null;
	}

	@Override
	public void setLifetime(int i) {

	}

	@Override
	public int getLifetime() {
		return 0;
	}

	@Override
	public void setOwnerId(UUID uuid) {

	}

//	@Nullable
//	@Override
//	public UUID func_184753_b() {
//		return null;
//	}

	@Override
	public void onSpawn() {

	}

	@Override
	public void onDespawn() {

	}

	@Override
	public boolean hasParticleEffect() {
		return false;
	}

	@Nullable
	@Override
	public UUID getOwnerId() {
		return null;
	}

	@Nullable
	@Override
	public Entity getOwner() {
		return null;
	}
}
