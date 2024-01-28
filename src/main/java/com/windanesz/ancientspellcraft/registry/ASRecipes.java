package com.windanesz.ancientspellcraft.registry;

import com.windanesz.ancientspellcraft.brewing.EssenceHealingPotionRecipe;
import com.windanesz.ancientspellcraft.brewing.EssenceRegenerationPotionRecipe;
import electroblob.wizardry.registry.WizardryItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class ASRecipes {

	private ASRecipes() {} // no instances

	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {

		FurnaceRecipes.instance().addSmeltingRecipeForBlock(ASBlocks.DEVORITIUM_ORE, new ItemStack(ASItems.devoritium_ingot), 0.5f);
		FurnaceRecipes.instance().addSmelting(WizardryItems.crystal_silver_plating, new ItemStack(ASItems.crystal_silver_ingot, 1), 1.5f);
		FurnaceRecipes.instance().addSmelting(ASItems.runic_plate, new ItemStack(ASItems.crystal_silver_nugget, 1), 1.5f);
		FurnaceRecipes.instance().addSmelting(Item.getItemFromBlock(ASBlocks.ARCANE_ANVIL), new ItemStack(ASItems.crystal_silver_nugget, 2), 1.8f);

		BrewingRecipeRegistry.addRecipe(new EssenceHealingPotionRecipe());
		BrewingRecipeRegistry.addRecipe(new EssenceRegenerationPotionRecipe());

	}

}
