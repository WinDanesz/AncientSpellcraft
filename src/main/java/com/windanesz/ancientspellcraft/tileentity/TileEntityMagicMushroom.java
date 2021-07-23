package com.windanesz.ancientspellcraft.tileentity;

import electroblob.wizardry.block.BlockThorns;
import electroblob.wizardry.tileentity.TileEntityThorns;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityMagicMushroom extends TileEntityThorns {

	private int ticksExisted = 0;
	private int lifetime;
	private int age;

	public float potency = 1;
	public float durationMultiplier = 1;

	public TileEntityMagicMushroom() {
		this.lifetime = 600;
	}

	@Override
	public void update() {

		ticksExisted++;

		if (ticksExisted > lifetime && !this.world.isRemote) {
			this.world.destroyBlock(pos, false);
		}

		if (ticksExisted % BlockThorns.GROWTH_STAGE_DURATION == 0 && age < BlockThorns.GROWTH_STAGES - 1) {
			age++;
			sync(); // Update displayed block
		}
	}

	public int getAge() {
		return age;
	}

	public void setLifetime(int lifetime) {
		this.lifetime = lifetime;
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		ticksExisted = tagCompound.getInteger("timer");
		lifetime = tagCompound.getInteger("maxTimer"); // Left as maxTimer for backwards compatibility
		age = tagCompound.getInteger("age");
		potency = tagCompound.getFloat("potency");
		potency = tagCompound.getFloat("durationMultiplier");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setInteger("timer", ticksExisted);
		tagCompound.setInteger("maxTimer", lifetime);
		tagCompound.setInteger("age", age);
		tagCompound.setFloat("potency", potency);
		tagCompound.setFloat("durationMultiplier", durationMultiplier);
		return tagCompound;
	}

}
