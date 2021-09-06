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
		registerItemModel(AncientSpellcraftItems.ring_permanent_shrinkage);
		registerItemModel(AncientSpellcraftItems.ring_permanent_growth);
		registerItemModel(AncientSpellcraftItems.ring_unbinding);
		registerItemModel(AncientSpellcraftItems.ring_disenchanter);
		registerItemModel(AncientSpellcraftItems.ring_quicksand);

		registerItemModel(AncientSpellcraftItems.amulet_mana);
		registerItemModel(AncientSpellcraftItems.amulet_rabbit);
		registerItemModel(AncientSpellcraftItems.amulet_poison_resistance);
		registerItemModel(AncientSpellcraftItems.amulet_power);
		registerItemModel(AncientSpellcraftItems.charm_cryostasis);
		registerItemModel(AncientSpellcraftItems.amulet_curse_ward);
		registerItemModel(AncientSpellcraftItems.amulet_pendant_of_eternity);
		registerItemModel(AncientSpellcraftItems.amulet_time_knot);
		registerItemModel(AncientSpellcraftItems.amulet_arcane_catalyst);
		registerItemModel(AncientSpellcraftItems.amulet_persistence);
		registerItemModel(AncientSpellcraftItems.amulet_healing_mushroom);
		registerItemModel(AncientSpellcraftItems.amulet_shield);

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
		registerItemModel(AncientSpellcraftItems.charm_burrow);
		registerItemModel(AncientSpellcraftItems.charm_quicksand_walker);
//		registerItemModel(AncientSpellcraftItems.charm_book_death);
//		registerItemModel(AncientSpellcraftItems.charm_book_life);
		registerItemModel(AncientSpellcraftItems.charm_prismatic_spray);
		registerItemModel(AncientSpellcraftItems.charm_ice_cream);
		registerItemModel(AncientSpellcraftItems.charm_runic_hammer);
		registerItemModel(AncientSpellcraftItems.charm_fabrikator_toolkit);
		registerItemModel(AncientSpellcraftItems.charm_scissors);
		registerItemModel(AncientSpellcraftItems.charm_philosophers_stone);
		registerItemModel(AncientSpellcraftItems.charm_cube_phasing);
		registerItemModel(AncientSpellcraftItems.alchemical_essence);

		registerItemModel(AncientSpellcraftItems.wand_channeling);

		registerItemModel(AncientSpellcraftItems.belt_enchanter);
		registerItemModel(AncientSpellcraftItems.belt_stone);
