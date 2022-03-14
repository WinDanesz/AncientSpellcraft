package com.windanesz.ancientspellcraft.integration.jei;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.item.ItemNewArtefact;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASItems;
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

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {

		if (!Settings.generalSettings.jei_integration) { return; }

		registry.addRecipeCategories(new ArcaneAnvilRecipeCategory(registry));
	}

	@Override
	public void register(IModRegistry registry) {

		if (!Wizardry.settings.jeiIntegration) { return; }

		// Add imbuement altar as the item required to use imbuement altar recipes
		registry.addRecipeCatalyst(new ItemStack(ASBlocks.ARCANE_ANVIL), ArcaneAnvilRecipeCategory.UID);
		registry.addRecipes(ArcaneAnvilRecipeCategory.generateRecipes(), ArcaneAnvilRecipeCategory.UID);

		// Hide some items from JEI
		IIngredientBlacklist blacklist = registry.getJeiHelpers().getIngredientBlacklist();

		addItemInfo(registry, Item.getItemFromBlock(ASBlocks.SCRIBING_DESK), ".desc_extended");
		addItemInfo(registry, Item.getItemFromBlock(ASBlocks.SPHERE_COGNIZANCE), ".desc_extended");
		addItemInfo(registry, Item.getItemFromBlock(ASBlocks.DEVORITIUM_BLOCK), ".desc_extended");

		addItemInfo(registry, ASItems.crystal_shard_fire, ".desc_extended");
		addItemInfo(registry, ASItems.crystal_shard_ice, ".desc_extended");
		addItemInfo(registry, ASItems.crystal_shard_lightning, ".desc_extended");
		addItemInfo(registry, ASItems.crystal_shard_necromancy, ".desc_extended");
		addItemInfo(registry, ASItems.crystal_shard_earth, ".desc_extended");
		addItemInfo(registry, ASItems.crystal_shard_sorcery, ".desc_extended");
		addItemInfo(registry, ASItems.crystal_shard_healing, ".desc_extended");

		addItemInfo(registry, ASItems.devoritium_bomb, ".desc_extended");
		addItemInfo(registry, ASItems.devoritium_sword, ".desc_extended");
		addItemInfo(registry, ASItems.devoritium_arrow, ".desc_extended");
		addItemInfo(registry, ASItems.devoritium_door, ".desc_extended");
		addItemInfo(registry, ASItems.devoritium_ingot, ".desc_extended");
		addItemInfo(registry, ASItems.astral_diamond_charged, ".desc_extended");

		addItemInfo(registry, ASItems.stone_tablet_small, ".desc_extended");
		addItemInfo(registry, ASItems.stone_tablet, ".desc_extended");
		addItemInfo(registry, ASItems.stone_tablet_large, ".desc_extended");
		addItemInfo(registry, ASItems.stone_tablet_grand, ".desc_extended");

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
		for (ItemStack stack : subItems) { addItemInfo(registry, stack, suffixes); }
	}

	private static void addItemInfo(IModRegistry registry, ItemStack stack, String... suffixes) {
		String prefix = stack.getItem().getTranslationKey(stack);
		String[] keys = Arrays.stream(suffixes).map(s -> prefix + s).toArray(String[]::new);
		registry.addIngredientInfo(stack, VanillaTypes.ITEM, keys);
	}
}
