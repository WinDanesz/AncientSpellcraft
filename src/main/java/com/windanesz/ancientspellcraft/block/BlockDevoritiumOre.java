package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.constants.AWConstants;
import com.windanesz.ancientspellcraft.material.IDevoritium;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASTabs;
import net.minecraft.block.BlockOre;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class BlockDevoritiumOre extends BlockOre implements IDevoritium {

	public BlockDevoritiumOre() {
		setSoundType(SoundType.STONE);
		setCreativeTab(ASTabs.ANCIENTSPELLCRAFT);
		setHardness(AWConstants.DEVORITIUM_BLOCK_HARDNESS);
		setResistance(AWConstants.DEVORITIUM_BLOCK_RESISTANCE);
		setHarvestLevel(AWConstants.DEVORITIUM_HARVEST_TOOL, AWConstants.DEVORITIUM_HARVEST_LEVEL);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(ASBlocks.DEVORITIUM_ORE);
	}

	@Override
	public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
		onEntityWalkDelegate(worldIn, pos, entityIn);
	}

	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entity) {
		onEntityCollisionDelegate(worldIn, pos, state, entity);
	}
}

