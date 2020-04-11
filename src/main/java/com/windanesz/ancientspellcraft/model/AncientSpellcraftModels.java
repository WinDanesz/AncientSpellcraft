package com.windanesz.ancientspellcraft.model;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.item.IMultiTexturedItem;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public final class AncientSpellcraftModels {
	private AncientSpellcraftModels() { // no instances
	}

	@SubscribeEvent
	public static void register(ModelRegistryEvent event) {
		registerItemModel(AncientSpellcraftItems.magic_shield);
		registerItemModel(AncientSpellcraftItems.spectral_shield);
		registerItemModel(AncientSpellcraftItems.ancient_spellcraft_spell_book);
		registerItemModel(AncientSpellcraftItems.ancient_spellcraft_scroll);
		registerItemModel(AncientSpellcraftItems.enchanted_name_tag);

	}

	// below registry methods are courtesy of EB
	private static void registerItemModel(Item item){
		ModelBakery.registerItemVariants(item, new ModelResourceLocation(item.getRegistryName(), "inventory"));
		ModelLoader.setCustomMeshDefinition(item, s -> new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}

	private static <T extends Item & IMultiTexturedItem> void registerMultiTexturedModel(T item){

		if(item.getHasSubtypes()){
			NonNullList<ItemStack> items = NonNullList.create();
			item.getSubItems(item.getCreativeTab(), items);
			for(ItemStack stack : items){
				ModelLoader.setCustomModelResourceLocation(item, stack.getMetadata(),
						new ModelResourceLocation(item.getModelName(stack), "inventory"));
			}
		}
	}

	private static void registerItemModel(Item item, int metadata, String variant){
		ModelLoader.setCustomModelResourceLocation(item, metadata,
				new ModelResourceLocation(item.getRegistryName(), variant));
	}

}

