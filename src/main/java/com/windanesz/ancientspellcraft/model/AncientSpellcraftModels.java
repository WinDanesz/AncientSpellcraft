package com.windanesz.ancientspellcraft.model;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftBlocks;
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
		registerItemModel(AncientSpellcraftItems.ancient_spell_book);
		registerItemModel(AncientSpellcraftItems.ancient_spellcraft_scroll);
		registerItemModel(AncientSpellcraftItems.enchanted_name_tag);
		registerItemModel(AncientSpellcraftItems.wizard_hat_ancient);

		// ====================== Artefacts ======================

		registerItemModel(AncientSpellcraftItems.ring_lodestone);
		registerItemModel(AncientSpellcraftItems.ring_poison_arrow);
		registerItemModel(AncientSpellcraftItems.ring_power);
		registerItemModel(AncientSpellcraftItems.ring_berserker);
		registerItemModel(AncientSpellcraftItems.ring_blast);
		registerItemModel(AncientSpellcraftItems.ring_prismarine);
		registerItemModel(AncientSpellcraftItems.ring_mana_transfer);
		registerItemModel(AncientSpellcraftItems.ring_range);
		registerItemModel(AncientSpellcraftItems.ring_focus_crystal);

		registerItemModel(AncientSpellcraftItems.amulet_mana);
		registerItemModel(AncientSpellcraftItems.amulet_rabbit);
		registerItemModel(AncientSpellcraftItems.amulet_poison_resistance);
		registerItemModel(AncientSpellcraftItems.amulet_power);
		registerItemModel(AncientSpellcraftItems.charm_cryostasis);
		registerItemModel(AncientSpellcraftItems.amulet_curse_ward);
		registerItemModel(AncientSpellcraftItems.amulet_pendant_of_eternity);
		registerItemModel(AncientSpellcraftItems.amulet_time_knot);

		registerItemModel(AncientSpellcraftItems.charm_rift_bottle);
		registerItemModel(AncientSpellcraftItems.charm_mana_flask);
		registerItemModel(AncientSpellcraftItems.charm_reanimation);
		registerItemModel(AncientSpellcraftItems.charm_knowledge_orb);
		registerItemModel(AncientSpellcraftItems.charm_power_orb);
		registerItemModel(AncientSpellcraftItems.charm_progression_orb);
		registerItemModel(AncientSpellcraftItems.charm_lightning_orb);
		registerItemModel(AncientSpellcraftItems.charm_ice_orb);
		registerItemModel(AncientSpellcraftItems.charm_earth_orb);
		registerItemModel(AncientSpellcraftItems.charm_healing_orb);
		registerItemModel(AncientSpellcraftItems.charm_necromancy_orb);
		registerItemModel(AncientSpellcraftItems.charm_fire_orb);
		registerItemModel(AncientSpellcraftItems.charm_sorcery_orb);
		registerItemModel(AncientSpellcraftItems.charm_mana_orb);
		registerItemModel(AncientSpellcraftItems.cornucopia);
		registerItemModel(AncientSpellcraftItems.charm_bucket_coal);
		registerItemModel(AncientSpellcraftItems.charm_gold_bag);
		registerItemModel(AncientSpellcraftItems.charm_evergrowing_crystal);
		registerItemModel(AncientSpellcraftItems.charm_shadow_blade);
		registerItemModel(AncientSpellcraftItems.charm_magic_light);
		registerItemModel(AncientSpellcraftItems.charm_war_horn);
		registerItemModel(AncientSpellcraftItems.charm_elemental_grimoire);
		registerItemModel(AncientSpellcraftItems.charm_wand_upgrade);
		registerItemModel(AncientSpellcraftItems.charm_enchanted_needle);
		registerItemModel(AncientSpellcraftItems.charm_seed_bag);
		registerItemModel(AncientSpellcraftItems.charm_omnicron);

		// misc
		registerItemModel(AncientSpellcraftItems.shadow_blade);
		registerItemModel(AncientSpellcraftItems.spectral_fishing_rod);
		registerItemModel(AncientSpellcraftItems.ice_shield);

		registerItemModel(AncientSpellcraftItems.stone_tablet_small);
		registerItemModel(AncientSpellcraftItems.stone_tablet);
		registerItemModel(AncientSpellcraftItems.stone_tablet_large);
		registerItemModel(AncientSpellcraftItems.stone_tablet_grand);
		registerItemModel(AncientSpellcraftItems.ancient_mana_flask);
		registerItemModel(AncientSpellcraftItems.ancient_bound_stone);

		registerItemModel(AncientSpellcraftItems.astral_diamond_shard);
		registerItemModel(AncientSpellcraftItems.astral_diamond_charged);
		registerItemModel(AncientSpellcraftItems.crystal_shard_fire);
		registerItemModel(AncientSpellcraftItems.crystal_shard_earth);
		registerItemModel(AncientSpellcraftItems.crystal_shard_healing);
		registerItemModel(AncientSpellcraftItems.crystal_shard_ice);
		registerItemModel(AncientSpellcraftItems.crystal_shard_lightning);
		registerItemModel(AncientSpellcraftItems.crystal_shard_necromancy);
		registerItemModel(AncientSpellcraftItems.crystal_shard_sorcery);

		// Itemblocks
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.ARTEFACT_PENSIVE));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.SPHERE_COGNIZANCE));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.SKULL_WATCH));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.SCRIBING_DESK));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.IMBUEMENT_ALTAR_RUINED));

		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.crystal_ore_fire));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.crystal_ore_earth));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.crystal_ore_healing));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.crystal_ore_ice));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.crystal_ore_lightning));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.crystal_ore_necromancy));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.crystal_ore_sorcery));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.log_crystal_tree));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.imbuement_altar_ruined));
//		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.log_crystal_tree2));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.leaves_crystal_tree));

	}

	// below registry methods are courtesy of EB
	private static void registerItemModel(Item item) {
		ModelBakery.registerItemVariants(item, new ModelResourceLocation(item.getRegistryName(), "inventory"));
		ModelLoader.setCustomMeshDefinition(item, s -> new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}

	private static <T extends Item & IMultiTexturedItem> void registerMultiTexturedModel(T item) {

		if (item.getHasSubtypes()) {
			NonNullList<ItemStack> items = NonNullList.create();
			item.getSubItems(item.getCreativeTab(), items);
			for (ItemStack stack : items) {
				ModelLoader.setCustomModelResourceLocation(item, stack.getMetadata(),
						new ModelResourceLocation(item.getModelName(stack), "inventory"));
			}
		}
	}

	private static void registerItemModel(Item item, int metadata, String variant) {
		ModelLoader.setCustomModelResourceLocation(item, metadata,
				new ModelResourceLocation(item.getRegistryName(), variant));
	}

}

