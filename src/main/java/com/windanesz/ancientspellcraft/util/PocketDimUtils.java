package com.windanesz.ancientspellcraft.util;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftBlocks;
import electroblob.wizardry.data.WizardData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

import static com.windanesz.ancientspellcraft.spell.PocketDimension.POCKET_DIM_LOCATION;

public class PocketDimUtils {

	private PocketDimUtils() {}

	public static void onPlayerEnteredPocketDim(EntityPlayerMP player, World pocketWorld) {

		WizardData data = WizardData.get(player);
		if (data != null) {

			NBTTagCompound compound = data.getVariable(POCKET_DIM_LOCATION);
			if (compound != null) {

				BlockPos pocketLocation = NBTUtil.getPosFromTag(compound);
				player.setPositionAndUpdate(pocketLocation.getX(), pocketLocation.getY() + 3, pocketLocation.getZ());


			} else {
				BlockPos pocketLocation = findSuitablePocketPos(pocketWorld);

				createPocket(pocketLocation, pocketWorld);

				player.setPosition(pocketLocation.getX(), pocketLocation.getY() + 3, pocketLocation.getZ());
				setPocketDimLocation(data, pocketLocation);
				if(!pocketWorld.isRemote) data.sync();

			}


		}
	}

	public static void setPocketDimLocation(WizardData data, BlockPos pocketLocation) {
		NBTTagCompound nbt = NBTUtil.createPosTag(pocketLocation.offset(EnumFacing.UP));
		data.setVariable(POCKET_DIM_LOCATION, nbt);
	}

	public static void onPlayerLeftPocketDim(EntityPlayerMP player, World pocketWorld) {

	}

	public static void createPocket(BlockPos pos, World pocketWorld) {
		createPlatform(pos, pocketWorld);
		createWalls(pos, pocketWorld);
		createPlatform(pos.offset(EnumFacing.UP, 11), pocketWorld); // roof
		pocketWorld.setBlockState(pos, AncientSpellcraftBlocks.DIMENSION_FOCUS.getDefaultState());
	}

	private static BlockPos findSuitablePocketPos(World pocketWorld) {
		BlockPos currPos = new BlockPos(0, 1, 0);

		while (!(isSuitablePosition(currPos, pocketWorld))) {
			currPos = getRandomLocationWithOffset(currPos);
		}
		return currPos;
	}

	private static boolean isSuitablePosition(BlockPos pos, World pocketWorld) {
		boolean isFree = true;
		for (BlockPos currPos : BlockPos.getAllInBox(
				pos.offset(EnumFacing.WEST, 250).offset(EnumFacing.SOUTH, 250).offset(EnumFacing.DOWN),
				pos.offset(EnumFacing.EAST, 250).offset(EnumFacing.NORTH, 250).offset(EnumFacing.UP))) {

			if (!(pocketWorld.isAirBlock(currPos))) {
				isFree = false;
				break;
			}
		}
		return isFree;
	}

	private static BlockPos getRandomLocationWithOffset(BlockPos origPos) {
		Random rand = new Random();

		int i = rand.nextBoolean() ? 1 : -1;

		EnumFacing facing = rand.nextBoolean() ? EnumFacing.NORTH : rand.nextBoolean() ? EnumFacing.SOUTH : rand.nextBoolean() ? EnumFacing.EAST : EnumFacing.WEST;
		return origPos.offset(facing, 300 * i);
	}

	private static void createPlatform(BlockPos pos, World pocketWorld) {
		for (BlockPos currPos : BlockPos.getAllInBox(
				pos.offset(EnumFacing.SOUTH, 6).offset(EnumFacing.WEST, 6),
				pos.offset(EnumFacing.NORTH, 6).offset(EnumFacing.EAST, 6))) {
			pocketWorld.setBlockState(currPos, AncientSpellcraftBlocks.DIMENSION_BOUNDARY.getDefaultState());
		}

	}

	private static void createWalls(BlockPos center, World pocketWorld) {
		for (BlockPos currPos : BlockPos.getAllInBox(
				center.offset(EnumFacing.SOUTH, 6).offset(EnumFacing.WEST, 6),
				center.offset(EnumFacing.NORTH, 6).offset(EnumFacing.WEST, 6).offset(EnumFacing.UP, 10))) {
			pocketWorld.setBlockState(currPos, AncientSpellcraftBlocks.DIMENSION_BOUNDARY.getDefaultState());
		}

		for (BlockPos currPos : BlockPos.getAllInBox(
				center.offset(EnumFacing.SOUTH, 6).offset(EnumFacing.EAST, 6),
				center.offset(EnumFacing.NORTH, 6).offset(EnumFacing.EAST, 6).offset(EnumFacing.UP, 10))) {
			pocketWorld.setBlockState(currPos, AncientSpellcraftBlocks.DIMENSION_BOUNDARY.getDefaultState());
		}

		for (BlockPos currPos : BlockPos.getAllInBox(
				center.offset(EnumFacing.SOUTH, 6).offset(EnumFacing.EAST, -6),
				center.offset(EnumFacing.SOUTH, 6).offset(EnumFacing.EAST, 6).offset(EnumFacing.UP, 10))) {
			pocketWorld.setBlockState(currPos, AncientSpellcraftBlocks.DIMENSION_BOUNDARY.getDefaultState());
		}

		for (BlockPos currPos : BlockPos.getAllInBox(
				center.offset(EnumFacing.NORTH, 6).offset(EnumFacing.WEST, -6),
				center.offset(EnumFacing.NORTH, 6).offset(EnumFacing.WEST, 6).offset(EnumFacing.UP, 10))) {
			pocketWorld.setBlockState(currPos, AncientSpellcraftBlocks.DIMENSION_BOUNDARY.getDefaultState());
		}
	}
}
