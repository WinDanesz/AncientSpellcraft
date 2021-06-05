package com.windanesz.ancientspellcraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockUsedRune extends Block {
	AxisAlignedBB AABB = new AxisAlignedBB(2 / 16F, 0, 2 / 16F, 14 / 16F, 1 / 16F, 14 / 16F);

	public BlockUsedRune(Material blockMaterialIn, MapColor blockMapColorIn) {
		super(blockMaterialIn, blockMapColorIn);
		this.setTickRandomly(true);
	}

	@Override
	public BlockRenderLayer getRenderLayer() { return BlockRenderLayer.CUTOUT; }

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) { return false; }

	@Override
	public boolean isFullCube(IBlockState state) { return false;}

	@Override
	public boolean isOpaqueCube(IBlockState state) { return false; }

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) { return AABB; }

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return side == EnumFacing.DOWN;
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if (world.rand.nextInt(10) == 0) {
			world.setBlockToAir(pos);
		}
	}

//	/**
//	 * Whether this Block can be replaced directly by other blocks (true for e.g. tall grass)
//	 */
//	public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos)
//	{
//		return true;
//	}

}