//		registerItemModel(AncientSpellcraftItems.belt_druid);
		registerItemModel(AncientSpellcraftItems.belt_vortex);
		registerItemModel(AncientSpellcraftItems.belt_horse);

		registerItemModel(AncientSpellcraftItems.head_curse);
		registerItemModel(AncientSpellcraftItems.head_minions);
		registerItemModel(AncientSpellcraftItems.head_riftstone);
		registerItemModel(AncientSpellcraftItems.head_fortune);
		registerItemModel(AncientSpellcraftItems.head_merchant);
		registerItemModel(AncientSpellcraftItems.head_shield);
		registerItemModel(AncientSpellcraftItems.head_magelight);

		// misc
		registerItemModel(AncientSpellcraftItems.shadow_blade);
		registerItemModel(AncientSpellcraftItems.spectral_fishing_rod);
		registerItemModel(AncientSpellcraftItems.ice_shield);
		registerItemModel(AncientSpellcraftItems.stone_fist);
		registerItemModel(AncientSpellcraftItems.advanced_stone_fist);
		registerItemModel(AncientSpellcraftItems.scepter_mind_control);

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
		registerItemModel(AncientSpellcraftItems.sacred_mace);

		registerItemModel(AncientSpellcraftItems.devoritium_ingot);
		registerItemModel(AncientSpellcraftItems.devoritium_sword);
		registerItemModel(AncientSpellcraftItems.devoritium_axe);
		registerItemModel(AncientSpellcraftItems.devoritium_arrow);
		registerItemModel(AncientSpellcraftItems.devoritium_bomb);
		registerItemModel(AncientSpellcraftItems.devoritium_door);
		registerItemModel(AncientSpellcraftItems.devoritium_scimitar);
		registerItemModel(AncientSpellcraftItems.devoritium_shield);

		registerItemModel(AncientSpellcraftItems.transmutation_scroll);
		registerItemModel(AncientSpellcraftItems.bartering_scroll);

		registerItemModel(AncientSpellcraftItems.rune_algiz);
		registerItemModel(AncientSpellcraftItems.rune_ansuz);
		registerItemModel(AncientSpellcraftItems.rune_berkanan);
		registerItemModel(AncientSpellcraftItems.rune_dagaz);
		registerItemModel(AncientSpellcraftItems.rune_ehwaz);
		registerItemModel(AncientSpellcraftItems.rune_feoh);
		registerItemModel(AncientSpellcraftItems.rune_gyfu);
		registerItemModel(AncientSpellcraftItems.rune_haglaz);
		registerItemModel(AncientSpellcraftItems.rune_ihwaz);
		registerItemModel(AncientSpellcraftItems.rune_isaz);
		registerItemModel(AncientSpellcraftItems.rune_jera);
		registerItemModel(AncientSpellcraftItems.rune_kaunan);
		registerItemModel(AncientSpellcraftItems.rune_laguz);
		registerItemModel(AncientSpellcraftItems.rune_mannaz);
		registerItemModel(AncientSpellcraftItems.rune_naudiz);
		registerItemModel(AncientSpellcraftItems.rune_odal);
		registerItemModel(AncientSpellcraftItems.rune_peorth);
		registerItemModel(AncientSpellcraftItems.rune_raido);
		registerItemModel(AncientSpellcraftItems.rune_sowilo);
		registerItemModel(AncientSpellcraftItems.rune_thurisaz);
		registerItemModel(AncientSpellcraftItems.rune_tiwaz);
		registerItemModel(AncientSpellcraftItems.rune_uruz);
		registerItemModel(AncientSpellcraftItems.rune_wynn);
		registerItemModel(AncientSpellcraftItems.rune_yngvi);

		registerItemModel(AncientSpellcraftItems.rune_feoh);
		registerItemModel(AncientSpellcraftItems.rune_uruz);
		registerItemModel(AncientSpellcraftItems.rune_thurisaz);
		registerItemModel(AncientSpellcraftItems.rune_ansuz);
		registerItemModel(AncientSpellcraftItems.rune_raido);
		registerItemModel(AncientSpellcraftItems.rune_kaunan);
		registerItemModel(AncientSpellcraftItems.rune_gyfu);
		registerItemModel(AncientSpellcraftItems.rune_wynn);
		registerItemModel(AncientSpellcraftItems.rune_haglaz);
		registerItemModel(AncientSpellcraftItems.rune_naudiz);
		registerItemModel(AncientSpellcraftItems.rune_isaz);
		registerItemModel(AncientSpellcraftItems.rune_jera);
		registerItemModel(AncientSpellcraftItems.rune_ihwaz);
		registerItemModel(AncientSpellcraftItems.rune_peorth);
		registerItemModel(AncientSpellcraftItems.rune_algiz);
		registerItemModel(AncientSpellcraftItems.rune_sowilo);
		registerItemModel(AncientSpellcraftItems.rune_tiwaz);
		registerItemModel(AncientSpellcraftItems.rune_berkanan);
		registerItemModel(AncientSpellcraftItems.rune_ehwaz);
		registerItemModel(AncientSpellcraftItems.rune_mannaz);
		registerItemModel(AncientSpellcraftItems.rune_laguz);
		registerItemModel(AncientSpellcraftItems.rune_yngvi);
		registerItemModel(AncientSpellcraftItems.rune_odal);
		registerItemModel(AncientSpellcraftItems.rune_dagaz);

		registerItemModel(AncientSpellcraftItems.ritual_book);
		registerItemModel(AncientSpellcraftItems.arcane_compound);
		registerItemModel(AncientSpellcraftItems.blank_rune);
		registerItemModel(AncientSpellcraftItems.soulbound_upgrade);
		registerItemModel(AncientSpellcraftItems.spectral_shovel);
		registerItemModel(AncientSpellcraftItems.ice_cream);

		// Itemblocks
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.ARTEFACT_PENSIVE));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.SPHERE_COGNIZANCE));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.SENTINEL_BLOCK_IRON));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.SENTINEL_BLOCK_GOLD));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.SENTINEL_BLOCK_DIAMOND));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.SENTINEL_BLOCK_LARGE_IRON));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.SENTINEL_BLOCK_LARGE_GOLD));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.SENTINEL_BLOCK_LARGE_DIAMOND));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.SKULL_WATCH));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.SCRIBING_DESK));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.IMBUEMENT_ALTAR_RUINED));

		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.CRYSTAL_ORE_FIRE));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.CRYSTAL_ORE_EARTH));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.CRYSTAL_ORE_HEALING));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.CRYSTAL_ORE_ICE));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.CRYSTAL_ORE_LIGHTNING));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.CRYSTAL_ORE_NECROMANCY));

		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.CRYSTAL_ORE_SORCERY));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.LOG_CRYSTAL_TREE));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.IMBUEMENT_ALTAR_RUINED));
		//		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.log_crystal_tree2));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.LEAVES_CRYSTAL_TREE));

		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.DEVORITIUM_BLOCK));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.DEVORITIUM_ORE));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.DEVORITIUM_BARS));
//		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.DEVORITIUM_DOOR));

		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.DIMENSION_BOUNDARY));
		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.DIMENSION_FOCUS));
//		registerItemModel(Item.getItemFromBlock(AncientSpellcraftBlocks.ANCIENT_DIMENSION_BOUNDARY));

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

