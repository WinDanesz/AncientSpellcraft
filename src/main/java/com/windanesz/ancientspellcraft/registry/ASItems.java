package com.windanesz.ancientspellcraft.registry;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.projectile.EntityDevoritiumBomb;
import com.windanesz.ancientspellcraft.item.*;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.ItemCrystal;
import electroblob.wizardry.item.ItemScroll;
import electroblob.wizardry.misc.BehaviourSpellDispense;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardryTabs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ObjectHolder(AncientSpellcraft.MODID)
@Mod.EventBusSubscriber
public final class ASItems {

	private ASItems() {
	} // No instances!

	@Nonnull
	@SuppressWarnings("ConstantConditions")
	private static <T> T placeholder() {
		return null;
	}

	public static final Item ancient_spellcraft_spell_book = placeholder();
	public static final Item ancient_spell_book = placeholder();
	public static final Item ancient_spellcraft_scroll = placeholder();
	public static final Item spectral_shield = placeholder();

	// wizard gear
	public static final Item wizard_hat_ancient = placeholder();

	// ====================== Artefacts ======================

	/// ring
	public static final Item ring_lodestone = placeholder();
	public static final Item ring_poison_arrow = placeholder();
	public static final Item ring_power = placeholder();
	public static final Item ring_berserker = placeholder();
	public static final Item ring_prismarine = placeholder();
	public static final Item ring_mana_transfer = placeholder();
	public static final Item ring_blast = placeholder();
	public static final Item ring_range = placeholder();
	public static final Item ring_duration = placeholder();
	public static final Item ring_focus_crystal = placeholder();
	public static final Item ring_permanent_shrinkage = placeholder();
	public static final Item ring_permanent_growth = placeholder();
	public static final Item ring_unbinding = placeholder();
	public static final Item ring_disenchanter = placeholder();
	public static final Item ring_quicksand = placeholder();
	public static final Item ring_mana_lesser = placeholder();
	public static final Item ring_mana_greater = placeholder();
	public static final Item ring_protector = placeholder();
	public static final Item ring_devotion = placeholder();
	public static final Item ring_withdraw_life = placeholder();
	public static final Item ring_cloudwalker = placeholder();
	public static final Item ring_lily_flower = placeholder();
	public static final Item ring_spirit_ward = placeholder();
	public static final Item ring_kinetic = placeholder();
	public static final Item ring_charge = placeholder();
	public static final Item ring_griefing = placeholder();
	public static final Item ring_metamagic_preserve = placeholder();
	public static final Item ring_rare_scroll = placeholder();
	public static final Item ring_absorb_crystal = placeholder();
	public static final Item ring_mana_cost = placeholder();
	public static final Item ring_chaos_blast_multitarget = placeholder();
	public static final Item ring_undeath = placeholder();
	public static final Item ring_of_force = placeholder();
	public static final Item ring_life_drain = placeholder();
	public static final Item ring_eternal_contingency = placeholder();
	public static final Item ring_solid_air = placeholder();
	public static final Item ring_shivering = placeholder();
	public static final Item ring_arcane_flames = placeholder();
	public static final Item ring_healer = placeholder();

	/// amulet
	public static final Item charm_cryostasis = placeholder();
	public static final Item amulet_poison_resistance = placeholder();
	public static final Item amulet_power = placeholder();
	public static final Item amulet_mana = placeholder();
	public static final Item amulet_rabbit = placeholder();
	public static final Item amulet_pendant_of_eternity = placeholder();
	public static final Item amulet_curse_ward = placeholder();
	public static final Item amulet_time_knot = placeholder();
	public static final Item amulet_arcane_catalyst = placeholder();
	public static final Item amulet_persistence = placeholder();
	public static final Item amulet_healing_mushroom = placeholder();
	public static final Item amulet_shield = placeholder();
	public static final Item amulet_imbued_marble = placeholder();
	public static final Item amulet_oakflesh = placeholder();
	public static final Item amulet_inspiration = placeholder();
	public static final Item amulet_domus = placeholder();
	public static final Item amulet_searing_skin = placeholder();
	public static final Item amulet_cursed_mirror = placeholder();
	public static final Item amulet_absorb_potion = placeholder();
	public static final Item amulet_spellbinding = placeholder();
	public static final Item amulet_cursed_pendant = placeholder();
	public static final Item amulet_time_slow = placeholder();
	public static final Item amulet_elemental_defense = placeholder();
	public static final Item amulet_elemental_offense = placeholder();
	public static final Item amulet_talisman_of_affinity = placeholder();
	public static final Item amulet_black_tongue = placeholder();
	public static final Item amulet_holy_aura = placeholder();
	public static final Item amulet_of_resistance = placeholder();
	public static final Item amulet_elemental_aura = placeholder();

	/// charm
	public static final Item charm_rift_bottle = placeholder();
	public static final Item charm_mana_flask = placeholder();
	public static final Item charm_reanimation = placeholder();
	public static final Item charm_knowledge_orb = placeholder();
	public static final Item charm_power_orb = placeholder();
	public static final Item charm_mana_orb = placeholder();
	public static final Item charm_progression_orb = placeholder();
	public static final Item charm_suppression_orb = placeholder();
	public static final Item charm_lightning_orb = placeholder();
	public static final Item charm_ice_orb = placeholder();
	public static final Item charm_earth_orb = placeholder();
	public static final Item charm_healing_orb = placeholder();
	public static final Item charm_necromancy_orb = placeholder();
	public static final Item charm_fire_orb = placeholder();
	public static final Item charm_sorcery_orb = placeholder();
	public static final Item charm_majestic_mana = placeholder();
	public static final Item charm_guardian_blade = placeholder();
	public static final Item charm_cat_eye = placeholder();

	public static final Item charm_bucket_coal = placeholder();
	public static final Item cornucopia = placeholder();
	public static final Item charm_gold_bag = placeholder();
	public static final Item charm_evergrowing_crystal = placeholder();
	public static final Item charm_shadow_blade = placeholder();
	public static final Item charm_magic_light = placeholder();
	public static final Item charm_war_horn = placeholder();
	public static final Item charm_elemental_grimoire = placeholder();
	public static final Item charm_wand_upgrade = placeholder();
	public static final Item charm_enchanted_needle = placeholder();
	public static final Item charm_seed_bag = placeholder();
	public static final Item charm_wizard_tankard = placeholder();
	public static final Item charm_omnicron = placeholder();
	public static final Item charm_burrow = placeholder();
	public static final Item charm_quicksand_walker = placeholder();
	//	public static final Item charm_book_death = placeholder();
	//	public static final Item charm_book_life = placeholder();
	public static final Item charm_prismatic_spray = placeholder();
	public static final Item charm_ice_cream = placeholder();
	public static final Item charm_runic_hammer = placeholder();
	public static final Item charm_fabrikator_toolkit = placeholder();
	public static final Item charm_scissors = placeholder();
	public static final Item charm_philosophers_stone = placeholder();
	public static final Item charm_cube_phasing = placeholder();
	public static final Item charm_cube_duplication = placeholder();
	public static final Item charm_spectral_army = placeholder();
	public static final Item charm_spectral_tome = placeholder();
	public static final Item charm_clover = placeholder();
	public static final Item charm_stone_tablet = placeholder();
	//public static final Item charm_ice_arrow = placeholder();
	public static final Item charm_hoarders_orb = placeholder();
	public static final Item charm_elemental_alkahest = placeholder();
	public static final Item charm_sage_diary = placeholder();
	public static final Item charm_devoritium_magnet = placeholder();
	public static final Item charm_wild_catalyst = placeholder();
	public static final Item charm_metamagic_amplifier = placeholder();
	public static final Item charm_sentinel_eye = placeholder();
	public static final Item charm_plunderers_mark = placeholder();
	public static final Item charm_wizard_ale = placeholder();
	public static final Item charm_dimensional_disk = placeholder();
	public static final Item charm_crystal_sickle = placeholder();
	public static final Item charm_monster_charm = placeholder();
	public static final Item charm_teleportation_stone = placeholder();
	public static final Item belt_temporal_anchor = placeholder();
	public static final Item belt_hook = placeholder();
	public static final Item charm_potion_kit = placeholder();
	public static final Item charm_thousand_anvils = placeholder();
	public static final Item charm_aurelian_cup = placeholder();
	public static final Item charm_vault_key = placeholder();
	public static final Item charm_transcribing_tome = placeholder();
	public static final Item charm_focus_stone = placeholder();
	public static final Item charm_arcane_diffuser = placeholder();
	public static final Item charm_skeleton_ghosts = placeholder();
	public static final Item charm_rainbowstring = placeholder();

