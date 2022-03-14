package com.windanesz.ancientspellcraft.tileentity;

import com.windanesz.ancientspellcraft.registry.ASBlocks;
import electroblob.wizardry.tileentity.TileEntityPlayerSave;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;

public class TileEntityLightningBlock extends TileEntityPlayerSave implements ITickable {

	private int ticksExisted = 0;
	private int lifetime;
	private int age;

	public float damage = 1;

	public TileEntityLightningBlock(){
		this.lifetime = 600;
	}

	@Override
	public void update(){

		if (world.isRemote) {

			float flag = 1.0f;
			boolean halfblock = world.getBlockState(pos.down()).getBlock() != ASBlocks.lightning_block && world.getBlockState(pos.up()).getBlock() != ASBlocks.lightning_block;
			if (halfblock) flag = 0.5f;
			for (int i = 0; i < 4; i++) {
				ParticleBuilder.create(ParticleBuilder.Type.SPARK)
						.pos(pos.getX() + world.rand.nextDouble(), pos.getY() + world.rand.nextDouble() * flag, pos.getZ() + world.rand.nextDouble())
						.time(5)
						.spawn(world);
			}
		}
		ticksExisted++;

		if(ticksExisted > lifetime && !this.world.isRemote){
			this.world.setBlockToAir(pos);
		}
	}

	public int getAge(){
		return age;
	}

	public TileEntityLightningBlock setDamage(float damage) {
		this.damage = damage;
		return this;
	}

	public TileEntityLightningBlock setLifetime(int lifetime){
		this.lifetime = lifetime;
		return this;
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound){
		super.readFromNBT(tagCompound);
		ticksExisted = tagCompound.getInteger("timer");
		lifetime = tagCompound.getInteger("maxTimer"); // Left as maxTimer for backwards compatibility
		damage = tagCompound.getFloat("damageMultiplier");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagCompound){
		super.writeToNBT(tagCompound);
		tagCompound.setInteger("timer", ticksExisted);
		tagCompound.setInteger("maxTimer", lifetime);
		tagCompound.setFloat("damageMultiplier", damage);
		return tagCompound;
	}

}
