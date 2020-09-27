package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.util.ASUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public class ItemCoalBucket extends ItemDailyArtefact {
	public static int MIN_COAL_AMOUNT = 2;
	public static int MAX_COAL_AMOUNT = 4;

	public ItemCoalBucket(EnumRarity rarity, Type type) {
		super(rarity, type);
	}

	@Override
	public void performAction(EntityPlayer player) {
		ItemStack stack = new ItemStack(Items.COAL, ASUtils.randIntBetween(MIN_COAL_AMOUNT, MAX_COAL_AMOUNT));
		if (!player.inventory.addItemStackToInventory(stack)) {
			player.dropItem(stack, false);
		}
	}
}
