package com.windanesz.ancientspellcraft.item;

import electroblob.wizardry.constants.Element;
import net.minecraft.item.EnumRarity;

public class ItemElementalCloak extends ItemASArtefact {

	Element element;

	public ItemElementalCloak(EnumRarity rarity, Type type, Element element) {
		super(rarity, type);
		this.element = element;
	}

	public Element getElement() {
		return element;
	}
}
