package com.windanesz.ancientspellcraft.tileentity;

import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASPotions;
import electroblob.wizardry.item.ItemArtefact;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileMageLight extends TileEntity implements ITickable {

	public TileMageLight() {}

	private int lifeTime = 0;
	private boolean extended = false;

	public EntityPlayer player = null;

	public void setPlayer(EntityPlayer player) {
		this.player = player;
	}

	/**
	 * This controls whether the tile entity gets replaced whenever the block state
	 * is changed. Normally only want this when block actually is replaced.
	 */
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return (oldState.getBlock() != newSate.getBlock());
	}

	@Override
	public void update() {

		if (world.getBlockState(pos.down()).getBlock() == ASBlocks.PLACED_RUNE) {
			return;
		}
		// check if player has moved away from the tile entity
		EntityPlayer thePlayer = world.getClosestPlayer(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5, 4.0D, false);

		if (thePlayer == null) {
			if (world.getBlockState(getPos()).getBlock() ==
					ASBlocks.MAGELIGHT) {
				world.setBlockToAir(this.getPos());
			}
		} else if (!thePlayer.isPotionActive(ASPotions.magelight) && !ItemArtefact.isArtefactActive(thePlayer, ASItems.charm_glyph_illumination)
				&& !ItemArtefact.isArtefactActive(thePlayer, ASItems.charm_magic_light)) {
			if (world.getBlockState(getPos()).getBlock() == ASBlocks.MAGELIGHT) {
				world.setBlockToAir(getPos());
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger("lifetime", lifeTime);
		compound.setBoolean("extended", extended);
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		lifeTime = compound.getInteger("lifetime");
		extended = compound.getBoolean("extended");
		super.readFromNBT(compound);
	}

	public void setLifeTime(int i) {
		lifeTime = i;
	}
}
	