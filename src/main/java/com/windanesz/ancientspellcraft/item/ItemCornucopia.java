package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.util.ASUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public class ItemCornucopia extends ItemDailyArtefact {
	public ItemCornucopia(EnumRarity rarity, Type type) {
		super(rarity, type);
	}

	@Override
	public void performAction(EntityPlayer player) {
		if (!player.world.isRemote) {
			ItemStack stack;
			float i = AncientSpellcraft.rand.nextFloat();
			if (i <= 0.2) {
				stack = new ItemStack(Items.BREAD, ASUtils.randIntBetween(1, 2));
			} else if (i <= 0.4) {
				stack = new ItemStack(Items.COOKED_MUTTON, ASUtils.randIntBetween(1, 2));
			} else if (i <= 0.5) {
				stack = new ItemStack(Items.COOKIE, ASUtils.randIntBetween(1, 3));
			} else if (i <= 0.6) {
				stack = new ItemStack(Items.BEETROOT_SOUP, ASUtils.randIntBetween(1, 1));
			} else if (i <= 0.7) {
				stack = new ItemStack(Items.APPLE, ASUtils.randIntBetween(1, 3));
			} else if (i <= 0.8) {
				stack = new ItemStack(Items.COOKED_CHICKEN, ASUtils.randIntBetween(1, 2));
			} else if (i <= 0.9) {
				stack = new ItemStack(Items.COOKED_FISH, ASUtils.randIntBetween(1, 2));
			} else if (i <= 0.95) {
				stack = new ItemStack(Items.COOKED_PORKCHOP, ASUtils.randIntBetween(1, 2));
			} else if (i < 0.97 && i < 1){
				stack = new ItemStack(Items.GOLDEN_APPLE, ASUtils.randIntBetween(1, 1));
			} else {
				stack = new ItemStack(Items.COOKED_BEEF, ASUtils.randIntBetween(1, 1));

			}

			if (!player.inventory.addItemStackToInventory(stack)) {
				player.dropItem(stack, false);
			}

		}
	}
}
