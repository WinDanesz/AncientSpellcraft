package com.windanesz.ancientspellcraft.integration.jei;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.item.ItemNewArtefact;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftBlocks;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.Wizardry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.Arrays;
import java.util.Locale;

@JEIPlugin
public class ASJEIIntegration implements IModPlugin {

	public ASJEIIntegration() {
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {

		if (!Settings.generalSettings.jei_integration)
			return;

	}

	@Override
	public void register(IModRegistry registry) {

		if (!Wizardry.settings.jeiIntegration)
			return;


		// Hide some items from JEI
		IIngredientBlacklist blacklist = registry.getJeiHelpers().getIngredientBlacklist();
//		blacklist.addIngredientToBlacklist(Item.getItemFromBlock(AncientSpellcraftBlocks.SKULL_WATCH));
//		blacklist.addIngredientToBlacklist(AncientSpellcraftItems.wizard_hat_ancient);

		addItemInfo(registry, Item.getItemFromBlock(AncientSpellcraftBlocks.SCRIBING_DESK), ".desc_extended");
		addItemInfo(registry, Item.getItemFromBlock(AncientSpellcraftBlocks.SPHERE_COGNIZANCE), ".desc_extended");
		addItemInfo(registry, Item.getItemFromBlock(AncientSpellcraftBlocks.DEVORITIUM_BLOCK), ".desc_extended");

		addItemInfo(registry, AncientSpellcraftItems.crystal_shard_fire, ".desc_extended");
		addItemInfo(registry, AncientSpellcraftItems.crystal_shard_ice, ".desc_extended");
		addItemInfo(registry, AncientSpellcraftItems.crystal_shard_lightning, ".desc_extended");
		addItemInfo(registry, AncientSpellcraftItems.crystal_shard_necromancy, ".desc_extended");
		addItemInfo(registry, AncientSpellcraftItems.crystal_shard_earth, ".desc_extended");
		addItemInfo(registry, AncientSpellcraftItems.crystal_shard_sorcery, ".desc_extended");
		addItemInfo(registry, AncientSpellcraftItems.crystal_shard_healing, ".desc_extended");

		addItemInfo(registry, AncientSpellcraftItems.devoritium_bomb, ".desc_extended");
		addItemInfo(registry, AncientSpellcraftItems.devoritium_sword, ".desc_extended");
		addItemInfo(registry, AncientSpellcraftItems.devoritium_arrow, ".desc_extended");
		addItemInfo(registry, AncientSpellcraftItems.devoritium_door, ".desc_extended");
		addItemInfo(registry, AncientSpellcraftItems.devoritium_ingot, ".desc_extended");
		addItemInfo(registry, AncientSpellcraftItems.astral_diamond_charged, ".desc_extended");

		addItemInfo(registry, AncientSpellcraftItems.stone_tablet_small, ".desc_extended");
		addItemInfo(registry, AncientSpellcraftItems.stone_tablet, ".desc_extended");
		addItemInfo(registry, AncientSpellcraftItems.stone_tablet_large, ".desc_extended");
		addItemInfo(registry, AncientSpellcraftItems.stone_tablet_grand, ".desc_extended");

		for (Item item : Item.REGISTRY) {

			if (item instanceof ItemNewArtefact) {

				ItemStack stack = new ItemStack(item);
				registry.addIngredientInfo(stack, VanillaTypes.ITEM, "item." + AncientSpellcraft.MODID + ":"
						+ ((ItemNewArtefact) item).getType().toString().toLowerCase(Locale.ROOT) + ".generic.desc");
			}
		}
	}

	private static void addItemInfo(IModRegistry registry, Item item, String... suffixes) {
		NonNullList<ItemStack> subItems = NonNullList.create();
		item.getSubItems(item.getCreativeTab(), subItems);
		for (ItemStack stack : subItems)
			addItemInfo(registry, stack, suffixes);
	}

	private static void addItemInfo(IModRegistry registry, ItemStack stack, String... suffixes) {
		String prefix = stack.getItem().getTranslationKey(stack);
		String[] keys = Arrays.stream(suffixes).map(s -> prefix + s).toArray(String[]::new);
		registry.addIngredientInfo(stack, VanillaTypes.ITEM, keys);
	}
}
