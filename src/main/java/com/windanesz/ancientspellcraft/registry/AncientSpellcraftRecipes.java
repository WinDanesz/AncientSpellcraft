package com.windanesz.ancientspellcraft.registry;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class AncientSpellcraftRecipes {

	private AncientSpellcraftRecipes() {} // no instances

	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {

		FurnaceRecipes.instance().addSmeltingRecipeForBlock(AncientSpellcraftBlocks.DEVORITIUM_ORE, new ItemStack(AncientSpellcraftItems.devoritium_ingot), 0.5f);

	}

}
