package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.registry.ASBlocks;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public class ItemGlyphOfIllumination extends ItemGlyphArtefact implements ITickableArtefact {

	public ItemGlyphOfIllumination(EnumRarity rarity, Type type) {
		super(rarity, type);
	}

	public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
		if (player.getHeldItemMainhand().getItem() instanceof ItemBattlemageSword && player.world.isAirBlock(player.getPosition().up())) {
			player.world.setBlockState(player.getPosition().up(), ASBlocks.MAGELIGHT.getDefaultState());
		}
	}
}
