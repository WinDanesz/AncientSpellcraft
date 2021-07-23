package com.windanesz.ancientspellcraft.brewing;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.common.brewing.IBrewingRecipe;

import javax.annotation.Nonnull;

public class EssenceHealingPotionRecipe implements IBrewingRecipe {

	public EssenceHealingPotionRecipe() {}

	/**
	 * Returns true is the passed ItemStack is an input for this recipe. "Input"
	 * being the item that goes in one of the three bottom slots of the brewing
	 * stand (e.g: water bottle)
	 */
	@Override
	public boolean isInput(@Nonnull ItemStack stack) {
		return PotionUtils.getPotionFromItem(stack) == PotionTypes.WATER;
	}

	/**
	 * Returns true if the passed ItemStack is an ingredient for this recipe.
	 * "Ingredient" being the item that goes in the top slot of the brewing
	 * stand (e.g: nether wart)
	 */
	@Override
	public boolean isIngredient(@Nonnull ItemStack ingredient) {
		return ingredient.getItem() == AncientSpellcraftItems.alchemical_essence;
	}

	/**
	 * Returns the output when the passed input is brewed with the passed
	 * ingredient. Empty if invalid input or ingredient.
	 */
	@Nonnull
	@Override
	public ItemStack getOutput(@Nonnull ItemStack input, @Nonnull ItemStack ingredient) {
		if (isInput(input) && isIngredient(ingredient))
			return PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.HEALING);

		return ItemStack.EMPTY;
	}
}
