package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.spell.PocketDimension;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class BlockDimensionFocus extends Block {

	public BlockDimensionFocus() {
		super(Material.ROCK, MapColor.OBSIDIAN);
		this.setBlockUnbreakable();
		this.setResistance(6000001.0F);
		this.setHardness(6000001.0F);
		this.disableStats();
		setLightLevel(0.8F);
	}

	/**
	 * Get the Item that this Block should drop when harvested.
	 */
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(Blocks.STONE);
	}


	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return PocketDimension.teleportPlayer(playerIn);
	}
}
