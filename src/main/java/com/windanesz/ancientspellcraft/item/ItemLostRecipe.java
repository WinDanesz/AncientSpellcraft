package com.windanesz.ancientspellcraft.item;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;

public class ItemLostRecipe extends ItemASArtefact {
	public ItemLostRecipe(EnumRarity rarity, Type type) {
		super(rarity, type);
		setContainerItem(this);
	}

	@Override
	public boolean hasContainerItem() {
		return true;
	}

	@Override
	public Item getContainerItem() {
		return this;
	}
}
