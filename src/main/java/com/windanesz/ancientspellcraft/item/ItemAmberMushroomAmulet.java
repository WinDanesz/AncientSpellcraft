package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.block.BlockMagicMushroom;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.util.BlockUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class ItemAmberMushroomAmulet extends ItemASArtefact implements ITickableArtefact {
	public ItemAmberMushroomAmulet(EnumRarity rarity, Type type) {
		super(rarity, type);
	}

	public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
		if (!player.world.isRemote && itemRand.nextBoolean() && player.ticksExisted % 120 == 0) {
			if (ASUtils.isInjured(player) && player instanceof EntityPlayer && !((EntityPlayer) player).getCooldownTracker().hasCooldown(this)) {

				for (int i = 0; i < player.world.rand.nextInt(3); i++) {
					BlockPos pos = BlockUtils.findNearbyFloorSpace(player, 7, 7);
					if (pos != null) {
						BlockMagicMushroom.tryPlaceMushroom(player.world, pos, player, (BlockMagicMushroom) ASBlocks.MUSHROOM_HEALING, 700);
						((EntityPlayer) player).getCooldownTracker().setCooldown(this, 900);

					}
				}
			}
		}
	}
}
