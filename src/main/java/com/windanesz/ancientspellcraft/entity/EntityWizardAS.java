package com.windanesz.ancientspellcraft.entity;

import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.item.ItemNewArtefact;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.entity.living.EntityWizard;
import electroblob.wizardry.registry.WizardryItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;


public class EntityWizardAS extends EntityWizard {

	public EntityWizardAS(World world) {
		super(world);
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		/// applying trade changes
		MerchantRecipeList list = getRecipes(player);

		// only add the books after the 12th trade
		if (list.size() >= 12 && (Settings.generalSettings.wizards_buy_ancient_spellcraft_books || Settings.generalSettings.wizards_buy_ancient_element_books)) {

			// buy
			ItemStack anyASSpellBook = new ItemStack(AncientSpellcraftItems.ancient_spellcraft_spell_book, 1, OreDictionary.WILDCARD_VALUE);
			ItemStack anyAncientSpellBook = new ItemStack(AncientSpellcraftItems.ancient_spell_book, 1, OreDictionary.WILDCARD_VALUE);
			ItemStack ritualBook = new ItemStack(AncientSpellcraftItems.ritual_book, 1, OreDictionary.WILDCARD_VALUE);

			MerchantRecipe ASBooks = new MerchantRecipe(anyASSpellBook, new ItemStack(WizardryItems.magic_crystal, 5));
			MerchantRecipe ancientElementBooks = new MerchantRecipe(anyAncientSpellBook, new ItemStack(WizardryItems.magic_crystal, 5));

			MerchantRecipe ritualBooks = new MerchantRecipe(ritualBook, new ItemStack(WizardryItems.magic_crystal, 10));

			if (Settings.generalSettings.wizards_buy_ancient_spellcraft_books && list.stream().noneMatch(i -> i.getItemToBuy().getItem() == AncientSpellcraftItems.ancient_spellcraft_spell_book)) {
				list.add(ASBooks);
			}

			if (Settings.generalSettings.wizards_buy_ancient_element_books && list.stream().noneMatch(i -> i.getItemToBuy().getItem() == AncientSpellcraftItems.ancient_spell_book)) {
				list.add(ancientElementBooks);
			}

			if (Settings.generalSettings.wizards_buy_ancient_spellcraft_ritual_books && list.stream().noneMatch(i -> i.getItemToBuy().getItem() == AncientSpellcraftItems.ancient_spellcraft_spell_book)) {
				list.add(ritualBooks);
			}
		}

		if (ItemNewArtefact.isNewArtefactActive(player, AncientSpellcraftItems.head_merchant)) {

			List<Item> scrollList = new ArrayList<>();
			scrollList.add(WizardryItems.scroll);
			scrollList.add(AncientSpellcraftItems.ancient_spellcraft_scroll);

			for (Item scroll : scrollList) {
				ItemStack scrollStack = new ItemStack(scroll, 1, OreDictionary.WILDCARD_VALUE);
				ItemStack crystalStack1 = new ItemStack(WizardryItems.magic_crystal, 1);
				MerchantRecipe rec = new MerchantRecipe(scrollStack, crystalStack1);

				boolean matches = false;
				for (MerchantRecipe r : list) {
					if (r.getItemToBuy().getItem() == rec.getItemToBuy().getItem()) {
						matches = true;
					}
				}

				if (!matches) {
					list.add(rec);
				}
//				if (list.stream().noneMatch(i -> i.getItemToBuy().getItem() == rec.getItemToBuy().getItem())) {

				}
			}

		/// normal behaviour

		return super.processInteract(player, hand);
	}
}
