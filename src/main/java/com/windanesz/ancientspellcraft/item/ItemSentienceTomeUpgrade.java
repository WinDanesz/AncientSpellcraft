package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASTabs;
import electroblob.wizardry.item.ItemWandUpgrade;
import electroblob.wizardry.util.WandHelper;

public class ItemSentienceTomeUpgrade extends ItemWandUpgrade {

	public ItemSentienceTomeUpgrade() {
		super();
		this.setCreativeTab(ASTabs.ANCIENTSPELLCRAFT);
	}

	public static void init() {
		WandHelper.registerSpecialUpgrade(ASItems.sentience_upgrade, "sentience_upgrade");
	}

	private static boolean isNumeric(String string) {
		return string.matches("\\d+");
	}

}
