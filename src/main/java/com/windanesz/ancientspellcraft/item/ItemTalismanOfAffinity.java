package com.windanesz.ancientspellcraft.item;

import electroblob.wizardry.item.ItemCrystal;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemTalismanOfAffinity extends AbstractItemArtefactWithSlots {

	public ItemTalismanOfAffinity(EnumRarity rarity, Type type) {
		super(rarity, type, 1, 1, true);
	}

	@Override
	public boolean isItemStackValid(ItemStack stack) {
		return stack.getItem() instanceof ItemCrystal && stack.getMetadata() != 0;
	}

	@Override
	public boolean isItemValid(Item item) {
		return item instanceof ItemCrystal;
	}
}
