package com.windanesz.ancientspellcraft.registry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

/**
 * Class responsible for defining all Ancient Spellcraft tabs.
 * Spells are stored in electroblob.wizardry.registry.WizardryTabs#SPELLS!
 *
 * @author WinDanesz
 */
public final class AncientSpellcraftTabs {

	public static final CreativeTabs ANCIENTSPELLCRAFT = new AncientSpellcraftTab("ancientspellcraft");
	public static final CreativeTabs ANCIENTSPELLCRAFT_GEAR = new AncientSpellcraftTab("ancientspellcraftgear");

	public static class AncientSpellcraftTab extends CreativeTabs {
		public AncientSpellcraftTab(String label) {
			super(label);
		}

		@Override
		public ItemStack createIcon() {
			return new ItemStack(AncientSpellcraftItems.charm_mana_orb);
		}
	}
}
