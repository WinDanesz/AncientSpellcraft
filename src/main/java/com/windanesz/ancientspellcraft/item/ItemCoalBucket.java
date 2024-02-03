package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.util.ASUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public class ItemCoalBucket extends ItemDailyArtefact {
	public static int MIN_COAL_AMOUNT = 5;
	public static int MAX_COAL_AMOUNT = 10;

	public ItemCoalBucket(EnumRarity rarity) {
		super(rarity);
		addReadinessPropertyOverride();
	}

	@Override
	public void performAction(EntityPlayer player) {
		if (!player.world.isRemote) {

		ItemStack stack = new ItemStack(Items.COAL, ASUtils.randIntBetween(MIN_COAL_AMOUNT, MAX_COAL_AMOUNT));
			if (!player.inventory.addItemStackToInventory(stack)) {
				player.dropItem(stack, false);
			}
		}
	}
}
