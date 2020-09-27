package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.util.ASUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public class ItemGoldBag extends ItemDailyArtefact {
	public static final int MIN_GOLD_AMOUNT = 3;
	public static final int MAX_GOLD_AMOUNT = 8;

	public ItemGoldBag(EnumRarity rarity, Type type) {
		super(rarity, type);
	}

	@Override
	public void performAction(EntityPlayer player) {
		ItemStack stack = new ItemStack(Items.GOLD_NUGGET, ASUtils.randIntBetween(MIN_GOLD_AMOUNT, MAX_GOLD_AMOUNT));
		if (!player.inventory.addItemStackToInventory(stack)) {
			player.dropItem(stack, false);
		}
	}
}
