package com.windanesz.ancientspellcraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockConjuredDirt extends Block implements ITileEntityProvider, ITemporaryBlock {

	public BlockConjuredDirt() {
		super(Material.GROUND);
		//setTemporaryBlockProperties(this);
	}

	//////////////// ITemporaryBlock Interface implementation ////////////////

	@Override
	public boolean isToolEffective(String type, IBlockState state) { return false; }

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) { return getItemDroppedDelegate(state, rand, fortune); }

	@Override
	protected boolean canSilkHarvest() {
		return false;
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
		harvestBlockDelegate(worldIn, player, pos, state, te, stack);
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(World world, int meta) { return createNewTileEntityDelegate(world, meta); }
}
