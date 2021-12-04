package com.windanesz.ancientspellcraft.integration.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArcaneAnvilRecipe implements IRecipeWrapper {

	private final ItemStack result;

	private final List<List<ItemStack>> inputs;

	public ArcaneAnvilRecipe(ItemStack firstStack, ItemStack secondStack, ItemStack result) {

		this.result = result;

		this.inputs = new ArrayList<>();
		this.inputs.add(Collections.singletonList(firstStack));
		this.inputs.add(Collections.singletonList(secondStack));
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputLists(VanillaTypes.ITEM, inputs);
		ingredients.setOutput(VanillaTypes.ITEM, result);
	}

}
