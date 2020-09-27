package com.windanesz.ancientspellcraft.tileentity;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftBlocks;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileCandleLight extends TileEntity implements ITickable {
	public TileCandleLight() {}

	/**
	 * This controls whether the tile entity gets replaced whenever the block state
	 * is changed. Normally only want this when block actually is replaced.
	 */
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return (oldState.getBlock() != newSate.getBlock());
	}

	@Override
	public void update() {
		// check if player has moved away from the tile entity
		EntityPlayer thePlayer = world.getClosestPlayer(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5, 2.0D, false);

		if (thePlayer == null) {
			if (world.getBlockState(getPos()).getBlock() ==
					AncientSpellcraftBlocks.CANDLELIGHT) {
				world.setBlockToAir(this.getPos());
			}
		} else if (!thePlayer.isPotionActive(AncientSpellcraftPotions.candlelight)) {
			if (world.getBlockState(getPos()).getBlock() == AncientSpellcraftBlocks.CANDLELIGHT) {
				world.setBlockToAir(getPos());
			}
		}
	}
}