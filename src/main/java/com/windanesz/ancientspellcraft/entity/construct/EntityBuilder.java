package com.windanesz.ancientspellcraft.entity.construct;

import com.windanesz.ancientspellcraft.block.ITemporaryBlock;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.entity.construct.EntityMagicConstruct;
import electroblob.wizardry.util.BlockUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import static electroblob.wizardry.util.BlockUtils.canBlockBeReplaced;

public class EntityBuilder extends EntityMagicConstruct {

	// the REMAINING blocks to build
	private List<BlockPos> buildList;

	private IBlockState blockToBuild;

	public int buildTickRate = 5;
	public int batchSize = 1;
	public int blockLifetime = 200;
	private boolean ignoreClaims = true;

	public EntityBuilder(World world) {
		super(world);
		this.height = 0.1f;
		this.width = 0.1f;
	}

	public void onUpdate() {

		super.onUpdate();

		if (!this.world.isRemote) {

			if (ticksExisted % buildTickRate == 0 && buildList != null && !buildList.isEmpty()) {

				for (int j = 0; j < batchSize; j++) {

					if (!buildList.isEmpty()) {

						BlockPos currPos = buildList.get(0);

						if (canBlockBeReplaced(world, currPos) && (ignoreClaims || BlockUtils.canPlaceBlock(getCaster(), world, currPos))) {

							if (blockToBuild.getBlock() instanceof ITemporaryBlock) {
								ITemporaryBlock.placeTemporaryBlock(getCaster(), world, blockToBuild.getBlock(), currPos, blockLifetime);
							} else {
								world.setBlockState(currPos, blockToBuild);
								world.scheduleUpdate(currPos.toImmutable(), blockToBuild.getBlock(), blockLifetime);
							}
						}
						buildList.remove(0);
					}
				}

			}
		}
	}

	public void setBlockToBuild(IBlockState blockToBuild) {
		this.blockToBuild = blockToBuild;
	}

	public void setBuildList(List<BlockPos> buildList) {
		this.buildList = buildList;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		buildTickRate = nbttagcompound.getInteger("buildTickRate");
		batchSize = nbttagcompound.getInteger("batchSize");

		if (nbttagcompound.hasKey("posList")) {
			buildList = ASUtils.getBlockPosListFromTag(nbttagcompound.getCompoundTag("posList"));
		}

		if (nbttagcompound.hasKey("blockToBuild")) {
			blockToBuild = NBTUtil.readBlockState(nbttagcompound.getCompoundTag("blockToBuild"));
		}

		if (nbttagcompound.hasKey("ignoreClaims")) {
			ignoreClaims = nbttagcompound.getBoolean("ignoreClaims");
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setInteger("buildTickRate", buildTickRate);
		nbt.setInteger("batchSize", batchSize);

		if (buildList != null) {
			nbt.setTag("posList", ASUtils.writeBlockPosListToTag(buildList));
		}

		if (blockToBuild != null) {
			nbt.setTag("blockToBuild", NBTUtil.writeBlockState(new NBTTagCompound(), blockToBuild));
		}
		nbt.setBoolean("ignoreClaims", ignoreClaims);
		super.writeEntityToNBT(nbt);
	}

	public void setIgnoreClaims(boolean ignoreClaims) {
		this.ignoreClaims = ignoreClaims;
	}
}

