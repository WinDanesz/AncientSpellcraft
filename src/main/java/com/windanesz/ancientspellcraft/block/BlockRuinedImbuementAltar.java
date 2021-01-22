package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.registry.WizardryItems;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.Random;

public class BlockRuinedImbuementAltar extends Block {

	private static AxisAlignedBB AABB = new AxisAlignedBB(0, 0, 0, 1D, 0.75D, 1D);
	public BlockRuinedImbuementAltar(Material materialIn) {
		super(materialIn);
		setHardness(3.0F);
		setResistance(5.0F);
		setSoundType(SoundType.STONE);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return WizardryItems.magic_crystal;
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {

		drops.add(new ItemStack(WizardryItems.crystal_shard, ASUtils.randIntBetween(1, 3)));
		drops.add(new ItemStack(Item.getItemFromBlock(Blocks.STONE), 1, 0));

		super.getDrops(drops, world, pos, state, fortune);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB; //a
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}
}