	public static final Item charm_arcane_mirror = placeholder();
	public static final Item charm_infernal_stone = placeholder();
	public static final Item charm_meteorite_stone = placeholder();
	public static final Item charm_beastcallers_horn = placeholder();
	public static final Item charm_transmutation_cube = placeholder();
	public static final Item charm_ancient_wand = placeholder();
	public static final Item charm_remnant_cage = placeholder();
	public static final Item charm_specterlight_torch = placeholder();
	public static final Item charm_hungry_chest = placeholder();
	public static final Item charm_diamond_goose = placeholder();
	public static final Item charm_wardrobe = placeholder();

	public static final Item alchemical_essence = placeholder();
	public static final Item wand_channeling = placeholder();

	public static final Item purifying_elixir_recipe = placeholder();
	public static final Item large_mana_flask_recipe = placeholder();

	/// now with Belts!

	public static final Item belt_enchanter = placeholder();
	public static final Item belt_stone = placeholder();
	//	public static final Item belt_druid = placeholder();
	public static final Item belt_vortex = placeholder();
	public static final Item belt_horse = placeholder();
	public static final Item belt_scroll_holder = placeholder();
	public static final Item belt_spring_charge = placeholder();
	public static final Item belt_soul_scorch = placeholder();
	public static final Item belt_emberguard = placeholder();
	public static final Item body_fire = placeholder();
	public static final Item body_ice = placeholder();
	public static final Item body_lightning = placeholder();
	public static final Item body_necromancy = placeholder();
	public static final Item body_earth = placeholder();
	public static final Item body_sorcery = placeholder();
	public static final Item body_healing = placeholder();

	/// and head slot artefacts!
	public static final Item head_curse = placeholder();
	public static final Item head_minions = placeholder();
	public static final Item head_riftstone = placeholder();
	public static final Item head_fortune = placeholder();
	public static final Item head_merchant = placeholder();
	public static final Item head_shield = placeholder();
	public static final Item head_magelight = placeholder();
	public static final Item head_lightning = placeholder();
	public static final Item head_chaos_magic = placeholder();
	public static final Item head_minion_circle = placeholder();
	public static final Item head_whispering_veil = placeholder();
	public static final Item head_disruptor_crown = placeholder();
	public static final Item head_circlet_of_the_unknown = placeholder();
	public static final Item head_ardor_crown = placeholder();
	public static final Item head_mask_of_perseigni = placeholder();

	/// misc
	public static final Item magic_shield = placeholder();
	public static final Item stone_tablet_small = placeholder();
	public static final Item stone_tablet = placeholder();
	public static final Item stone_tablet_large = placeholder();
	public static final Item stone_tablet_grand = placeholder();
	public static final Item ancient_mana_flask = placeholder();
	public static final Item ancient_bound_stone = placeholder();

	public static final Item astral_diamond_shard = placeholder();
	public static final Item astral_diamond_charged = placeholder();
	public static final Item crystal_shard_fire = placeholder();
	public static final Item crystal_shard_earth = placeholder();
	public static final Item crystal_shard_healing = placeholder();
	public static final Item crystal_shard_ice = placeholder();
	public static final Item crystal_shard_lightning = placeholder();
	public static final Item crystal_shard_necromancy = placeholder();
	public static final Item crystal_shard_sorcery = placeholder();

	public static final Item devoritium_ingot = placeholder();
	public static final Item devoritium_sword = placeholder();
	public static final Item devoritium_axe = placeholder();
	public static final Item devoritium_arrow = placeholder();
	public static final Item devoritium_bomb = placeholder();
	public static final Item devoritium_door = placeholder();
	public static final Item devoritium_scimitar = placeholder();
	public static final Item devoritium_shield = placeholder();
	public static final Item devoritium_nugget = placeholder();
	public static final Item transmutation_scroll = placeholder();
	public static final Item bartering_scroll = placeholder();
	public static final Item knowledge_scroll = placeholder();
	public static final Item duplication_scroll = placeholder();
	public static final Item amnesia_scroll = placeholder();
	public static final Item unsealing_scroll = placeholder();
	public static final Item imbuement_scroll = placeholder();

	/// handheld
	public static final Item shadow_blade = placeholder();
	public static final Item spectral_fishing_rod = placeholder();
	public static final Item ice_shield = placeholder();
	public static final Item battlemage_sword_novice = placeholder();
	public static final Item battlemage_sword_apprentice = placeholder();
	public static final Item battlemage_sword_advanced = placeholder();
	public static final Item battlemage_sword_master = placeholder();
	public static final Item battlemage_shield = placeholder();
	public static final Item devoritium_chestplate = placeholder();
	public static final Item master_bolt = placeholder();
	public static final Item runic_hammer = placeholder();

	public static final Item sacred_mace = placeholder();
	public static final Item stone_fist = placeholder();
	public static final Item advanced_stone_fist = placeholder();
	public static final Item scepter_mind_control = placeholder();

	// ====================== Misc ======================
	public static final Item enchanted_name_tag = placeholder();

	public static final Item blank_rune = placeholder();
	public static final Item rune_feoh = placeholder();
	public static final Item rune_uruz = placeholder();
	public static final Item rune_thurisaz = placeholder();
	public static final Item rune_ansuz = placeholder();
	public static final Item rune_raido = placeholder();
	public static final Item rune_kaunan = placeholder();
	public static final Item rune_gyfu = placeholder();
	public static final Item rune_wynn = placeholder();
	public static final Item rune_haglaz = placeholder();
	public static final Item rune_naudiz = placeholder();
	public static final Item rune_isaz = placeholder();
	public static final Item rune_jera = placeholder();
	public static final Item rune_ihwaz = placeholder();
	public static final Item rune_peorth = placeholder();
	public static final Item rune_algiz = placeholder();
	public static final Item rune_sowilo = placeholder();
	public static final Item rune_tiwaz = placeholder();
	public static final Item rune_berkanan = placeholder();
	public static final Item rune_ehwaz = placeholder();
	public static final Item rune_mannaz = placeholder();
	public static final Item rune_laguz = placeholder();
	public static final Item rune_yngvi = placeholder();
	public static final Item rune_odal = placeholder();
	public static final Item rune_dagaz = placeholder();
	public static final Item ritual_book = placeholder();
	public static final Item arcane_compound = placeholder();
	public static final Item ice_cream = placeholder();

	public static final Item soulbound_upgrade = placeholder();
	public static final Item spectral_shovel = placeholder();
	public static final Item battlemage_sword_hilt = placeholder();
	public static final Item battlemage_sword_blade = placeholder();
	public static final Item crystal_silver_ingot = placeholder();
	public static final Item crystal_silver_nugget = placeholder();

	public static final Item enchanted_filament = placeholder();
	public static final Item enchanted_page = placeholder();
	public static final Item ethereal_essence = placeholder();

	public static final Item sage_tome_novice_magic = placeholder();
	public static final Item sage_tome_apprentice_magic = placeholder();
	public static final Item sage_tome_advanced_magic = placeholder();
	public static final Item sage_tome_master_magic = placeholder();

	public static final Item sage_tome_novice_fire = placeholder();
	public static final Item sage_tome_apprentice_fire = placeholder();
	public static final Item sage_tome_advanced_fire = placeholder();
	public static final Item sage_tome_master_fire = placeholder();

	public static final Item sage_tome_novice_ice = placeholder();
	public static final Item sage_tome_apprentice_ice = placeholder();
	public static final Item sage_tome_advanced_ice = placeholder();
	public static final Item sage_tome_master_ice = placeholder();

	public static final Item sage_tome_novice_lightning = placeholder();
	public static final Item sage_tome_apprentice_lightning = placeholder();
	public static final Item sage_tome_advanced_lightning = placeholder();
	public static final Item sage_tome_master_lightning = placeholder();

	public static final Item sage_tome_novice_necromancy = placeholder();
	public static final Item sage_tome_apprentice_necromancy = placeholder();
	public static final Item sage_tome_advanced_necromancy = placeholder();
	public static final Item sage_tome_master_necromancy = placeholder();

	public static final Item sage_tome_novice_earth = placeholder();
	public static final Item sage_tome_apprentice_earth = placeholder();
	public static final Item sage_tome_advanced_earth = placeholder();
	public static final Item sage_tome_master_earth = placeholder();

	public static final Item sage_tome_novice_sorcery = placeholder();
	public static final Item sage_tome_apprentice_sorcery = placeholder();
	public static final Item sage_tome_advanced_sorcery = placeholder();
	public static final Item sage_tome_master_sorcery = placeholder();

