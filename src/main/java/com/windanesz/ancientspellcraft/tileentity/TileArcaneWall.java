package com.windanesz.ancientspellcraft.tileentity;

import com.windanesz.ancientspellcraft.registry.ASBlocks;
import electroblob.wizardry.tileentity.TileEntityPlayerSave;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileArcaneWall extends TileEntityPlayerSave implements ITickable {

	public void setGenerated(boolean generated) {
		this.generated = generated;
	}

	public boolean isGenerated() {
		return generated;
	}

	/**
	 * Determines if this wall is a naturally generated one, in worldgen structures. Such walls can only be dispelled by the unseal scroll.
	 */
	private boolean generated = false;
	private boolean isBeingDispelled = false;

	public TileArcaneWall() { }

	public boolean isBeingDispelled() {
		return isBeingDispelled;
	}

	public void setBeingDispelled(boolean beingDispelled) {
		isBeingDispelled = beingDispelled;
		markDirty();
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		if (tagCompound.hasKey("generated")) {
			generated = tagCompound.getBoolean("generated");
		}
		if (tagCompound.hasKey("being_dispelled")) {
			isBeingDispelled = tagCompound.getBoolean("being_dispelled");
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
		tagCompound.setBoolean("generated", generated);
		tagCompound.setBoolean("being_dispelled", isBeingDispelled);
		return super.writeToNBT(tagCompound);
	}

	@Override
	public void update() {
		if (!world.isRemote && isBeingDispelled) {
			for (EnumFacing direction : EnumFacing.VALUES) {
				IBlockState blockState = this.world.getBlockState(pos.offset(direction));

				if (blockState.getBlock() == ASBlocks.arcane_wall) {
					TileEntity tile = this.world.getTileEntity(pos.offset(direction));
					if (tile instanceof TileArcaneWall && ((TileArcaneWall) tile).isGenerated()) {
						((TileArcaneWall) tile).setBeingDispelled(true);
					}
				}
			}

			world.removeTileEntity(this.pos);
			world.setBlockToAir(this.pos);
		}
	}


}