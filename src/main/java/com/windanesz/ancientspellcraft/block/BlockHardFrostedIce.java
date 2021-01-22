package com.windanesz.ancientspellcraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFrostedIce;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Like {@link BlockFrostedIce) or {@link BlockFrostedIce), but melting does not depend on light level or neighbouring blocks, and it just
 * disappears instead of turning to water. Also has a high blast resistance and breaking resistance, and the breaking is more predictable
 * with a 40 ticks for schedules.
 * Used by {@link com.windanesz.ancientspellcraft.spell.Cryostasis}
 */
public class BlockHardFrostedIce extends BlockFrostedIce {

	public BlockHardFrostedIce() {
		super();
		this.setHardness(50.0F);
		this.setResistance(2000.0F);
	}

	@Override
	protected void turnIntoWater(World world, BlockPos pos) {
		world.destroyBlock(pos, false);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		this.slightlyMelt(worldIn, pos, state, rand, false);
	}

	@Override
	protected void slightlyMelt(World world, BlockPos pos, IBlockState state, Random rand, boolean meltNeighbors) {
		{
			int i = ((Integer) state.getValue(AGE)).intValue();

			if (i < 3) {
				world.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(i + 1)), 2);
				world.scheduleUpdate(pos, this, 40);
			} else {
				world.destroyBlock(pos, false);
			}
		}
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		// noop
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
		player.addExhaustion(0.005F);
		worldIn.setBlockToAir(pos);
	}
}
