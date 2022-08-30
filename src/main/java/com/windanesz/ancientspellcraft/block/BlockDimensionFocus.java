package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.registry.ASDimensions;
import com.windanesz.ancientspellcraft.registry.ASTabs;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
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

import static com.windanesz.ancientspellcraft.spell.PocketDimension.teleportPlayer;

public class BlockDimensionFocus extends Block {

	public BlockDimensionFocus() {
		super(Material.ROCK, MapColor.OBSIDIAN);
		this.setBlockUnbreakable();
		this.setResistance(6000001.0F);
		this.setSoundType(SoundType.STONE);
		this.setCreativeTab(ASTabs.ANCIENTSPELLCRAFT);
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
		return playerIn.dimension == ASDimensions.POCKET_DIM_ID && teleportPlayer(playerIn);
	}
}
