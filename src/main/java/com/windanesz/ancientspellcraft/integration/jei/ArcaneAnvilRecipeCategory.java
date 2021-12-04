package com.windanesz.ancientspellcraft.integration.jei;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.util.WandHelper;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * JEI recipe category implementation for all 'recipes' in the imbuement altar.
 *
 * @author WinDanesz
 * @since AS 1.3
 */
public class ArcaneAnvilRecipeCategory implements IRecipeCategory<ArcaneAnvilRecipe> {

	static final String UID = "ancientspellcraft:arcane_anvil";
	static final ResourceLocation TEXTURE = new ResourceLocation(AncientSpellcraft.MODID, "textures/integration/jei/arcane_anvil_background.png");

	static final int WIDTH = 139;
	static final int HEIGHT = 58;
	// Annoyingly, these seem to be for 18x18 slots rather than the actual highlighted area, unlike container slots
	static final int INPUT_SLOT_1_X = 7;
	static final int INPUT_SLOT_1_Y = 20;
	static final int INPUT_SLOT_2_X = 56;
	static final int INPUT_SLOT_2_Y = 20;
	static final int OUTPUT_SLOT_X = 114;
	static final int OUTPUT_SLOT_Y = 20;

	private final IDrawable background;

	public ArcaneAnvilRecipeCategory(IRecipeCategoryRegistration registry){
		IGuiHelper helper = registry.getJeiHelpers().getGuiHelper();
		background = helper.createDrawable(TEXTURE, 0, 0, WIDTH, HEIGHT);
	}

	@Override
	public String getUid(){
		return UID;
	}

	@Override
	public String getTitle(){
		// JEI is client-side, so client classes can safely be used here
		return I18n.format("integration.jei.category." + UID);
	}

	@Override
	public String getModName(){
		return AncientSpellcraft.NAME;
	}

	@Override
	public IDrawable getBackground(){
		return background;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, ArcaneAnvilRecipe recipeWrapper, IIngredients ingredients){

		// Okay, they're not technically *slots* but to all intents and purposes, that's how they behave
		IGuiItemStackGroup slots = recipeLayout.getItemStacks();

		List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
		List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);

		// Slot initialisation
		slots.init(0, true, INPUT_SLOT_1_X, INPUT_SLOT_1_Y); // input 1
		slots.init(1, true, INPUT_SLOT_2_X, INPUT_SLOT_2_Y); // input 2
		slots.init(2, false, OUTPUT_SLOT_X, OUTPUT_SLOT_Y); // Output

		// Assign ingredients to slots
		for(int j = 0; j < inputs.size(); j++) slots.set(j, inputs.get(j));
		for(int k = 0; k < outputs.size(); k++) slots.set(inputs.size() + k, outputs.get(k));

	}

	/** Called to generate all of AS's arcane anvil 'recipes' for JEI. */
	public static Collection<ArcaneAnvilRecipe> generateRecipes(){

		List<ArcaneAnvilRecipe> recipes = new ArrayList<>();

		recipes.addAll(generateAnvilRecipes());
		return recipes;

	}

	private static Collection<ArcaneAnvilRecipe> generateAnvilRecipes(){

		List<ArcaneAnvilRecipe> recipes = new ArrayList<>();

		// ingot -> plating
		ItemStack plating_input1 = new ItemStack(AncientSpellcraftItems.crystal_silver_ingot);
		ItemStack plating_input2 = ItemStack.EMPTY;
		ItemStack plating_output = new ItemStack(WizardryItems.crystal_silver_plating);
		recipes.add(new ArcaneAnvilRecipe(plating_input1, plating_input2, plating_output));

		// novice sword
		ItemStack novice_input1 = new ItemStack(AncientSpellcraftItems.battlemage_sword_hilt);
		ItemStack novice_input2 = new ItemStack(AncientSpellcraftItems.battlemage_sword_blade);
		ItemStack novice_output = new ItemStack(AncientSpellcraftItems.battlemage_sword_novice);
		recipes.add(new ArcaneAnvilRecipe(novice_input1, novice_input2, novice_output));

		// apprentice sword
		ItemStack apprentice_input1 = new ItemStack(AncientSpellcraftItems.battlemage_sword_novice);
		WandHelper.addProgression(apprentice_input1, Tier.APPRENTICE.getProgression());

		ItemStack apprentice_input2 = new ItemStack(AncientSpellcraftItems.crystal_silver_ingot);
		ItemStack apprentice_output = new ItemStack(AncientSpellcraftItems.battlemage_sword_apprentice);
		recipes.add(new ArcaneAnvilRecipe(apprentice_input1, apprentice_input2, apprentice_output));

		// advanced sword
		ItemStack advanced_input1 = new ItemStack(AncientSpellcraftItems.battlemage_sword_apprentice);
		WandHelper.addProgression(advanced_input1, Tier.ADVANCED.getProgression());

		ItemStack advanced_input2 = new ItemStack(AncientSpellcraftItems.crystal_silver_ingot);
		ItemStack advanced_output = new ItemStack(AncientSpellcraftItems.battlemage_sword_advanced);
		recipes.add(new ArcaneAnvilRecipe(advanced_input1, advanced_input2, advanced_output));

		// master sword
		ItemStack master_input1 = new ItemStack(AncientSpellcraftItems.battlemage_sword_advanced);
		WandHelper.addProgression(advanced_input1, Tier.MASTER.getProgression());

		ItemStack master_input2 = new ItemStack(AncientSpellcraftItems.crystal_silver_ingot);
		ItemStack master_output = new ItemStack(AncientSpellcraftItems.battlemage_sword_master);
		recipes.add(new ArcaneAnvilRecipe(master_input1, master_input2, master_output));

		return recipes;
	}
}
