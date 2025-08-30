package com.windanesz.ancientspellcraft.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IItemWithSlots {

	int getSlotCount();

	boolean hasGUI();

	int getRowCount();

	int getColumnCount();

	default boolean isItemValid(Item item) {
		return true;
	}

	default boolean isItemStackValid(ItemStack stack) {
		return true;
	}
}
