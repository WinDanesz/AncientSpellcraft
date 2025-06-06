package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.tileentity.TileEntityArcaneFlame;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class ItemArcaneFlameRing extends ItemASArtefact implements ITickableArtefact {
	public ItemArcaneFlameRing(EnumRarity rarity, Type type) {
		super(rarity, type);
	}

	public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
		if (!player.world.isRemote && itemRand.nextBoolean() && player.ticksExisted % 5 == 0) {
			// Replace fire blocks with arcane flame blocks in a 20-block radius
			replaceFireBlocksWithArcaneFlame(player);
		}
	}

	private void replaceFireBlocksWithArcaneFlame(EntityLivingBase player) {
		int radius = 20;
		BlockPos playerPos = player.getPosition();

		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius; y <= radius; y++) {
				for (int z = -radius; z <= radius; z++) {
					BlockPos pos = playerPos.add(x, y, z);

					// Check if block is within spherical radius
					if (playerPos.distanceSq(pos) <= radius * radius) {
						if (player.world.getBlockState(pos).getBlock() == Blocks.FIRE) {
							player.world.setBlockState(pos, ASBlocks.ARCANE_FLAME.getDefaultState());

							// Get the tile entity and configure it
							TileEntity tileEntity = player.world.getTileEntity(pos);
							if (tileEntity instanceof TileEntityArcaneFlame) {
								TileEntityArcaneFlame arcaneFlame = (TileEntityArcaneFlame) tileEntity;
								// Configure the flame properties
								int lifetime = (200);
								arcaneFlame.setLifetime(lifetime);
								arcaneFlame.setCaster(player);
							}
						}
					}
				}
			}
		}
	}
}