	public static final Item sage_tome_novice_healing = placeholder();
	public static final Item sage_tome_apprentice_healing = placeholder();
	public static final Item sage_tome_advanced_healing = placeholder();
	public static final Item sage_tome_master_healing = placeholder();

	public static final Item empty_theory_scroll = placeholder();
	public static final Item theory_scroll = placeholder();
	public static final Item mystic_scroll = placeholder();
	public static final Item blank_mystic_scroll = placeholder();

	public static final Item runic_plate = placeholder();
	public static final Item mystic_spell_book = placeholder();
	public static final Item empty_mystic_spell_book = placeholder();
	public static final Item tome_controller = placeholder();
	public static final Item sentience_upgrade = placeholder();
	public static final Item empowerment_upgrade = placeholder();

	public static final Item charm_glyph_illumination = placeholder();
	public static final Item charm_glyph_leeching = placeholder();
	public static final Item charm_glyph_antigravity = placeholder();
	public static final Item charm_glyph_vitality = placeholder();
	public static final Item charm_glyph_warden = placeholder();
	public static final Item charm_glyph_fortification = placeholder();
	public static final Item charm_glyph_imbuement = placeholder();
	public static final Item charm_glyph_shield_disable = placeholder();
	public static final Item charm_glyph_might = placeholder();
	public static final Item charm_glyph_charge = placeholder();
	public static final Item charm_aura_alacrity = placeholder();
	public static final Item charm_aura_hatred = placeholder();
	public static final Item charm_aura_life = placeholder();
	public static final Item charm_aura_purity = placeholder();
	public static final Item charm_aura_warding = placeholder();
	public static final Item charm_aura_wither = placeholder();
	public static final Item charm_aura_tenacity = placeholder();
	public static final Item charm_aura_defense = placeholder();
	public static final Item charm_aura_vulnerability = placeholder();

	public static final Item warlock_orb_novice_magic = placeholder();
	public static final Item warlock_orb_apprentice_magic = placeholder();
	public static final Item warlock_orb_advanced_magic = placeholder();
	public static final Item warlock_orb_master_magic = placeholder();
	public static final Item warlock_orb_novice_earth = placeholder();
	public static final Item warlock_orb_apprentice_earth = placeholder();
	public static final Item warlock_orb_advanced_earth = placeholder();
	public static final Item warlock_orb_master_earth = placeholder();
	public static final Item warlock_orb_novice_sorcery = placeholder();
	public static final Item warlock_orb_apprentice_sorcery = placeholder();
	public static final Item warlock_orb_advanced_sorcery = placeholder();
	public static final Item warlock_orb_master_sorcery = placeholder();
	public static final Item warlock_orb_novice_fire = placeholder();
	public static final Item warlock_orb_apprentice_fire = placeholder();
	public static final Item warlock_orb_advanced_fire = placeholder();
	public static final Item warlock_orb_master_fire = placeholder();
	public static final Item warlock_orb_novice_ice = placeholder();
	public static final Item warlock_orb_apprentice_ice = placeholder();
	public static final Item warlock_orb_advanced_ice = placeholder();
	public static final Item warlock_orb_master_ice = placeholder();
	public static final Item warlock_orb_novice_lightning = placeholder();
	public static final Item warlock_orb_apprentice_lightning = placeholder();
	public static final Item warlock_orb_advanced_lightning = placeholder();
	public static final Item warlock_orb_master_lightning = placeholder();
	public static final Item warlock_orb_novice_necromancy = placeholder();
	public static final Item warlock_orb_apprentice_necromancy = placeholder();
	public static final Item warlock_orb_advanced_necromancy = placeholder();
	public static final Item warlock_orb_master_necromancy = placeholder();
	public static final Item warlock_orb_novice_healing = placeholder();
	public static final Item warlock_orb_apprentice_healing = placeholder();
	public static final Item warlock_orb_advanced_healing = placeholder();
	public static final Item warlock_orb_master_healing = placeholder();

	public static final Item forbidden_tome = placeholder();
	public static final Item body_power_gem = placeholder();
	public static final Item belt_fire = placeholder();
	public static final Item belt_ice = placeholder();
	public static final Item belt_lightning = placeholder();
	public static final Item belt_necromancy = placeholder();
	public static final Item belt_earth = placeholder();
	public static final Item belt_sorcery = placeholder();
	public static final Item belt_healing = placeholder();
	public static final Item belt_enchanted_whetstone = placeholder();
	public static final Item battlemage_contract = placeholder();

	public static final Item charm_conjuration_inhibitor = placeholder();

	public static final Item charm_verdant_crucible = placeholder();

	public static final Item charm_vessel_of_the_withered_oath = placeholder();

	// below registry methods are courtesy of EB
	public static void registerItem(IForgeRegistry<Item> registry, String name, Item item) {
		registerItem(registry, name, item, false);
	}

	// below registry methods are courtesy of EB
	public static void registerItem(IForgeRegistry<Item> registry, String name, String modid, Item item) {
		registerItem(registry, name, modid, item, false);
	}

	public static void registerItem(IForgeRegistry<Item> registry, String name, Item item, boolean setTabIcon) {
		item.setRegistryName(AncientSpellcraft.MODID, name);
		item.setTranslationKey(item.getRegistryName().toString());
		registry.register(item);

		if (setTabIcon && item.getCreativeTab() instanceof WizardryTabs.CreativeTabSorted) {
			((WizardryTabs.CreativeTabSorted) item.getCreativeTab()).setIconItem(new ItemStack(item));
		}

		if (item.getCreativeTab() instanceof WizardryTabs.CreativeTabListed) {
			((WizardryTabs.CreativeTabListed) item.getCreativeTab()).order.add(item);
		}
	}

	public static void registerItem(IForgeRegistry<Item> registry, String name, String modid, Item item, boolean setTabIcon) {
		item.setRegistryName(modid, name);
		item.setTranslationKey(item.getRegistryName().toString());
		registry.register(item);

		if (setTabIcon && item.getCreativeTab() instanceof WizardryTabs.CreativeTabSorted) {
			((WizardryTabs.CreativeTabSorted) item.getCreativeTab()).setIconItem(new ItemStack(item));
		}

		if (item.getCreativeTab() instanceof WizardryTabs.CreativeTabListed) {
			((WizardryTabs.CreativeTabListed) item.getCreativeTab()).order.add(item);
		}
	}

	private static void registerItemBlock(IForgeRegistry<Item> registry, Block block) {
		Item itemblock = new ItemBlock(block).setRegistryName(block.getRegistryName());
		registry.register(itemblock);
	}

