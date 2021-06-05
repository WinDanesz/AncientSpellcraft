package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.constants.AWConstants;
import com.windanesz.ancientspellcraft.material.IDevoritium;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftTabs;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockDevoritium extends Block implements IDevoritium {

	public BlockDevoritium() {
		super(AncientSpellcraft.DEVORITIUM);
		setHardness(AWConstants.DEVORITIUM_BLOCK_HARDNESS);
		setResistance(AWConstants.DEVORITIUM_BLOCK_RESISTANCE);
		setSoundType(SoundType.METAL);
		setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT);
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

