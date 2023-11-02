package com.windanesz.ancientspellcraft.item;

import net.minecraft.item.Item;

public interface IItemWithSlots {

	int getSlotCount();

	boolean hasGUI();

	int getRowCount();

	int getColumnCount();

	boolean isItemValid(Item item);
}