	private static void registerItemBlock(IForgeRegistry<Item> registry, Block block, Item itemblock) {
		itemblock.setRegistryName(block.getRegistryName());
		registry.register(itemblock);
	}

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Item> event) {

		IForgeRegistry<Item> registry = event.getRegistry();

		//		registerItemBlock(registry, AncientSpellcraftBlocks.SNOW_SLAB);
		//		registerItemBlock(registry, AncientSpellcraftBlocks.ICE_CRAFTING_TABLE);


		registerItemBlock(registry, ASBlocks.SKULL_WATCH, new ItemBlock(ASBlocks.SKULL_WATCH));
		registerItemBlock(registry, ASBlocks.SCRIBING_DESK, new ItemBlock(ASBlocks.SCRIBING_DESK));
		registerItemBlock(registry, ASBlocks.IMBUEMENT_ALTAR_RUINED, new ItemBlock(ASBlocks.IMBUEMENT_ALTAR_RUINED));
		registerItemBlock(registry, ASBlocks.SPHERE_COGNIZANCE, new ItemBlock(ASBlocks.SPHERE_COGNIZANCE));
		registerItemBlock(registry, ASBlocks.SENTINEL_BLOCK_IRON, new ItemBlock(ASBlocks.SENTINEL_BLOCK_IRON));
		registerItemBlock(registry, ASBlocks.SENTINEL_BLOCK_GOLD, new ItemBlock(ASBlocks.SENTINEL_BLOCK_GOLD));
		registerItemBlock(registry, ASBlocks.SENTINEL_BLOCK_DIAMOND, new ItemBlock(ASBlocks.SENTINEL_BLOCK_DIAMOND));
		registerItemBlock(registry, ASBlocks.SENTINEL_BLOCK_LARGE_IRON, new ItemBlock(ASBlocks.SENTINEL_BLOCK_LARGE_IRON));
		registerItemBlock(registry, ASBlocks.SENTINEL_BLOCK_LARGE_GOLD, new ItemBlock(ASBlocks.SENTINEL_BLOCK_LARGE_GOLD));
		registerItemBlock(registry, ASBlocks.SENTINEL_BLOCK_LARGE_DIAMOND, new ItemBlock(ASBlocks.SENTINEL_BLOCK_LARGE_DIAMOND));

		registerItemBlock(registry, ASBlocks.CRYSTAL_ORE_FIRE, new ItemBlock(ASBlocks.CRYSTAL_ORE_FIRE));
		registerItemBlock(registry, ASBlocks.CRYSTAL_ORE_EARTH, new ItemBlock(ASBlocks.CRYSTAL_ORE_EARTH));
		registerItemBlock(registry, ASBlocks.CRYSTAL_ORE_HEALING, new ItemBlock(ASBlocks.CRYSTAL_ORE_HEALING));
		registerItemBlock(registry, ASBlocks.CRYSTAL_ORE_ICE, new ItemBlock(ASBlocks.CRYSTAL_ORE_ICE));
		registerItemBlock(registry, ASBlocks.CRYSTAL_ORE_LIGHTNING, new ItemBlock(ASBlocks.CRYSTAL_ORE_LIGHTNING));
		registerItemBlock(registry, ASBlocks.CRYSTAL_ORE_NECROMANCY, new ItemBlock(ASBlocks.CRYSTAL_ORE_NECROMANCY));
		registerItemBlock(registry, ASBlocks.CRYSTAL_ORE_SORCERY, new ItemBlock(ASBlocks.CRYSTAL_ORE_SORCERY));
		registerItemBlock(registry, ASBlocks.CRYSTAL_SILVER_ORE, new ItemBlock(ASBlocks.CRYSTAL_SILVER_ORE));
		registerItemBlock(registry, ASBlocks.ASTRAL_DIAMOND_ORE, new ItemBlock(ASBlocks.ASTRAL_DIAMOND_ORE));

		registerItemBlock(registry, ASBlocks.DEVORITIUM_ORE, new ItemBlockDevoritiumMaterial(ASBlocks.DEVORITIUM_ORE));
		registerItemBlock(registry, ASBlocks.DEVORITIUM_BLOCK, new ItemBlockDevoritiumMaterial(ASBlocks.DEVORITIUM_BLOCK));
		registerItemBlock(registry, ASBlocks.DEVORITIUM_GILDED_STONE, new ItemBlockDevoritiumMaterial(ASBlocks.DEVORITIUM_GILDED_STONE));
		registerItemBlock(registry, ASBlocks.DEVORITIUM_BARS, new ItemBlockDevoritiumMaterial(ASBlocks.DEVORITIUM_BARS));

		registerItemBlock(registry, ASBlocks.LOG_CRYSTAL_TREE, new ItemBlock(ASBlocks.LOG_CRYSTAL_TREE));
		//		registerItemBlock(registry, AncientSpellcraftBlocks.log_crystal_tree2, new ItemBlock(AncientSpellcraftBlocks.log_crystal_tree2));
		registerItemBlock(registry, ASBlocks.LEAVES_CRYSTAL_TREE, new ItemBlock(ASBlocks.LEAVES_CRYSTAL_TREE));

		registerItemBlock(registry, ASBlocks.DIMENSION_BOUNDARY, new ItemBlock(ASBlocks.DIMENSION_BOUNDARY));
		registerItemBlock(registry, ASBlocks.DIMENSION_FOCUS, new ItemBlock(ASBlocks.DIMENSION_FOCUS));
		registerItemBlock(registry, ASBlocks.ARCANE_ANVIL, new ItemBlock(ASBlocks.ARCANE_ANVIL));
		registerItemBlock(registry, ASBlocks.sage_lectern, new ItemBlock(ASBlocks.sage_lectern));
		registerItemBlock(registry, ASBlocks.master_bolt, new ItemMasterBolt());

		registerItemBlock(registry, ASBlocks.sealed_stone, new ItemBlock(ASBlocks.sealed_stone));
		registerItemBlock(registry, ASBlocks.unsealed_stone, new ItemBlock(ASBlocks.unsealed_stone));
		registerItemBlock(registry, ASBlocks.unseal_button, new ItemBlock(ASBlocks.unseal_button));
		registerItemBlock(registry, ASBlocks.sage_flax, new ItemBlock(ASBlocks.sage_flax) {
			@Override
			public IRarity getForgeRarity(ItemStack stack) {
				return EnumRarity.RARE;
			}

			@Override
			public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
				Wizardry.proxy.addMultiLineDescription(tooltip, "item." + this.getRegistryName() + ".desc", new Object[0]);
			}
		});

		//		registerItemBlock(registry, AncientSpellcraftBlocks.ANCIENT_DIMENSION_BOUNDARY, new ItemBlock(AncientSpellcraftBlocks.ANCIENT_DIMENSION_BOUNDARY));
		registerItem(registry, "sage_tome_novice_magic", new ItemSageTome(Tier.NOVICE, Element.MAGIC));
		registerItem(registry, "sage_tome_apprentice_magic", new ItemSageTome(Tier.APPRENTICE, Element.MAGIC));
		registerItem(registry, "sage_tome_advanced_magic", new ItemSageTome(Tier.ADVANCED, Element.MAGIC));
		registerItem(registry, "sage_tome_master_magic", new ItemSageTome(Tier.MASTER, Element.MAGIC));

		registerItem(registry, "sage_tome_novice_fire", new ItemSageTome(Tier.NOVICE, Element.FIRE));
		registerItem(registry, "sage_tome_apprentice_fire", new ItemSageTome(Tier.APPRENTICE, Element.FIRE));
		registerItem(registry, "sage_tome_advanced_fire", new ItemSageTome(Tier.ADVANCED, Element.FIRE));
		registerItem(registry, "sage_tome_master_fire", new ItemSageTome(Tier.MASTER, Element.FIRE));

		registerItem(registry, "sage_tome_novice_ice", new ItemSageTome(Tier.NOVICE, Element.ICE));
		registerItem(registry, "sage_tome_apprentice_ice", new ItemSageTome(Tier.APPRENTICE, Element.ICE));
		registerItem(registry, "sage_tome_advanced_ice", new ItemSageTome(Tier.ADVANCED, Element.ICE));
		registerItem(registry, "sage_tome_master_ice", new ItemSageTome(Tier.MASTER, Element.ICE));

		registerItem(registry, "sage_tome_novice_lightning", new ItemSageTome(Tier.NOVICE, Element.LIGHTNING));
		registerItem(registry, "sage_tome_apprentice_lightning", new ItemSageTome(Tier.APPRENTICE, Element.LIGHTNING));
		registerItem(registry, "sage_tome_advanced_lightning", new ItemSageTome(Tier.ADVANCED, Element.LIGHTNING));
		registerItem(registry, "sage_tome_master_lightning", new ItemSageTome(Tier.MASTER, Element.LIGHTNING));

		registerItem(registry, "sage_tome_novice_necromancy", new ItemSageTome(Tier.NOVICE, Element.NECROMANCY));
		registerItem(registry, "sage_tome_apprentice_necromancy", new ItemSageTome(Tier.APPRENTICE, Element.NECROMANCY));
		registerItem(registry, "sage_tome_advanced_necromancy", new ItemSageTome(Tier.ADVANCED, Element.NECROMANCY));
		registerItem(registry, "sage_tome_master_necromancy", new ItemSageTome(Tier.MASTER, Element.NECROMANCY));

		registerItem(registry, "sage_tome_novice_earth", new ItemSageTome(Tier.NOVICE, Element.EARTH));
		registerItem(registry, "sage_tome_apprentice_earth", new ItemSageTome(Tier.APPRENTICE, Element.EARTH));
		registerItem(registry, "sage_tome_advanced_earth", new ItemSageTome(Tier.ADVANCED, Element.EARTH));
		registerItem(registry, "sage_tome_master_earth", new ItemSageTome(Tier.MASTER, Element.EARTH));

		registerItem(registry, "sage_tome_novice_sorcery", new ItemSageTome(Tier.NOVICE, Element.SORCERY));
		registerItem(registry, "sage_tome_apprentice_sorcery", new ItemSageTome(Tier.APPRENTICE, Element.SORCERY));
		registerItem(registry, "sage_tome_advanced_sorcery", new ItemSageTome(Tier.ADVANCED, Element.SORCERY));
		registerItem(registry, "sage_tome_master_sorcery", new ItemSageTome(Tier.MASTER, Element.SORCERY));

		registerItem(registry, "sage_tome_novice_healing", new ItemSageTome(Tier.NOVICE, Element.HEALING));
		registerItem(registry, "sage_tome_apprentice_healing", new ItemSageTome(Tier.APPRENTICE, Element.HEALING));
		registerItem(registry, "sage_tome_advanced_healing", new ItemSageTome(Tier.ADVANCED, Element.HEALING));
		registerItem(registry, "sage_tome_master_healing", new ItemSageTome(Tier.MASTER, Element.HEALING));

		registerItem(registry, "warlock_orb_novice_magic", new ItemWarlockOrb(Tier.NOVICE, Element.MAGIC));
		registerItem(registry, "warlock_orb_apprentice_magic", new ItemWarlockOrb(Tier.APPRENTICE, Element.MAGIC));
		registerItem(registry, "warlock_orb_advanced_magic", new ItemWarlockOrb(Tier.ADVANCED, Element.MAGIC));
		registerItem(registry, "warlock_orb_master_magic", new ItemWarlockOrb(Tier.MASTER, Element.MAGIC));

		registerItem(registry, "warlock_orb_novice_earth", new ItemWarlockOrb(Tier.NOVICE, Element.EARTH));
		registerItem(registry, "warlock_orb_apprentice_earth", new ItemWarlockOrb(Tier.APPRENTICE, Element.EARTH));
		registerItem(registry, "warlock_orb_advanced_earth", new ItemWarlockOrb(Tier.ADVANCED, Element.EARTH));
		registerItem(registry, "warlock_orb_master_earth", new ItemWarlockOrb(Tier.MASTER, Element.EARTH));
		registerItem(registry, "warlock_orb_novice_sorcery", new ItemWarlockOrb(Tier.NOVICE, Element.SORCERY));
		registerItem(registry, "warlock_orb_apprentice_sorcery", new ItemWarlockOrb(Tier.APPRENTICE, Element.SORCERY));
		registerItem(registry, "warlock_orb_advanced_sorcery", new ItemWarlockOrb(Tier.ADVANCED, Element.SORCERY));
		registerItem(registry, "warlock_orb_master_sorcery", new ItemWarlockOrb(Tier.MASTER, Element.SORCERY));
		registerItem(registry, "warlock_orb_novice_fire", new ItemWarlockOrb(Tier.NOVICE, Element.FIRE));
		registerItem(registry, "warlock_orb_apprentice_fire", new ItemWarlockOrb(Tier.APPRENTICE, Element.FIRE));
		registerItem(registry, "warlock_orb_advanced_fire", new ItemWarlockOrb(Tier.ADVANCED, Element.FIRE));
		registerItem(registry, "warlock_orb_master_fire", new ItemWarlockOrb(Tier.MASTER, Element.FIRE));
		registerItem(registry, "warlock_orb_novice_ice", new ItemWarlockOrb(Tier.NOVICE, Element.ICE));
		registerItem(registry, "warlock_orb_apprentice_ice", new ItemWarlockOrb(Tier.APPRENTICE, Element.ICE));
		registerItem(registry, "warlock_orb_advanced_ice", new ItemWarlockOrb(Tier.ADVANCED, Element.ICE));
		registerItem(registry, "warlock_orb_master_ice", new ItemWarlockOrb(Tier.MASTER, Element.ICE));
		registerItem(registry, "warlock_orb_novice_lightning", new ItemWarlockOrb(Tier.NOVICE, Element.LIGHTNING));
		registerItem(registry, "warlock_orb_apprentice_lightning", new ItemWarlockOrb(Tier.APPRENTICE, Element.LIGHTNING));
		registerItem(registry, "warlock_orb_advanced_lightning", new ItemWarlockOrb(Tier.ADVANCED, Element.LIGHTNING));
		registerItem(registry, "warlock_orb_master_lightning", new ItemWarlockOrb(Tier.MASTER, Element.LIGHTNING));
		registerItem(registry, "warlock_orb_novice_necromancy", new ItemWarlockOrb(Tier.NOVICE, Element.NECROMANCY));
		registerItem(registry, "warlock_orb_apprentice_necromancy", new ItemWarlockOrb(Tier.APPRENTICE, Element.NECROMANCY));
		registerItem(registry, "warlock_orb_advanced_necromancy", new ItemWarlockOrb(Tier.ADVANCED, Element.NECROMANCY));
		registerItem(registry, "warlock_orb_master_necromancy", new ItemWarlockOrb(Tier.MASTER, Element.NECROMANCY));
		registerItem(registry, "warlock_orb_novice_healing", new ItemWarlockOrb(Tier.NOVICE, Element.HEALING));
		registerItem(registry, "warlock_orb_apprentice_healing", new ItemWarlockOrb(Tier.APPRENTICE, Element.HEALING));
		registerItem(registry, "warlock_orb_advanced_healing", new ItemWarlockOrb(Tier.ADVANCED, Element.HEALING));
		registerItem(registry, "warlock_orb_master_healing", new ItemWarlockOrb(Tier.MASTER, Element.HEALING));

		registerItem(registry, "spectral_shield", new ItemSpectralShield());
		registerItem(registry, "ancient_spellcraft_spell_book", new ItemASSpellBook());
		registerItem(registry, "ancient_spell_book", new ItemASSpellBook());
		registerItem(registry, "ancient_spellcraft_scroll", new ItemScroll());
		registerItem(registry, "enchanted_name_tag", new ItemEnchantedNameTag());

		registerItem(registry, "wizard_hat_ancient", new ItemAncientHat(WizardryItems.Materials.SILK, 1, EntityEquipmentSlot.HEAD, Element.MAGIC));

		registerItem(registry, "battlemage_sword_novice", new ItemBattlemageSword(Tier.NOVICE, 3));
		registerItem(registry, "battlemage_sword_apprentice", new ItemBattlemageSword(Tier.APPRENTICE, 5));
		registerItem(registry, "battlemage_sword_advanced", new ItemBattlemageSword(Tier.ADVANCED, 7));
		registerItem(registry, "battlemage_sword_master", new ItemBattlemageSword(Tier.MASTER, 9));
		registerItem(registry, "battlemage_shield", new ItemBattlemageShield());
		registerItem(registry, "runic_hammer", new ItemRunicHammer());

		registerItem(registry, "devoritium_ingot", new ItemDevoritium());
		registerItem(registry, "devoritium_nugget", new ItemDevoritium());
		registerItem(registry, "devoritium_sword", new ItemDevoritiumSword());
		registerItem(registry, "devoritium_axe", new ItemDevoritiumAxe());
		registerItem(registry, "devoritium_arrow", new ItemDevoritiumArrow().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "devoritium_bomb", new ItemDevoritiumBomb());
		registerItem(registry, "devoritium_door", new ItemDevoritiumDoor(ASBlocks.DEVORITIUM_DOOR));
		registerItem(registry, "devoritium_chestplate", new ItemDevoritiumArmour(AncientSpellcraft.DEVORITIUM_ARMOR_MATERIAL, 0, EntityEquipmentSlot.CHEST));
		registerItem(registry, "devoritium_scimitar", new ItemDevoritiumScimitar());
		registerItem(registry, "devoritium_shield", new ItemDevoritiumShield());
		registerItem(registry, "magic_shield", new ItemMagicShield(EnumRarity.EPIC));

		// ====================== Artefacts ======================

		/// ring
		registerItem(registry, "ring_poison_arrow", new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.RING));
		registerItem(registry, "ring_power", new ItemSetArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.RING, "jewels_of_power", 4, new ArrayList<String>(Arrays.asList("ring_power", "amulet_power", "charm_power_orb"))));
		registerItem(registry, "ring_prismarine", new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.RING));
		registerItem(registry, "ring_mana_transfer", new ItemRingManaTransfer(EnumRarity.UNCOMMON, ItemArtefact.Type.RING));
		registerItem(registry, "ring_mana_lesser", AncientSpellcraft.MODID, new ItemManaArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.RING, 500));
		registerItem(registry, "ring_protector", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.RING));
		registerItem(registry, "ring_lily_flower", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.RING));
		registerItem(registry, "ring_mana_cost", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.RING));
		registerItem(registry, "ring_charge", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.RING));
		registerItem(registry, "ring_solid_air", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.RING));
		registerItem(registry, "ring_lodestone", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_berserker", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_blast", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING) {
			@Override
			@SideOnly(Side.CLIENT)
			public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
				tooltip.add(net.minecraft.client.resources.I18n.format("item." + this.getRegistryName() + ".desc"));
				tooltip.add("\u00A7c" + net.minecraft.client.resources.I18n.format("item." + this.getRegistryName() + ".desc2"));
			}
		});

		registerItem(registry, "ring_range", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING) {
			@Override
			@SideOnly(Side.CLIENT)
			public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
				tooltip.add(net.minecraft.client.resources.I18n.format("item." + this.getRegistryName() + ".desc"));
				tooltip.add("\u00A7c" + net.minecraft.client.resources.I18n.format("item." + this.getRegistryName() + ".desc2"));
			}
		});
		registerItem(registry, "ring_duration", new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING) {
			@Override
			@SideOnly(Side.CLIENT)
			public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
				tooltip.add(net.minecraft.client.resources.I18n.format("item." + this.getRegistryName() + ".desc"));
				tooltip.add("\u00A7c" + net.minecraft.client.resources.I18n.format("item." + this.getRegistryName() + ".desc2"));
			}
		});

		registerItem(registry, "ring_focus_crystal", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_unbinding", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_disenchanter", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_quicksand", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_mana_greater", AncientSpellcraft.MODID, new ItemManaArtefact(EnumRarity.RARE, ItemArtefact.Type.RING, 1000));
		registerItem(registry, "ring_devotion", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_withdraw_life", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_cloudwalker", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_spirit_ward", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_kinetic", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_griefing", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_metamagic_preserve", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_rare_scroll", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_absorb_crystal", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_chaos_blast_multitarget", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_of_force", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_life_drain", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		registerItem(registry, "ring_undeath", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.RING));
		registerItem(registry, "ring_eternal_contingency", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.RING));
		registerItem(registry, "ring_shivering", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.RING));
		registerItem(registry, "ring_permanent_shrinkage", AncientSpellcraft.MODID, new ItemASArtemisLibArtefact(EnumRarity.EPIC, ItemArtefact.Type.RING));
		registerItem(registry, "ring_permanent_growth", AncientSpellcraft.MODID, new ItemASArtemisLibArtefact(EnumRarity.EPIC, ItemArtefact.Type.RING));
		registerItem(registry, "ring_arcane_flames", AncientSpellcraft.MODID, new ItemArcaneFlameRing(EnumRarity.EPIC, ItemArtefact.Type.RING));
		registerItem(registry, "ring_healer", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));

		/// amulet
		registerItem(registry, "amulet_mana", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_rabbit", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_elemental_offense", AncientSpellcraft.MODID, new AbstractItemArtefactWithSlots(EnumRarity.UNCOMMON, ItemArtefact.Type.AMULET, 1, 1, true) {
			@Override
			public boolean isItemValid(Item item) {
				return item instanceof ItemCrystal;
			}
		});
		registerItem(registry, "amulet_talisman_of_affinity", AncientSpellcraft.MODID, new ItemTalismanOfAffinity(EnumRarity.UNCOMMON, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_poison_resistance", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_power", AncientSpellcraft.MODID, new ItemSetArtefact(EnumRarity.RARE, ItemArtefact.Type.AMULET, "jewels_of_power", 4, new ArrayList<String>(Arrays.asList("ring_power", "amulet_power", "charm_power_orb"))));
		registerItem(registry, "amulet_persistence", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_healing_mushroom", AncientSpellcraft.MODID, new ItemAmberMushroomAmulet(EnumRarity.UNCOMMON, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_shield", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_imbued_marble", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_oakflesh", AncientSpellcraft.MODID, new ItemAmuletOaken(EnumRarity.RARE, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_inspiration", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_domus", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_searing_skin", AncientSpellcraft.MODID, new ItemSearingSkinAmulet(EnumRarity.RARE, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_cursed_mirror", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_absorb_potion", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_spellbinding", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_cursed_pendant", AncientSpellcraft.MODID, new ItemAmuletCursedPendant(EnumRarity.RARE, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_elemental_defense", AncientSpellcraft.MODID, new AbstractItemArtefactWithSlots(EnumRarity.RARE, ItemArtefact.Type.AMULET, 1, 1, true) {
			@Override
			public boolean isItemValid(Item item) {
				return item instanceof ItemCrystal;
			}
		});
		registerItem(registry, "amulet_black_tongue", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_of_resistance", AncientSpellcraft.MODID, new ItemAmuletOfResistance(EnumRarity.RARE, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_curse_ward", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_pendant_of_eternity", AncientSpellcraft.MODID, new ItemEternityPendant(EnumRarity.EPIC, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_time_knot", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_arcane_catalyst", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_elemental_aura", AncientSpellcraft.MODID, new ItemElementalAuraAmulet(EnumRarity.EPIC, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_time_slow", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_holy_aura", AncientSpellcraft.MODID, new ItemAmuletHolyAura(EnumRarity.EPIC, ItemArtefact.Type.AMULET));
		registerItem(registry, "amulet_celerity", AncientSpellcraft.MODID, new ItemAmuletOfCelerity(EnumRarity.RARE, ItemArtefact.Type.AMULET));

		/// charm
		registerItem(registry, "charm_mana_flask", AncientSpellcraft.MODID, new ItemEverfullManaFlask(EnumRarity.UNCOMMON, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_ice_cream", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_wild_catalyst", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_sentinel_eye", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_transcribing_tome", AncientSpellcraft.MODID, new ItemTranscribingTome(EnumRarity.UNCOMMON, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_mana_orb", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_specterlight_torch", AncientSpellcraft.MODID, new ItemSpecterLightTorch(EnumRarity.UNCOMMON, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_hungry_chest", AncientSpellcraft.MODID, new ItemHungryChest(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_diamond_goose", AncientSpellcraft.MODID, new ItemDiamondGoose(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_wardrobe", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_seed_bag", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_wizard_tankard", AncientSpellcraft.MODID, new ItemWizardTankard(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_enchanted_needle", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_bucket_coal", AncientSpellcraft.MODID, new ItemCoalBucket(EnumRarity.RARE));
		registerItem(registry, "cornucopia", AncientSpellcraft.MODID, new ItemCornucopia(EnumRarity.RARE));
		registerItem(registry, "charm_gold_bag", AncientSpellcraft.MODID, new ItemGoldBag(EnumRarity.RARE));
		registerItem(registry, "charm_quicksand_walker", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_magic_light", AncientSpellcraft.MODID, new ItemPhialOfEternalFlame(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_burrow", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_hoarders_orb", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_metamagic_amplifier", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_majestic_mana", AncientSpellcraft.MODID, new ItemManaArtefact(EnumRarity.RARE, ItemArtefact.Type.RING, 2500));
		registerItem(registry, "charm_plunderers_mark", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_wizard_ale", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_potion_kit", AncientSpellcraft.MODID, new ItemDimensionalDisk(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_crystal_sickle", AncientSpellcraft.MODID, new ItemCrystalSickle(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_focus_stone", AncientSpellcraft.MODID, new ItemFocusStone(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_meteorite_stone", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_beastcallers_horn", AncientSpellcraft.MODID, new ItemBeastcallersHorn(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItemBlock(registry, ASBlocks.ARTEFACT_PENSIVE, new ItemBlock(ASBlocks.ARTEFACT_PENSIVE) {
			@Override
			public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
				Wizardry.proxy.addMultiLineDescription(tooltip, "item.ancientspellcraft:pensive.desc");
			}

			@Override
			public EnumRarity getRarity(ItemStack stack) {
				return EnumRarity.EPIC;
			}
		});
		registerItem(registry, "charm_knowledge_orb", AncientSpellcraft.MODID, new ItemKnowledgeOrb(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_power_orb", AncientSpellcraft.MODID, new ItemSetArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM, "jewels_of_power", 4, new ArrayList<String>(Arrays.asList("ring_power", "amulet_power", "charm_power_orb"))));
		registerItem(registry, "charm_progression_orb", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_suppression_orb", AncientSpellcraft.MODID, new ItemDevoritiumOrb(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_lightning_orb", AncientSpellcraft.MODID, new ItemElementalOrb(EnumRarity.EPIC, ItemArtefact.Type.CHARM, "ebwizardry:thunderstorm", 6000, Element.LIGHTNING));
		registerItem(registry, "charm_ice_orb", AncientSpellcraft.MODID, new ItemElementalOrb(EnumRarity.EPIC, ItemArtefact.Type.CHARM, "ebwizardry:ice_age", 6000, Element.ICE));
		registerItem(registry, "charm_infernal_stone", AncientSpellcraft.MODID, new ItemInfernalStone(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_earth_orb", AncientSpellcraft.MODID, new ItemElementalOrb(EnumRarity.EPIC, ItemArtefact.Type.CHARM, "ebwizardry:earthquake", 6000, Element.EARTH));
		registerItem(registry, "charm_healing_orb", AncientSpellcraft.MODID, new ItemElementalOrb(EnumRarity.EPIC, ItemArtefact.Type.CHARM, "ebwizardry:font_of_vitality", 6000, Element.HEALING));
		registerItem(registry, "charm_necromancy_orb", AncientSpellcraft.MODID, new ItemElementalOrb(EnumRarity.EPIC, ItemArtefact.Type.CHARM, "ebwizardry:plague_of_darkness", 6000, Element.NECROMANCY));
		registerItem(registry, "charm_fire_orb", AncientSpellcraft.MODID, new ItemElementalOrb(EnumRarity.EPIC, ItemArtefact.Type.CHARM, "ebwizardry:firestorm", 6000, Element.FIRE));
		registerItem(registry, "charm_sorcery_orb", AncientSpellcraft.MODID, new ItemElementalOrb(EnumRarity.EPIC, ItemArtefact.Type.CHARM, "ebwizardry:shockwave", 6000, Element.SORCERY));
		registerItem(registry, "charm_rift_bottle", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.AMULET));
		registerItem(registry, "charm_reanimation", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_evergrowing_crystal", AncientSpellcraft.MODID, new ItemEvergrowingCrystal(EnumRarity.EPIC));
		registerItem(registry, "charm_shadow_blade", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_elemental_grimoire", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_war_horn", AncientSpellcraft.MODID, new ItemHorn(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_wand_upgrade", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_cryostasis", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_prismatic_spray", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_runic_hammer", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_fabrikator_toolkit", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_scissors", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_philosophers_stone", AncientSpellcraft.MODID, new ItemPhiloshopersStone(EnumRarity.EPIC));
		registerItem(registry, "charm_omnicron", AncientSpellcraft.MODID, new ItemOmnicron(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_cube_phasing", AncientSpellcraft.MODID, new ItemCubePhasing(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_cube_duplication", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_spectral_army", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_spectral_tome", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_clover", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_stone_tablet", AncientSpellcraft.MODID, new ItemMoonLetterDictionary(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_elemental_alkahest", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_sage_diary", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_devoritium_magnet", AncientSpellcraft.MODID, new ItemDevoritiumMagnet(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_remnant_cage", AncientSpellcraft.MODID, new ItemRemnantCage(EnumRarity.EPIC));
		registerItem(registry, "charm_aurelian_cup", AncientSpellcraft.MODID, new ItemAurelianCup(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_thousand_anvils", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_dimensional_disk", AncientSpellcraft.MODID, new ItemDimensionalDisk(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_vault_key", AncientSpellcraft.MODID, new ItemVaultKey(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_arcane_diffuser", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_skeleton_ghosts", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_guardian_blade", AncientSpellcraft.MODID, new ItemGuardianBlade(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_rainbowstring", AncientSpellcraft.MODID, new ItemRainbowString(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_cat_eye", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));

		registerItem(registry, "charm_arcane_mirror", AncientSpellcraft.MODID, new ItemArcaneMirror(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_ancient_wand", AncientSpellcraft.MODID, new ItemAncientWand(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_transmutation_cube", AncientSpellcraft.MODID, new ItemCubeTransmutation(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "scepter_mind_control", AncientSpellcraft.MODID, new ItemOverlordScepter());
		registerItem(registry, "purifying_elixir_recipe", AncientSpellcraft.MODID, new ItemLostRecipe(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "large_mana_flask_recipe", AncientSpellcraft.MODID, new ItemLostRecipe(EnumRarity.EPIC, ItemArtefact.Type.CHARM));

		registerItem(registry, "charm_glyph_illumination", AncientSpellcraft.MODID, new ItemGlyphOfIllumination(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_glyph_leeching", AncientSpellcraft.MODID, new ItemGlyphArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_glyph_antigravity", AncientSpellcraft.MODID, new ItemGlyphArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_glyph_vitality", AncientSpellcraft.MODID, new ItemGlyphArtefact.ItemGlyphViality(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_glyph_warden", AncientSpellcraft.MODID, new ItemGlyphArtefact.ItemGlyphWarden(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_glyph_imbuement", AncientSpellcraft.MODID, new ItemGlyphArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_glyph_shield_disable", AncientSpellcraft.MODID, new ItemGlyphArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_glyph_fortification", AncientSpellcraft.MODID, new ItemGlyphArtefact.ItemGlyphFortification(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_glyph_might", AncientSpellcraft.MODID, new ItemGlyphArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_glyph_charge", AncientSpellcraft.MODID, new ItemGlyphArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));

		registerItem(registry, "charm_aura_alacrity", AncientSpellcraft.MODID, new ItemGlyphAuraArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_aura_hatred", AncientSpellcraft.MODID, new ItemGlyphAuraArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_aura_life", AncientSpellcraft.MODID, new ItemGlyphAuraArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_aura_purity", AncientSpellcraft.MODID, new ItemGlyphAuraArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_aura_warding", AncientSpellcraft.MODID, new ItemGlyphAuraArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_aura_wither", AncientSpellcraft.MODID, new ItemGlyphAuraArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_aura_tenacity", AncientSpellcraft.MODID, new ItemGlyphAuraArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_aura_defense", AncientSpellcraft.MODID, new ItemGlyphAuraArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_aura_vulnerability", AncientSpellcraft.MODID, new ItemGlyphAuraArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_monster_charm", AncientSpellcraft.MODID, new ItemMonsterCharm(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_teleportation_stone", AncientSpellcraft.MODID, new ItemTeleportationStone(EnumRarity.RARE, ItemArtefact.Type.CHARM));

		registerItem(registry, "charm_conjuration_inhibitor", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		registerItem(registry, "charm_vessel_of_the_withered_oath", AncientSpellcraft.MODID, new ItemVesselOfTheWitheredOath(EnumRarity.RARE, ItemArtefact.Type.CHARM));

		registerItem(registry, "charm_verdant_crucible", AncientSpellcraft.MODID, new ItemVerdantCrucible(EnumRarity.UNCOMMON));


		registerItem(registry, "body_power_gem", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.BODY));
		registerItem(registry, "body_fire", AncientSpellcraft.MODID, new ItemElementalCloak(EnumRarity.EPIC, ItemArtefact.Type.BODY, Element.FIRE));
		registerItem(registry, "body_ice", AncientSpellcraft.MODID, new ItemElementalCloak(EnumRarity.EPIC, ItemArtefact.Type.BODY, Element.ICE));
		registerItem(registry, "body_lightning", AncientSpellcraft.MODID, new ItemElementalCloak(EnumRarity.EPIC, ItemArtefact.Type.BODY, Element.LIGHTNING));
		registerItem(registry, "body_necromancy", AncientSpellcraft.MODID, new ItemElementalCloak(EnumRarity.EPIC, ItemArtefact.Type.BODY, Element.NECROMANCY));
		registerItem(registry, "body_earth", AncientSpellcraft.MODID, new ItemElementalCloak(EnumRarity.EPIC, ItemArtefact.Type.BODY, Element.EARTH));
		registerItem(registry, "body_sorcery", AncientSpellcraft.MODID, new ItemElementalCloak(EnumRarity.EPIC, ItemArtefact.Type.BODY, Element.SORCERY));
		registerItem(registry, "body_healing", AncientSpellcraft.MODID, new ItemElementalCloak(EnumRarity.EPIC, ItemArtefact.Type.BODY, Element.HEALING));
		registerItem(registry, "tome_controller", new ItemTomeController());

		registerItem(registry, "belt_scroll_holder", AncientSpellcraft.MODID, new ItemBeltScrollHolder(EnumRarity.RARE, ItemArtefact.Type.BELT));
		registerItem(registry, "belt_horse", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.BELT));
		registerItem(registry, "belt_enchanter", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.BELT));
		registerItem(registry, "belt_stone", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.BELT));
		registerItem(registry, "belt_vortex", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.BELT));
		registerItem(registry, "belt_spring_charge", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.BELT));
		registerItem(registry, "belt_soul_scorch", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.BELT));
		registerItem(registry, "belt_temporal_anchor", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.BELT));
		registerItem(registry, "belt_emberguard", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.BELT));

		registerItem(registry, "belt_fire", AncientSpellcraft.MODID, new ItemElementalBelt(EnumRarity.EPIC, ItemArtefact.Type.BELT, Element.FIRE));
		registerItem(registry, "belt_ice", AncientSpellcraft.MODID, new ItemElementalBelt(EnumRarity.EPIC, ItemArtefact.Type.BELT, Element.ICE));
		registerItem(registry, "belt_lightning", AncientSpellcraft.MODID, new ItemElementalBelt(EnumRarity.EPIC, ItemArtefact.Type.BELT, Element.LIGHTNING));
		registerItem(registry, "belt_necromancy", AncientSpellcraft.MODID, new ItemElementalBelt(EnumRarity.EPIC, ItemArtefact.Type.BELT, Element.NECROMANCY));
		registerItem(registry, "belt_earth", AncientSpellcraft.MODID, new ItemElementalBelt(EnumRarity.EPIC, ItemArtefact.Type.BELT, Element.EARTH));
		registerItem(registry, "belt_sorcery", AncientSpellcraft.MODID, new ItemElementalBelt(EnumRarity.EPIC, ItemArtefact.Type.BELT, Element.SORCERY));
		registerItem(registry, "belt_healing", AncientSpellcraft.MODID, new ItemElementalBelt(EnumRarity.EPIC, ItemArtefact.Type.BELT, Element.HEALING));

		registerItem(registry, "belt_enchanted_whetstone", AncientSpellcraft.MODID, new ItemBeltEnchantedWhetstone(EnumRarity.RARE, ItemArtefact.Type.BELT));
		registerItem(registry, "belt_hook", AncientSpellcraft.MODID, new AbstractItemArtefactWithSlots(EnumRarity.EPIC, ItemArtefact.Type.BELT, 1, 1, true) {
					@Override
					public boolean isItemValid(Item item) {
						return item instanceof ItemArtefact;
					}
				}
		);

		registerItem(registry, "head_curse", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.HEAD));
		registerItem(registry, "head_merchant", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.HEAD));
		registerItem(registry, "head_minions", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.HEAD));
		registerItem(registry, "head_ardor_crown", AncientSpellcraft.MODID, new ItemArdorCrown(EnumRarity.EPIC, ItemArtefact.Type.HEAD));
		registerItem(registry, "head_riftstone", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.HEAD));
		registerItem(registry, "head_fortune", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.HEAD));
		registerItem(registry, "head_shield", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.HEAD));
		registerItem(registry, "head_magelight", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.HEAD));
		registerItem(registry, "head_lightning", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.HEAD));
		registerItem(registry, "head_chaos_magic", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.EPIC, ItemArtefact.Type.HEAD));
		registerItem(registry, "head_minion_circle", AncientSpellcraft.MODID, new ItemCircletOfCircling(EnumRarity.EPIC, ItemArtefact.Type.HEAD));
		registerItem(registry, "head_whispering_veil", AncientSpellcraft.MODID, new ItemWhisperingVeil(EnumRarity.EPIC, ItemArtefact.Type.HEAD));
		registerItem(registry, "head_disruptor_crown", AncientSpellcraft.MODID, new ItemDisruptorCrown(EnumRarity.EPIC, ItemArtefact.Type.HEAD));
		registerItem(registry, "head_circlet_of_the_unknown", AncientSpellcraft.MODID, new ItemASArtefact(EnumRarity.RARE, ItemArtefact.Type.HEAD));
		registerItem(registry, "head_mask_of_perseigni", AncientSpellcraft.MODID, new ItemMaskOfPerseigni(EnumRarity.RARE, ItemArtefact.Type.HEAD));

		/// misc

		registerItem(registry, "stone_tablet_small", new ItemRelic("stone_tablet_small", EnumRarity.COMMON));
		registerItem(registry, "stone_tablet", new ItemRelic("stone_tablet", EnumRarity.UNCOMMON));
		registerItem(registry, "stone_tablet_large", new ItemRelic("stone_tablet_large", EnumRarity.RARE));
		registerItem(registry, "stone_tablet_grand", new ItemRelic("stone_tablet_grand", EnumRarity.EPIC));
		registerItem(registry, "ancient_mana_flask", new ItemRelic("ancient_mana_flask", EnumRarity.UNCOMMON).setCreativeTab(null));
		registerItem(registry, "ancient_bound_stone", new ItemRelic("ancient_bound_stone", EnumRarity.EPIC).setCreativeTab(null));

		registerItem(registry, "shadow_blade", new ItemShadowBlade());
		registerItem(registry, "spectral_fishing_rod", new ItemSpectralFishingRod());
		registerItem(registry, "ice_shield", new ItemIceShield());
		registerItem(registry, "stone_fist", new ItemStoneFist());
		registerItem(registry, "advanced_stone_fist", new ItemAdvancedStoneFist());
		registerItem(registry, "sacred_mace", new ItemSacredMace());

		registerItem(registry, "astral_diamond_shard", new Item().setMaxDamage(0).setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "astral_diamond_charged", new ItemChargedAstralDiamond());

		registerItem(registry, "crystal_shard_fire", new Item().setMaxDamage(0).setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "crystal_shard_earth", new Item().setMaxDamage(0).setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "crystal_shard_healing", new Item().setMaxDamage(0).setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "crystal_shard_ice", new Item().setMaxDamage(0).setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "crystal_shard_lightning", new Item().setMaxDamage(0).setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "crystal_shard_necromancy", new Item().setMaxDamage(0).setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "crystal_shard_sorcery", new Item().setMaxDamage(0).setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "battlemage_sword_hilt", new Item().setMaxDamage(0).setMaxStackSize(1).setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "battlemage_sword_blade", new Item().setMaxDamage(0).setMaxStackSize(1).setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "crystal_silver_nugget", new ItemArmourUpgradeMaterial());
		registerItem(registry, "crystal_silver_ingot", new ItemArmourUpgradeMaterial());
		registerItem(registry, "enchanted_filament", new ItemArmourUpgradeMaterial());
		registerItem(registry, "enchanted_page", new ItemArmourUpgradeMaterial());
		registerItem(registry, "ethereal_essence", new ItemArmourUpgradeMaterial());
		registerItem(registry, "battlemage_contract", new ItemBattlemageContract());

		registerItem(registry, "wand_channeling", new Item().setMaxDamage(0).setCreativeTab(null));

		registerItem(registry, "transmutation_scroll", new ItemTransmutationScroll());
		registerItem(registry, "bartering_scroll", new ItemBarterScroll());
		registerItem(registry, "knowledge_scroll", new ItemKnowledgeScroll());
		registerItem(registry, "duplication_scroll", new ItemDuplicationScroll());
		registerItem(registry, "amnesia_scroll", new ItemAmnesiaScroll());
		registerItem(registry, "unsealing_scroll", new ItemUnsealingScroll());
		registerItem(registry, "imbuement_scroll", new ItemImbuementScroll());
		registerItem(registry, "empty_theory_scroll", new ItemTheoryScroll());
		registerItem(registry, "theory_scroll", new ItemTheoryScroll());
		//		registerItem(registry, "mystic_scroll", new ItemMysticScroll());
		//		registerItem(registry, "blank_mystic_scroll", new ItemBlankMysticScroll());

		registerItem(registry, "blank_rune", new Item().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT).setMaxStackSize(16));
		registerItem(registry, "rune_feoh", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_uruz", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_thurisaz", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_ansuz", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_raido", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_kaunan", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_gyfu", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_wynn", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_haglaz", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_naudiz", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_isaz", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_jera", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_ihwaz", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_peorth", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_algiz", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_sowilo", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_tiwaz", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_berkanan", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_ehwaz", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_mannaz", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_laguz", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_yngvi", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_odal", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));
		registerItem(registry, "rune_dagaz", new ItemRune().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT));

		registerItem(registry, "ritual_book", new ItemRitualBook());
		registerItem(registry, "arcane_compound", new Item().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT).setMaxStackSize(16));

		registerItem(registry, "soulbound_upgrade", new ItemSoulboundWandUpgrade());
		registerItem(registry, "sentience_upgrade", new ItemWandUpgradeAS());
		registerItem(registry, "empowerment_upgrade", new ItemEmpowermentTomeUpgrade());

		registerItem(registry, "spectral_shovel", new ItemSpectralShovel());
		registerItem(registry, "ice_cream", new ItemIceCream());
		registerItem(registry, "alchemical_essence", new ItemAlchemicalEssence());

		registerItem(registry, "runic_plate", new ItemRunicPlate(), true);
		registerItem(registry, "mystic_spell_book", new ItemSageSpellBook());
		registerItem(registry, "empty_mystic_spell_book", new ItemEmptyMysticSpellBook().setCreativeTab(ASTabs.ANCIENTSPELLCRAFT).setMaxStackSize(16));
		registerItem(registry, "forbidden_tome", new ItemWarlockSpellBook());

	}

	public static void registerDispenseBehaviours() {
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ancient_spellcraft_scroll, new BehaviourSpellDispense());

		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(devoritium_bomb, new BehaviorProjectileDispense() {
			@Override
			protected IProjectile getProjectileEntity(World world, IPosition position, ItemStack stack) {
				EntityDevoritiumBomb entity = new EntityDevoritiumBomb(world);
				entity.setPosition(position.getX(), position.getY(), position.getZ());
				return entity;
			}
		});

	}

}
