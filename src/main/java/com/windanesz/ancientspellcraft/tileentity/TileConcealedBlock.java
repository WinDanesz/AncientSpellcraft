package com.windanesz.ancientspellcraft.tileentity;

import electroblob.wizardry.tileentity.TileEntityPlayerSave;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileConcealedBlock extends TileEntityPlayerSave implements ITickable {

	private IBlockState oldState = null;
	private TileEntity tile = null;

	public TileConcealedBlock() { }

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		if (tagCompound.hasKey("old_state")) {
			oldState = NBTUtil.readBlockState(tagCompound.getCompoundTag("old_state"));
		}
		if (tagCompound.hasKey("tile_data")) {
			tile = TileEntity.create(world, tagCompound.getCompoundTag("tile_data"));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
		tagCompound = super.writeToNBT(tagCompound);
		if (oldState != null) {
			tagCompound.setTag("old_state", NBTUtil.writeBlockState(new NBTTagCompound(), oldState));
		}
		if (tile != null) {
			tagCompound.setTag("tile_data", tile.writeToNBT(new NBTTagCompound()));
		}
		return tagCompound;
	}

	public void revert() {
		if (!world.isRemote) {
			if (oldState != null) {
				world.setBlockState(this.getPos(), oldState);
			}
			if (tile != null) {
				world.setTileEntity(this.getPos(), tile);
			}
		}
	}

	public void store(NBTTagCompound nbt) {
		if (nbt.hasKey("old_state")) {
			oldState = NBTUtil.readBlockState(nbt.getCompoundTag("old_state"));
		}
		if (nbt.hasKey("tile_data")) {
			tile = TileEntity.create(world, nbt.getCompoundTag("tile_data"));
		}
		markDirty();
	}

	@Override
	public void update() {
	}
}
