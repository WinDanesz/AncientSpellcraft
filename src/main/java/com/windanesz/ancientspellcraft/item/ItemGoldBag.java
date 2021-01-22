package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.util.ASUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public class ItemGoldBag extends ItemDailyArtefact {
	private static final int MIN_GOLD_AMOUNT = 2;
	private static final int MAX_GOLD_AMOUNT = 8;

	public ItemGoldBag(EnumRarity rarity) {
		super(rarity);
	}

	@Override
	public void performAction(EntityPlayer player) {
		if (!player.world.isRemote) {
			ItemStack stack = new ItemStack(Items.GOLD_NUGGET, ASUtils.randIntBetween(MIN_GOLD_AMOUNT, MAX_GOLD_AMOUNT));
			if (!player.inventory.addItemStackToInventory(stack)) {
				player.dropItem(stack, false);
			}
		}
	}
}
