package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASTabs;
import electroblob.wizardry.item.ItemWandUpgrade;
import electroblob.wizardry.util.WandHelper;

public class ItemWandUpgradeAS extends ItemWandUpgrade {

	public ItemWandUpgradeAS() {
		super();
		this.setCreativeTab(ASTabs.ANCIENTSPELLCRAFT);
	}

	public static void init() {
		WandHelper.registerSpecialUpgrade(ASItems.soulbound_upgrade, "soulbound_upgrade");
		WandHelper.registerSpecialUpgrade(ASItems.sentience_upgrade, "sentience_upgrade");
		WandHelper.registerSpecialUpgrade(ASItems.empowerment_upgrade, "empowerment_upgrade");
	}

}
