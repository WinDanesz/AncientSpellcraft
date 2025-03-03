package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.registry.ASBlocks;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public class ItemGlyphOfIllumination extends ItemGlyphArtefact implements ITickableArtefact {

	public ItemGlyphOfIllumination(EnumRarity rarity, Type type) {
		super(rarity, type);
	}

	@Override
	public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
		if (!player.world.isRemote && shouldPlaceMagelight(player)) {
			placeMagelightIfNeeded(player);
		}
	}

	private boolean shouldPlaceMagelight(EntityLivingBase player) {
		boolean isHoldingRequiredItem = isHoldingBattlemageWeapon(player);
		boolean isAboveAir = player.world.isAirBlock(player.getPosition().up());
		return isHoldingRequiredItem && isAboveAir;
	}

	private boolean isHoldingBattlemageWeapon(EntityLivingBase player) {
		return player.getHeldItemOffhand().getItem() instanceof ItemBattlemageShield
				|| player.getHeldItemMainhand().getItem() instanceof ItemBattlemageSword;
	}

	private void placeMagelightIfNeeded(EntityLivingBase player) {
		if (!player.world.getBlockState(player.getPosition().up()).equals(ASBlocks.MAGELIGHT.getDefaultState())) {
			player.world.setBlockState(player.getPosition().up(), ASBlocks.MAGELIGHT.getDefaultState());
		}
	}
}
