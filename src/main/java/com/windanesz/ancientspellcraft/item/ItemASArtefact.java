package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftTabs;
import electroblob.wizardry.item.ItemArtefact;
import net.minecraft.item.EnumRarity;

public class ItemASArtefact extends ItemArtefact {

	public ItemASArtefact(EnumRarity rarity, Type type) {
		super(rarity, type);
		setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT_GEAR);
	}
}
