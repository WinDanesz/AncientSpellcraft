package com.windanesz.ancientspellcraft.tileentity;

import electroblob.wizardry.tileentity.TileEntityPlayerSave;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ITickable;

public class TileEntityRevertingBlock extends TileEntityPlayerSave implements ITickable {
	private int ticksExisted = 0;
	private int lifetime;

	private IBlockState oldState;

	public TileEntityRevertingBlock() {
		this.lifetime = 600;

	}

	@Override
	public void update() {
		ticksExisted++;

		if (ticksExisted > 5 && ticksExisted > lifetime && !this.world.isRemote && oldState != null) {
			world.setBlockState(this.pos, oldState);
			world.removeTileEntity(pos);
		}
	}

	public void setLifetime(int lifetime) {
		this.lifetime = lifetime;
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		ticksExisted = tagCompound.getInteger("timer");
		lifetime = tagCompound.getInteger("maxTimer"); // Left as maxTimer for backwards compatibility
		if (tagCompound.hasKey("old_state")) {
			oldState = NBTUtil.readBlockState(tagCompound.getCompoundTag("old_state"));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setInteger("timer", ticksExisted);
		tagCompound.setInteger("maxTimer", lifetime);
		if (oldState != null) {
			tagCompound.setTag("old_state", NBTUtil.writeBlockState(new NBTTagCompound(), oldState));
		}

		return super.writeToNBT(tagCompound);
	}

	public void setOldState(IBlockState oldState) {
		this.oldState = oldState;
	}
}
