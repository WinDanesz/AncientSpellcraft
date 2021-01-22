package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.constants.AWConstants;
import com.windanesz.ancientspellcraft.material.IDevoritium;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftTabs;
import net.minecraft.block.BlockPane;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockDevoritiumBars extends BlockPane implements IDevoritium {

	public BlockDevoritiumBars() {
		super(AncientSpellcraft.DEVORITIUM, true);
		setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT);
		setHardness(AWConstants.DEVORITIUM_BLOCK_HARDNESS);
		setResistance(AWConstants.DEVORITIUM_BLOCK_RESISTANCE);
		setHarvestLevel(AWConstants.DEVORITIUM_HARVEST_TOOL, AWConstants.DEVORITIUM_HARVEST_LEVEL);
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
