package com.windanesz.ancientspellcraft.item;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemLostRecipe extends ItemASArtefact {
	public ItemLostRecipe(EnumRarity rarity, Type type) {
		super(rarity, type);
		setContainerItem(this);
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return true;
	}

	/**
	 * The item will return itself when used in a crafting recipe
	 */
	@Override
	public Item getContainerItem() {
		return this;
	}

	@Override
	public ItemStack getContainerItem(ItemStack itemStack) {
		return itemStack;
	}
}
