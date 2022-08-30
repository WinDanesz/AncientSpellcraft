package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASTabs;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class BlockUnsealedStone extends Block {

	public BlockUnsealedStone() {
		super(Material.ROCK, MapColor.STONE);
		this.setSoundType(SoundType.STONE);
		this.setCreativeTab(ASTabs.ANCIENTSPELLCRAFT);
		this.setHardness(3.0F);
		this.setResistance(5.0F);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(ASBlocks.unsealed_stone);
	}

	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
	{
		return new ItemStack(this);
	}
}
