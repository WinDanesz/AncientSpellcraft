package com.windanesz.ancientspellcraft.registry;

import electroblob.wizardry.item.ItemScroll;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.registry.WizardryTabs;
import electroblob.wizardry.spell.Spell;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

/**
 * Class responsible for defining all Ancient Spellcraft tabs.
 * Spells are stored in electroblob.wizardry.registry.WizardryTabs#SPELLS!
 *
 * @author WinDanesz
 */
public final class ASTabs {

	public static final CreativeTabs ANCIENTSPELLCRAFT = new ASTab("ancientspellcraft") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(ASItems.stone_tablet_large);
		}
	};

	public static final CreativeTabs CLASS_SPELLS = new WizardryTabs.CreativeTabSorted("ancientspellcraftclassspells",

			(stack1, stack2) -> {

				if((stack1.getItem() instanceof ItemSpellBook && stack2.getItem() instanceof ItemSpellBook)
						|| (stack1.getItem() instanceof ItemScroll && stack2.getItem() instanceof ItemScroll)){

					Spell spell1 = Spell.byMetadata(stack1.getItemDamage());
					Spell spell2 = Spell.byMetadata(stack2.getItemDamage());

					return spell1.compareTo(spell2);

				}else if(stack1.getItem() instanceof ItemScroll){
					return 1;
				}else if(stack2.getItem() instanceof ItemScroll){
					return -1;
				}
				return 0;
			},

			true);

	public static final CreativeTabs ANCIENTSPELLCRAFT_GEAR = new ASTab("ancientspellcraftgear") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(ASItems.ring_blast);
		}
	};

	public static final CreativeTabs ANCIENTSPELLCRAFT_RITUALS = new ASTab("ancientspellcraftrituals") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(ASItems.ritual_book);
		}
	};

	private static class ASTab extends CreativeTabs {
		public ASTab(String label) {
			super(label);
		}

		@Override
		public ItemStack createIcon() {
			return new ItemStack(ASItems.charm_mana_orb);
		}
	}


}
