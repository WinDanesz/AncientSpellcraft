package com.windanesz.ancientspellcraft;

import electroblob.wizardry.Wizardry;
import electroblob.wizardry.item.ItemArtefact;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.List;

import static electroblob.wizardry.Settings.ARTEFACTS_CATEGORY;
import static electroblob.wizardry.Settings.toResourceLocations;

@Config(modid = AncientSpellcraft.MODID, name = "AncientSpellcraft")
// No fancy configs here so we can use the annotation, hurrah!
public class Settings {

	public ResourceLocation[] lootInjectionLocations = toResourceLocations(generalSettings.DEFAULT_LOOT_INJECTION_LOCATIONS);

	public ResourceLocation[] artefactInjectionLocations = toResourceLocations(generalSettings.ARTEFACT_INJECTION_LOCATIONS);

	public ResourceLocation[] voidCreeperBiomeBlacklist = toResourceLocations(generalSettings.void_creeper_biome_blacklist);
	public ResourceLocation[] skeletonMageBiomeBlacklist = toResourceLocations(generalSettings.skeleton_mage_biome_blacklist);

	public List<ResourceLocation> shardEarthShardBiomeWhitelist = Arrays.asList(toResourceLocations(generalSettings.earth_shard_biome_whitelist));
	public List<ResourceLocation> shardSorceryShardBiomeWhitelist = Arrays.asList(toResourceLocations(generalSettings.sorcery_shard_biome_whitelist));
	public List<ResourceLocation> shardNecromancyShardBiomeWhitelist = Arrays.asList(toResourceLocations(generalSettings.necromancy_shard_biome_whitelist));
	public List<ResourceLocation> shardHealingBiomeWhitelist = Arrays.asList(toResourceLocations(generalSettings.healing_shard_biome_whitelist));
	public List<ResourceLocation> shardLightningBiomeWhitelist = Arrays.asList(toResourceLocations(generalSettings.lightning_shard_biome_whitelist));
	public List<ResourceLocation> shardFireBiomeWhitelist = Arrays.asList(toResourceLocations(generalSettings.fire_shard_biome_whitelist));
	public List<ResourceLocation> shardIceBiomeWhitelist = Arrays.asList(toResourceLocations(generalSettings.ice_shard_biome_whitelist));

	public ResourceLocation[] battlemageCampFiles = {new ResourceLocation(AncientSpellcraft.MODID, "battlemage_camp_chest_0"), new ResourceLocation(AncientSpellcraft.MODID, "battlemage_camp_0")};
	public ResourceLocation[] battlemageKeepFiles = {new ResourceLocation(AncientSpellcraft.MODID, "battlemage_keep_chest_0"), new ResourceLocation(AncientSpellcraft.MODID, "battlemage_keep_chest_1"), new ResourceLocation(AncientSpellcraft.MODID, "battlemage_keep_0"), new ResourceLocation(AncientSpellcraft.MODID, "battlemage_keep_1")};

	public ResourceLocation[] sageHillWithChestFiles = {new ResourceLocation(AncientSpellcraft.MODID, "sage_hill_chest_0"), new ResourceLocation(AncientSpellcraft.MODID, "sage_hill_chest_1"), new ResourceLocation(AncientSpellcraft.MODID, "sage_hill_chest_2"), new ResourceLocation(AncientSpellcraft.MODID, "sage_hill_chest_3"), new ResourceLocation(AncientSpellcraft.MODID, "sage_hill_chest_4"), new ResourceLocation(AncientSpellcraft.MODID, "sage_hill_chest_5"), new ResourceLocation(AncientSpellcraft.MODID, "sage_hill_chest_6"), new ResourceLocation(AncientSpellcraft.MODID, "sage_hill_chest_7"), new ResourceLocation(AncientSpellcraft.MODID, "sage_hill_0"), new ResourceLocation(AncientSpellcraft.MODID, "sage_hill_0"), new ResourceLocation(AncientSpellcraft.MODID, "sage_hill_0"), new ResourceLocation(AncientSpellcraft.MODID, "sage_hill_0"), new ResourceLocation(AncientSpellcraft.MODID, "sage_hill_0"), new ResourceLocation(AncientSpellcraft.MODID, "sage_hill_0"), new ResourceLocation(AncientSpellcraft.MODID, "sage_hill_0"), new ResourceLocation(AncientSpellcraft.MODID, "sage_hill_0")};
	public ResourceLocation[] ancientVaultFiles = {new ResourceLocation(AncientSpellcraft.MODID, "ancient_vault_0"), new ResourceLocation(AncientSpellcraft.MODID, "ancient_vault_1")};
	public ResourceLocation[] fallenTowerFiles = {new ResourceLocation(AncientSpellcraft.MODID, "fallen_tower_0")};
	public ResourceLocation[] ancientTempleFiles = {new ResourceLocation(AncientSpellcraft.MODID, "ancient_temple_0")};
	public ResourceLocation[] bookVaultFiles = {new ResourceLocation(AncientSpellcraft.MODID, "bookvault_0")};

	/**
	 * Helper method to figure out if an item was disabled in the ebwiz configs, as unfortunately itemArtefact#enabled private and has no getter method
	 *
	 * @param artefact to check
	 * @return true if the item is enabled (or if it has no config)
	 */
	public static boolean isArtefactEnabled(Item artefact) {
		if (artefact instanceof ItemArtefact && (Wizardry.settings.getConfigCategory(ARTEFACTS_CATEGORY).containsKey(artefact.getRegistryName().toString()))) {
			return (Wizardry.settings.getConfigCategory(ARTEFACTS_CATEGORY).get(artefact.getRegistryName().toString()).getBoolean());
		}

		// no setting to control this item so it shouldn't be disabled..
		return true;
	}

	@SuppressWarnings("unused")
	@Mod.EventBusSubscriber(modid = AncientSpellcraft.MODID)
	private static class EventHandler {
		/**
		 * Inject the new values and save to the config file when the config has been changed from the GUI.
		 *
		 * @param event The event
		 */
		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
			if (event.getModID().equals(AncientSpellcraft.MODID)) {
				ConfigManager.sync(AncientSpellcraft.MODID, Config.Type.INSTANCE);
			}
		}
	}

	@Config.Name("General Settings")
	@Config.LangKey("settings.ancientspellcraft:general_settings")
	public static GeneralSettings generalSettings = new GeneralSettings();

	@Config.Name("Client Settings")
	@Config.LangKey("settings.ancientspellcraft:client_settings")
	public static ClientSettings clientSettings = new ClientSettings();

	public static class GeneralSettings {

		@Config.Name("JEI integration")
		@Config.Comment("Enables or disables the JEI integration of the mod")
		@Config.RequiresMcRestart
		public boolean jei_integration = true;

		@Config.Name("ArtemisLib integration")
		@Config.Comment("Enables or disables the ArtemisLib integration of the mod")
		@Config.RequiresMcRestart
		public boolean artemislib_integration = true;

		@Config.Name("Antique Atlas Integration")
		@Config.Comment("Enables or disables the Antique Atlas integration of the mod")
		@Config.RequiresMcRestart
		public boolean antique_atlas_integration = true;

		@Config.Name("Battlemage Camp Map Markers")
		@Config.Comment("[Server-only] Controls whether AS automatically places antique atlas markers at the locations of battlemage camps")
		public boolean auto_battlemage_camp_markers = true;

		@Config.Name("Battlemage Keep Map Markers")
		@Config.Comment("[Server-only] Controls whether AS automatically places antique atlas markers at the locations of battlemage keeps")
		public boolean auto_battlemage_keep_markers = true;

		@Config.Name("Ancient Vault Map Markers")
		@Config.Comment("[Server-only] Controls whether AS automatically places antique atlas markers at the locations of ancient vaults")
		public boolean ancient_vault_markers = true;

		@Config.Name("Generate Crystal Ore Shards")
		@Config.Comment("Determines whether to generate elemental crystal shards in the Overworld or not")
		@Config.RequiresMcRestart
		public boolean generate_ore_shards = true;

		@Config.Name("Generate Devoritium Ore")
		@Config.Comment("Determines whether to generate devoritium ore blocks in the Overworld or not")
		@Config.RequiresMcRestart
		public boolean generate_devoritium_ore = true;

		@Config.Name("Sphere Spell Identify Chance")
		@Config.Comment("The chance of identifying unknown spells when researching them with the Sphere of Cognizance. This doesn't affects the other hint texts given by the Sphere. 0 = never identify a spell, 1.0 = always")
		@Config.RequiresMcRestart
		public double sphere_spell_identification_chance = 0.05D;

		@Config.Name("Loot Inject Locations")
		@Config.Comment("List of loot tables to inject Ancient Spellcraft loot (as specified in loot_tables/chests/dungeon_additions.json) into. This currently includes stuff like Stone Tablets.")
		public String[] DEFAULT_LOOT_INJECTION_LOCATIONS = {"ebwizardry:chests/wizard_tower", "ebwizardry:chests/shrine", "ebwizardry:chests/obelisk",

				"minecraft:chests/desert_pyramid", "minecraft:chests/jungle_temple", "minecraft:chests/stronghold_corridor", "minecraft:chests/stronghold_crossing", "minecraft:chests/stronghold_library", "minecraft:chests/igloo_chest", "minecraft:chests/woodland_mansion", "minecraft:chests/end_city_treasure"};

		@Config.Name("Amulet of Resistance Mana Capacity")
		@Config.Comment("List of loot tables to inject Ancient Spellcraft artefacts into.")
		public int amulet_of_resistance_mana_capacity = 600;

		@Config.Name("Amulet of Celerity Speed Bonus")
		@Config.Comment("The movement speed bonus granted by the Amulet of Celerity, as a multiplier (0.1 = +10% speed)")
		@Config.RequiresMcRestart
		@Config.RangeDouble(min = 0.0, max = 2.0)
		public double amulet_of_celerity_speed_bonus = 0.1D;

		@Config.Name("Aurelian Cup Absorption Duration")
		@Config.Comment("The duration in seconds of the Absorption effect granted by the Aurelian Cup when drinking potions")
		@Config.RequiresMcRestart
		@Config.RangeInt(min = 1, max = 300)
		public int aurelian_cup_absorption_duration = 10;

		@Config.Name("Beastcaller's Flute Cooldown")
		@Config.Comment("The cooldown in seconds for the Beastcaller's Flute charm")
		@Config.RequiresMcRestart
		@Config.RangeInt(min = 60, max = 3600)
		public int beastcallers_flute_cooldown = 300;

		@Config.Name("Ancient Wand Max Spell Tier")
		@Config.Comment("Max Spell Tier for Ancient Wands (0-3")
		public int ancient_wand_max_spell_tier = 2;

		@Config.Name("Artefact Inject locations")
		@Config.Comment("List of loot tables to inject Ancient Spellcraft artefacts into.")
		private String[] ARTEFACT_INJECTION_LOCATIONS = {"ebwizardry:subsets/uncommon_artefacts", "ebwizardry:subsets/rare_artefacts", "ebwizardry:subsets/epic_artefacts"};

//		@Config.Name("Imbuement Scroll Recipe List")
//		@Config.Comment("List of recipes for the Scroll of Imbuement. Format: input|auto/[ice/fire/lightning/sorcery/necromancy/healing/earth]|output"
//				+ "input: an item's registry name in namespace:path format e.g. minecraft:stick or ebwizardry:magic_crystal. Metadata is optional, can be appended with a :<metadata> suffix"
//				+ "auto/[ice/fire/lightning/sorcery/necromancy/healing/earth]: if you provide the 'auto' value, you don't need to provide an output at all and the mod will try to find matching alternatives of the items based on their registry name"
//				+ "output: if you have provided an element in the second parameter, you must provide an output item in the same item format as you did with the 'input'.")
//		private String[] IMBUEMENT_SCROLL_RECIPE_LIST = {
//		};

		@Config.Name("Extension Spell Potion Blacklist")
		@Config.Comment("List of potion effect which cannot be extended with the Extension sage spell.")
		public String[] extension_spell_blacklist = {};

		@Config.Name("Absorb Artefact Blacklist")
		@Config.Comment("Artefacts Which cannot be absorbed. The default list contains artefacts which won't work in an absorbed form. You can add entries for balancing.")
		public String[] absorb_artefact_blacklist = {"ancientspellcraft:charm_philosophers_stone", "ancientspellcraft:cornucopia", "ancientspellcraft:charm_bucket_coal", "ancientspellcraft:charm_evergrowing_crystal", "ancientspellcraft:charm_gold_bag",

		};

		@Config.Name("Scroll Items Bought By Wizards Using The Crown of The Merchant King Artefact")
		@Config.Comment("Wizards will always offer 1 magic crystal for these items. Be sure to not remove all scrolls (that are instance of ItemScroll) or the item won't work, but otherwise," + "you can add pretty much any items, not just scrolls")
		public String[] scroll_items_for_crown_of_the_merchant_king = {"ebwizardry:scroll", "ancientspellcraft:ancient_spellcraft_scroll"};

		@Config.Name("Additional Items Accepted by the Golden Scroll Holder")
		@Config.Comment("These have no effect and they are just included for roleplay." + "you can add pretty much any items, not just scrolls")
		public String[] golden_scroll_holder_additional_items = {"minecraft:paper", "minecraft:map", "minecraft:filled_map", "minecraft:enchanted_book", "minecraft:writable_book", "minecraft:written_book"};

		@Config.Name("Expertiment Debuff Blacklist")
		@Config.Comment("List of negative potion effect which cannot occur as a side effect of the Experiment spell. Must be in a 'modid:potion_registry_name' format.")
		public String[] experiment_debuff_blacklist = {};

		@Config.Name("Expertiment Buff Blacklist")
		@Config.Comment("List of postive potion effect which cannot occur as a side effect of the Experiment spell. Must be in a 'modid:potion_registry_name' format.")
		public String[] experiment_buff_blacklist = {

		};

		@Config.Name("Essence Extraction Screen Shake")
		@Config.Comment("Determines whether to the Essence Extraction spell shakes the screen while extracting powerful blocks or not.")
		@Config.RequiresMcRestart
		public boolean shake_screen = true;

		@Config.Name("Baubles Integration")
		@Config.Comment("Enable/Disable Baubles integration for the new artefact types (belt, helm, etc). This does NOT affect Electroblob's Wizardry's own Baubles support implementation (ring, amulet, charm)!")
		@Config.RequiresMcRestart
		public boolean baubles_integration = true;

		@Config.Name("Void Creeper Spawn Rate")
		@Config.Comment("Spawn rate for naturally-spawned void creepers; higher numbers mean more void creepers will spawn. Set to 0 do disable spawning entirely")
		@Config.RequiresMcRestart
		public int void_creeper_spawn_rate = 2;

		@Config.Name("Skeleton Mage Spawn Rate")
		@Config.Comment("Spawn rate for naturally-spawned void Skeleton Mages; higher numbers mean more Skeleton Mages will spawn. Set to 0 do disable spawning entirely")
		@Config.RequiresMcRestart
		public int skeleton_mage_spawn_rate = 3;

		@Config.Name("Skeleton Mage Max Group Size")
		@Config.Comment("Max group size for naturally-spawned Skeleton Mages; higher numbers mean more Skeleton Mages will spawn. Set to 0 do disable spawning entirely")
		@Config.RequiresMcRestart
		public int skeleton_mage_max_group_size = 3;

		@Config.Name("Class Armour Evil Wizard Spawn Rate")
		@Config.Comment("Spawn rate for naturally-spawned class (sage, warlock, battlemage) wizards; higher numbers mean more wizards will spawn.\n5 is equivalent to witches, 100 is equivalent to zombies, skeletons and creepers.\nSet to 0 to disable evil wizard spawning entirely.")
		@Config.RequiresMcRestart
		@Config.RangeInt(min = 0, max = 100)
		public int evil_class_wizard_spawn_rate = 2;

		@Config.Name("Spellblade base damage")
		@Config.Comment("The base damage of spellblades, not accounting for the tiers")
		@Config.RequiresMcRestart
		@Config.RangeInt(min = 1, max = 32)
		public int spellblade_base_damage = 2;

		@Config.Name("Spellblade damage increase per tier")
		@Config.Comment("The damage increase per tier of the spellblade")
		@Config.RequiresMcRestart
		@Config.RangeInt(min = 1, max = 6)
		public int spellblade_damage_increase_per_tier = 1;

		@Config.Name("Spellblade charge progress per spellcast")
		@Config.Comment("The amount of charge received for each spellcasts with the spellblade")
		@Config.RequiresMcRestart
		@Config.RangeInt(min = 0, max = 100)
		public int spellblade_charge_gain_per_spellcast = 20;

		@Config.Name("Spellblade charge progress per hit")
		@Config.Comment("The amount of charge received for each hit with the spellblade")
		@Config.RequiresMcRestart
		@Config.RangeInt(min = 0, max = 100)
		public int spellblade_charge_gain_per_hit = 5;

		@Config.Name("Spellblade basic attack mana cost")
		@Config.Comment("The amount of mana required & consumed when the spellblade is used to hit a target")
		@Config.RequiresMcRestart
		@Config.RangeInt(min = 1, max = 100)
		public int spellblade_base_mana_cost = 5;

		@Config.Name("Spellblade base mana per tier")
		@Config.Comment("The amount of mana a spell blade has by default for each tier,")
		@Config.RequiresMcRestart
		public int[] spell_blade_base_mana_per_tier = {150, 300, 600, 900};

		@Config.Name("Orb Artefact Potency Percent Bonus")
		@Config.Comment("Determines the potency bonus of the elemental orb artefacts in a percentage value")
		@Config.RequiresMcRestart
		@Config.RangeInt(min = 0, max = 100)
		public int orb_artefact_potency_bonus = 30;

		@Config.Name("Cloak Potency Percent Bonus")
		@Config.Comment("Determines the potency bonus of the elemental cloaks in a percentage value")
		@Config.RequiresMcRestart
		@Config.RangeInt(min = 0, max = 100)
		public int cloak_potency_bonus = 15;

		@Config.Name("Cloak of Levitation Max Charges")
		@Config.Comment("Maximum levitation charges for the Cloak of Levitation. Each charge equals 1 second of flight.")
		@Config.RangeInt(min = 1, max = 300)
		public int cloak_of_levitation_max_charges = 12;

		@Config.Name("Cloak of Levitation Recharge Rate (ticks per charge)")
		@Config.Comment("Number of ticks required to regenerate one charge unit for the Cloak of Levitation. 20 ticks = 1 second. Default: 60 (3 seconds per charge).")
		@Config.RangeInt(min = 1, max = 6000)
		public int cloak_of_levitation_recharge_ticks = 60;

		@Config.Name("Talisman of Affinity Percent Chance for Elemental Spells")
		@Config.Comment("Determines the chance of getting the talisman's currently bound crystal's element when looting spell books. Note that even at 100% this is not guaranteed to be always the desired element.")
		@Config.RequiresMcRestart
		@Config.RangeDouble(min = 0, max = 1)
		public double talisman_of_affinity_chance = 0.4;

		@Config.Name("Void Creeper Biome Blacklist")
		@Config.Comment("List of Biomes where Void Creepers will never spawn.")
		@Config.RequiresMcRestart
		public String[] void_creeper_biome_blacklist = {"mushroom_island", "mushroom_island_shore"};

		@Config.Name("Skeleton Mage Biome Blacklist")
		@Config.Comment("List of Biomes where Skeleton Mages will never spawn.")
		@Config.RequiresMcRestart
		public String[] skeleton_mage_biome_blacklist = {"mushroom_island", "mushroom_island_shore"};

		@Config.Name("Void Creeper Dimension Whitelist")
		@Config.Comment("List of Dimensions where Void Creepers are allowed to spawn. Defaults to Overworld only." + "\n make")
		@Config.RequiresMcRestart
		public Integer[] void_creeper_dimension_whitelist = {0};

		@Config.Name("Skeleton Mage Dimension Whitelist")
		@Config.Comment("List of Dimensions where Skeleton Mages are allowed to spawn. Defaults to Overworld only." + "\n make")
		@Config.RequiresMcRestart
		public Integer[] skeleton_mage_dimension_whitelist = {0};

		@Config.Name("Elemental Fire Crystal Shard Biome List")
		@Config.Comment("List of Biomes where Fire Crystal Shards can spawn.")
		@Config.RequiresMcRestart
		public String[] fire_shard_biome_whitelist = {"desert", "desert_hills", "mutated_desert"};

		@Config.Name("Elemental Earth Crystal Shard Biome List")
		@Config.Comment("List of Biomes where Earth Crystal Shards can spawn.")
		@Config.RequiresMcRestart
		public String[] earth_shard_biome_whitelist = {"forest", "birch_forest", "roofed_forest"};

		@Config.Name("Elemental Sorcery Crystal Shard Biome List")
		@Config.Comment("List of Biomes where Sorcery Crystal Shards can spawn.")
		@Config.RequiresMcRestart
		public String[] sorcery_shard_biome_whitelist = {"plains", "mutated_plains"};

		@Config.Name("Elemental Necromancy Crystal Shard Biome List")
		@Config.Comment("List of Biomes where Necromancy Crystal Shards can spawn.")
		@Config.RequiresMcRestart
		public String[] necromancy_shard_biome_whitelist = {"swampland", "mutated_swampland"};

		@Config.Name("Elemental Healing Crystal Shard Biome List")
		@Config.Comment("List of Biomes where Healing Crystal Shards can spawn.")
		@Config.RequiresMcRestart
		public String[] healing_shard_biome_whitelist = {"jungle", "jungle_hills", "jungle_edge"};

		@Config.Name("Elemental Lightning Crystal Shard Biome List")
		@Config.Comment("List of Biomes where Lightning Crystal Shards can spawn.")
		@Config.RequiresMcRestart
		public String[] lightning_shard_biome_whitelist = {"extreme_hills", "smaller_extreme_hills", "extreme_hills_with_trees", "mutated_extreme_hills", "mutated_extreme_hills_with_trees"};

		@Config.Name("Use biomes for Skeleton and Ghost Mage elements")
		@Config.Comment("This setting decides how much the other skeleton and ghost mage biome settings matter (chance to consider the lists)." + "If 1 = biome whitelist settings always apply for element selection" + "if 0 = elements are always random, and the biome lists won't be considered")
		@Config.RequiresMcRestart
		public float use_biomes_for_mage_elements = 1.0f;

		@Config.Name("Elemental Ice Crystal Shard Biome List")
		@Config.Comment("List of Biomes where Ice Crystal Shards can spawn.")
		@Config.RequiresMcRestart
		public String[] ice_shard_biome_whitelist = {"taiga", "taiga_hills", "taiga_cold", "taiga_cold_hills", "mutated_taiga", "mutated_taiga_cold"};

		@Config.Name("Fire Skeleton and Ghost Mage Biome List")
		@Config.Comment("List of Biomes where Fire Skeleton and Ghost Mages can spawn.")
		@Config.RequiresMcRestart
		public String[] fire_skeleton_and_ghost_biome_whitelist = {"desert", "desert_hills", "mutated_desert"};

		@Config.Name("Earth Skeleton and Ghost Mage Biome List")
		@Config.Comment("List of Biomes where Earth Skeleton and Ghost Mages can spawn.")
		@Config.RequiresMcRestart
		public String[] earth_skeleton_and_ghost_biome_whitelist = {"forest", "birch_forest", "roofed_forest"};

		@Config.Name("Sorcery Skeleton and Ghost Mage Biome List")
		@Config.Comment("List of Biomes where Sorcery Skeleton and Ghost Mages can spawn.")
		@Config.RequiresMcRestart
		public String[] sorcery_skeleton_and_ghost_biome_whitelist = {"plains", "mutated_plains"};

		@Config.Name("Necromancy Skeleton and Ghost Mage Biome List")
		@Config.Comment("List of Biomes where Necromancy Skeleton and Ghost Mages can spawn.")
		@Config.RequiresMcRestart
		public String[] necromancy_skeleton_and_ghost_biome_whitelist = {"swampland", "mutated_swampland"};

		@Config.Name("Healing Skeleton and Ghost Mage Biome List")
		@Config.Comment("List of Biomes where Healing Skeleton and Ghost Mages can spawn.")
		@Config.RequiresMcRestart
		public String[] healing_skeleton_and_ghost_biome_whitelist = {"jungle", "jungle_hills", "jungle_edge"};

		@Config.Name("Lightning Skeleton and Ghost Mage Biome List")
		@Config.Comment("List of Biomes where Lightning Skeleton and Ghost Mages can spawn.")
		@Config.RequiresMcRestart
		public String[] lightning_skeleton_and_ghost_biome_whitelist = {"extreme_hills", "smaller_extreme_hills", "extreme_hills_with_trees", "mutated_extreme_hills", "mutated_extreme_hills_with_trees"};

		@Config.Name("Ice Skeleton and Ghost Mage Biome List")
		@Config.Comment("List of Biomes where Ice Skeleton and Ghost Mages can spawn.")
		@Config.RequiresMcRestart
		public String[] ice_skeleton_and_ghost_biome_whitelist = {"taiga", "taiga_hills", "redwood_taiga_hills", "taiga_cold", "taiga_cold_hills", "mutated_taiga", "mutated_taiga_cold"};


		@Config.Name("[UNUSED] Pocket Biome registry ID")
		@Config.Comment("Allows you to change the pocket biome registry ID if you encounter biome ID conflicts")
		@Config.RequiresMcRestart
		public int pocket_biome_registry_id = 168;

		@Config.Name("Runic Shield Durability (mana)")
		@Config.RequiresMcRestart
		@Config.RangeInt(min = 1, max = 10000)
		public int runic_shield_durability = 1000;

		@Config.Name("Immobility Contingency Spell Trigger Effects")
		@Config.Comment("List of potion effects which can be considered as an immobilizing effect. Receiving one of these will trigger the stored Contingency - Immobility spell")
		@Config.RequiresMcRestart
		public String[] immobility_contingency_effects = {"ebwizardry:paralysis", "ebwizardry:containment", "ebwizardry:slow_time", "ebwizardry:frost", "minecraft:slowness"};

		@Config.Name("Curses that the Dispel Item Curse spell can remove. Should work with modded enchantments as well.")
		@Config.RequiresMcRestart
		public String[] dispel_item_curse_list = {"minecraft:vanishing_curse", "minecraft:binding_curse", "somanyenchantments:bluntness", "somanyenchantments:curseofdecay", "somanyenchantments:curseofholding", "somanyenchantments:curseofinaccuracy", "somanyenchantments:curseofpossession", "somanyenchantments:curseofvulnerability", "somanyenchantments:cursededge", "somanyenchantments:inefficent", "somanyenchantments:instability", "somanyenchantments:powerless", "somanyenchantments:rusted", "somanyenchantments:heavyweight", "somanyenchantments:curseofvulnerability", "somanyenchantments:unpredictable", "charm:clumsiness_curse", "charm:harming_curse", "charm:haunting_curse", "charm:rusting_curse"};

		@Config.Name("Metamagic - Projectile incompatible spells")
		@Config.RequiresMcRestart
		public String[] metamagic_projectile_incompatible_spells = {"ancientspellcraft:animate_item", "ancientspellcraft:animate_weapon", "ancientspellcraft:aquatic_agility", "ancientspellcraft:arcane_aegis", "ancientspellcraft:bubble_head", "ancientspellcraft:conjure_ink", "ancientspellcraft:eagle_eye", "ancientspellcraft:experiment", "ancientspellcraft:farsight", "ancientspellcraft:ice_tower", "ancientspellcraft:locating", "ancientspellcraft:magic_sparks", "ancientspellcraft:projectile_ward", "ancientspellcraft:resist_fire",};

		@Config.Name("Alter Potion Spell Mapping")
		@Config.Comment("Alter Potion Spell Mapping, entries should be separated by | ")
		@Config.RequiresMcRestart
		public String[] alter_potion_mapping = {"minecraft:speed|minecraft:slowness", "minecraft:regeneration|minecraft:poison", "minecraft:strength|minecraft:weakness", "minecraft:haste|minecraft:mining_fatigue", "minecraft:instant_health|minecraft:instant_damage", "minecraft:luck|minecraft:unluck", "minecraft:invisibility|minecraft:glowing", "minecraft:jump_boost|minecraft:levitation", "minecraft:night_vision|minecraft:blindness", "ebwizardry:empowerment|ancientspellcraft:magical_exhaustion",};

		@Config.Name("Sorcery Cloak Potion Effects")
		@Config.Comment("List of potion effects that can be granted by the Cloak of Spellweaving. Must be in 'modid:potion_registry_name' format. Default includes beneficial vanilla and mod potions.")
		@Config.RequiresMcRestart
		public String[] sorcery_cloak_potion_effects = {"minecraft:speed", "minecraft:jump_boost", "minecraft:strength", "minecraft:regeneration", "minecraft:absorption", "minecraft:night_vision", "minecraft:water_breathing", "minecraft:resistance", "ancientspellcraft:spell_range", "ancientspellcraft:spell_blast", "ancientspellcraft:spell_duration", "ancientspellcraft:spell_siphon"};

		@Config.Name("Duplication Scroll Additonal Items")
		@Config.Comment("List of registry names (in a 'modid:itemname' format) of additional items that can be duplicated by the Scroll of Duplication")
		@Config.RequiresMcRestart
		public String[] duplication_scroll_additional_items = {};

		@Config.Name("Wizards Buy Ancient Element Books")
		@Config.Comment("If true, friendly Wizards will buy ancient element books (the gray ones)")
		@Config.RequiresMcRestart
		public boolean wizards_buy_ancient_element_books = true;

		@Config.Name("Wizards Buy Ancient Spellcraft Spell Books")
		@Config.Comment("If true, friendly Wizards will buy ancient spellcraft element books (the blue/dark books)")
		@Config.RequiresMcRestart
		public boolean wizards_buy_ancient_spellcraft_books = true;

		@Config.Name("Wizards Buy Ancient Spellcraft Ritual Books")
		@Config.Comment("If true, friendly Wizards will buy ancient spellcraft ritual books")
		@Config.RequiresMcRestart
		public boolean wizards_buy_ancient_spellcraft_ritual_books = true;

		@Config.Name("Enable Wizard Entity Changes")
		@Config.Comment("If true, A.S. will alter the wizard entities to inject into their trade list. Disable if you are having issues related to this feature.")
		@Config.RequiresMcRestart
		public boolean apply_wizard_entity_changes = true;

		@Config.Name("Transportation Portal Teleports Any Entites")
		@Config.Comment("If true, Transportation Portals can transport non-player entities. If false, only players can use the portal.")
		public boolean transportation_portal_teleports_any_entites = true;

		@Config.Name("Sage Lectern Allowed Item List")
		@Config.Comment("List of item registry names that are allowed to be placed on the sage lectern. Each entry has a format of 'modid:item_name', example: antiqueatlas:antique_atlas. All spell books are allowed by default, without listing them." + "NOTE that this probably won't work with many items as they expect the player to hold the item in their hand to function, so only experiment with this in a testworld.")
		@Config.RequiresMcRestart
		public String[] sage_lectern_item_whitelist = {"ancientspellcraft:empty_mystic_spell_book", "antiqueatlas:antique_atlas", "minecraft:writable_book", "minecraft:written_book", "minecraft:book", "minecraft:enchanted_book"};

		@Config.Name("Sage Tome Required Enchanted Pages Per Tier")
		@Config.Comment("The required amount of Enchanted Pages to progress a Sage Tome to the next tier.")
		@Config.RequiresMcRestart
		@Config.RangeInt(min = 1, max = 100)
		public int sage_tome_enchanted_page_requirement = 2;

		@Config.Name("Sage Tome Required Enchanted Pages Per Tier Multiplier")
		@Config.Comment("Works in conjunction with \"Sage Tome Required Enchanted Pages Per Tier\"." + "This is basically a multiplier, to require more and more Enchanted Pages for each tier. If set to 0, all tiers will require the amount of Enchanted Pages defined in \"Sage Tome Required Enchanted Pages Per Tier\"." + "The formula: (\"Sage Tome Required Enchanted Pages Per Tier\") * (this multiplier) * (tierNumber (1->3, Apprentice->Master))")
		@Config.RequiresMcRestart
		@Config.RangeInt(min = 0, max = 10)
		public int sage_tome_enchanted_page_requirement_multiplier = 2;

		@Config.Name("Potency Gain from Sage Tome Empowerment Upgade Scrolls")
		@Config.Comment("")
		@Config.RequiresMcRestart
		@Config.RangeDouble(min = 0, max = 10)
		public double empowerment_upgrade_potency_gain = 0.05f;

		@Config.Name("Gem of Power Max Absorb Count")
		@Config.Comment("")
		@Config.RequiresMcRestart
		@Config.RangeDouble(min = 1, max = 100)
		public double gem_of_power_max_absorb_amount = 30;

		@Config.Name("Master Bolt impact deals block damage")
		@Config.Comment("")
		public boolean master_bolt_impact_deals_block_damage = false;

		@Config.Name("Wild Catalyst max distance ")
		@Config.Comment("[Server-only] The max destination distance in blocks where the Wild Catalyst artefact can redirect Transportation Portals.")
		@Config.RequiresMcRestart
		public int wild_catalyst_max_distance = 10000;

		@Config.Name("Vault Key Usage Mana Cost")
		@Config.RangeInt(min = 100, max = 10000)
		public int vault_key_usage_mana_cost = 5000;

		@Config.Name("Absorb Object Block Blacklist")
		public String[] absorb_object_block_blacklist = {};

		@Config.Name("Runic Shield Armor Amount")
		@Config.RequiresMcRestart
		public float runic_shield_armor = 5.0f;

		@Config.Name("Runic Shield Armor Toughness Amount")
		@Config.RequiresMcRestart
		public float runic_shield_armor_toughness = 5.0f;

		@Config.Name("Warlock Bonus Override")
		@Config.RequiresMcRestart
		public boolean warlock_bonus_override = true;

		@Config.Name("Warlock Bonus Potency Amount")
		@Config.RequiresMcRestart
		public float warlock_bonus_potency_amount = 0.15f;

		@Config.Name("Ring of Undeath max HP percent restore amount")
		@Config.RequiresMcRestart
		public float ring_of_undeath_heal_amount = 0.5f;

		@Config.Name("Cursed Pendant summon chance (1 == 100%)")
		@Config.RequiresMcRestart
		public float cursed_pendant_summon_chance = 0.5f;

		@Config.Name("Diamond Goose Detection Range")
		@Config.Comment("The maximum range in blocks at which the Diamond Goose artefact can detect bound blocks")
		@Config.RangeInt(min = 2, max = 32)
		public int diamondGooseDetectionRange = 8;

		@Config.Name("Verdant Crucible Ingredient List")
		@Config.Comment("List of alchemical ingredients and their min/max quantities for the Verdant Crucible. Format: modid:itemname|meta|nbt|min|max, e.g. minecraft:redstone|0||1|3. Meta is required (use 0 for default), nbt is optional (empty for none).")
		public String[] verdant_crucible_ingredients = {"minecraft:redstone|0||3|6", "minecraft:glowstone_dust|0||1|2", "minecraft:magma_cream|0||1|2", "minecraft:carrot|0||1|2", "minecraft:spider_eye|0||1|2", "minecraft:sugar|0||1|4", "minecraft:blaze_powder|0||1|2", "minecraft:ghast_tear|0||1|1", "minecraft:fish|3||1|1", "minecraft:rabbit_foot|0||1|1", "minecraft:speckled_melon|0||1|1", "rustic:aloe_vera:0:1:2", "rustic:blood_orchid:0:1:2", "rustic:chamomile:0:1:2", "rustic:cohosh:0:1:2", "rustic:deathstalk:0:1:2", "rustic:horsetail:0:1:2", "rustic:mooncap:0:1:2", "rustic:wind_thistle:0:1:2", "rustic:vanta_lily:0:1:2", "rustic:cloudsbluff:0:1:2", "rustic:core_root:0:1:2", "rustic:ginseng:0:1:2", "rustic:marsh_mallow:0:1:2"};
	}

	public static class ClientSettings {

		@Config.Name("Show Contingency HUD")
		@Config.Comment("Whether to show the contingency HUD when there are active spell Contingencies.")
		@Config.RequiresMcRestart
		public boolean show_contingency_hud = true;

		@Config.Name("Clips Mouse To Hud")
		@Config.Comment("Clips the mouse to the hud, possibly allowing faster spell selection.")
		@Config.RequiresMcRestart
		public boolean clip_mouse_to_circle = true;

		@Config.Name("Contingency HUD Left Side Position")
		@Config.Comment("Whether to show the contingency HUD on the left side of the screen (defaults to right side).")
		@Config.RequiresMcRestart
		public boolean contingency_hud_left_side_position = true;

		@Config.Name("Radial Spell Menu Enabled")
		@Config.Comment("If true, you can open the radial spell selector menu with the configured key. Otherwise you must click on a spell to select it.")
		public boolean radial_menu_enabled = true;

		@Config.Name("Release To Swap")
		@Config.Comment("If true, the hovered spell will be selected when you release the radial GUI button while having the cursor over a spell.")
		public boolean release_to_swap = true;

	}

	@Config.Name("Spell Compat Settings")
	@Config.LangKey("settings.ancientspellcraft:spell_compat_settings")
	public static SpellCompatSettings spellCompatSettings = new SpellCompatSettings();

	public static class SpellCompatSettings {

		@Config.Name("Mine Spell Override")
		@Config.Comment("If enabled, Ancient Spellcraft will override the base Wizardry mod's Mine spell to add compatibility to the Fortune related artefact." + "Disabling this feature will cause the game to load the default Mine spell class which can be helpful if you are having issues, but it also makes the Circlet of Fortune artefact useless!")
		@Config.RequiresMcRestart
		public boolean mineSpellOverride = true;

		@Config.Name("Mine Spell Network ID")
		@Config.Comment("WARNING! Don't change this value unless you are told you so, otherwise your world won't start! " + "\nThe reason this value exists as a settings is to provide a quick way to fix compatibility (until the A.S. update is released to fix it) if the network ID of the spell is changed by an EBWiz update. " + "\nThis could possibly happen if new spells are added by the base mod and the NetworkIDs shift.")
		@Config.RequiresMcRestart
		public int mineSpellNetworkID = 141;

		@Config.Name("Charge Spell Network ID")
		@Config.Comment("WARNING! Don't change this value unless you are told you so, otherwise your world won't start! " + "\nThe reason this value exists as a settings is to provide a quick way to fix compatibility (until the A.S. update is released to fix it) if the network ID of the spell is changed by an EBWiz update. " + "\nThis could possibly happen if new spells are added by the base mod and the NetworkIDs shift.")
		@Config.RequiresMcRestart
		public int chargeSpellNetworkID = 147;

		@Config.Name("Charge Spell Override")
		@Config.Comment("If enabled, Ancient Spellcraft will override the base Wizardry mod's Charge spell to add compatibility for the related artefact." + "\nDisabling this feature will cause the game to load the default Charge spell class which can be helpful if you are having issues, but it also makes the related artefact useless!")
		@Config.RequiresMcRestart
		public boolean chargeSpellOverride = true;

		@Config.Name("Clairvoyance Spell Override")
		@Config.Comment("If enabled, Ancient Spellcraft will override the base Wizardry mod's Charge spell to add compatibility for the related artefact." + "\nDisabling this feature will cause the game to load the default Clairvoyance spell class which can be helpful if you are having issues, but it also makes the related artefact useless!")
		@Config.RequiresMcRestart
		public boolean clairvoyanceSpellOverride = true;

		@Config.Name("Conjure Pickaxe Spell Override")
		@Config.Comment("If enabled, Ancient Spellcraft will override the base Wizardry mod's Conjure Pickaxe spell to add compatibility to the Fortune related artefact." + "\nDisabling this feature will cause the game to load the default Conjure Pickaxe spell class which can be helpful if you are having issues, but it also makes the Circlet of Fortune artefact useless!")
		@Config.RequiresMcRestart
		public boolean conjurePickaxeSpellOverride = true;

		@Config.Name("Conjure Pickaxe Spell Network ID")
		@Config.Comment("WARNING! Don't change this value unless you are told you so, otherwise your world won't start! " + "\nThe reason this value exists as a settings is to provide a quick way to fix compatibility (until the A.S. update is released to fix it) if the network ID of the spell is changed by an EBWiz update." + "\nThis could possibly happen if new spells are added by the base mod and the NetworkIDs shift.")
		@Config.RequiresMcRestart
		public int conjurePickaxeSpellNetworkID = 41;

		@Config.Name("Plague of Darkenss Spell Network ID")
		@Config.Comment("WARNING! Don't change this value unless you are told you so, otherwise your world won't start! " + "\nThe reason this value exists as a settings is to provide a quick way to fix compatibility (until the A.S. update is released to fix it) if the network ID of the spell is changed by an EBWiz update." + "\nThis could possibly happen if new spells are added by the base mod and the NetworkIDs shift.")
		@Config.RequiresMcRestart
		public int plagueOfDarknessSpellNetworkID = 99;

	}

	@Config.Name("Worldgen Settings")
	@Config.LangKey("settings.ancientspellcraft:worldgen_settings")
	public static WorldgenSettings worldgenSettings = new WorldgenSettings();

	@Config.Name("Entity Spawn Settings")
	@Config.LangKey("settings.ancientspellcraft:entity_spawn_settings")
	public static EntitySpawnSettings entitySpawnSettings = new EntitySpawnSettings();

	public static class EntitySpawnSettings {

		@Config.Name("Enable Wizard Merchant Spawns")
		@Config.Comment("Set to true to enable natural spawning of the Wizard Merchant NPC.")
		public boolean wizardMerchantSpawnEnabled = true;

		@Config.Name("Wizard Merchant Spawn Weight")
		@Config.Comment("Controls the rarity of the Wizard Merchant NPC. Higher values mean more frequent spawns. (Default: 1, very rare)")
		@Config.RangeInt(min = 0, max = 100)
		public int wizardMerchantSpawnWeight = 1;

		@Config.Name("Wizard Merchant Min Group Size")
		@Config.Comment("Minimum number of Wizard Merchants that can spawn in a group.")
		@Config.RangeInt(min = 1, max = 5)
		public int wizardMerchantMinGroupSize = 1;

		@Config.Name("Wizard Merchant Max Group Size")
		@Config.Comment("Maximum number of Wizard Merchants that can spawn in a group.")
		@Config.RangeInt(min = 1, max = 5)
		public int wizardMerchantMaxGroupSize = 1;

		@Config.Name("Wizard Merchant Max Per Dimension")
		@Config.Comment("Maximum number of Wizard Merchants that can exist in a single dimension at once. Set to 0 for no limit.")
		@Config.RangeInt(min = 0, max = 15)
		public int wizardMerchantMaxPerDimension = 1;
	}

	public static class WorldgenSettings {

		@Config.Name("Battlemage Camp Dimensions")
		@Config.Comment("[Server-only] List of dimension ids in which to spawn battlemage camps.")
		@Config.RequiresMcRestart
		public int[] battlemageCampDimensions = {0};

		@Config.Name("Battlemage Keep Dimensions")
		@Config.Comment("[Server-only] List of dimension ids in which to spawn battlemage keeps.")
		@Config.RequiresMcRestart
		public int[] battlemageKeepDimensions = {0};

		@Config.Name("Ancient Vault Dimensions")
		@Config.Comment("[Server-only] List of dimension ids in which to spawn Ancient Vault.")
		@Config.RequiresMcRestart
		public int[] ancientVaultDimensions = {0};

		@Config.Name("Warlock Structure Dimensions")
		@Config.Comment("[Server-only] List of dimension ids in which to spawn Warlock Structure.")
		@Config.RequiresMcRestart
		public int[] warlockStructureDimensions = {0};

		@Config.Name("Sage hill Dimensions")
		@Config.Comment("[Server-only] List of dimension ids in which to spawn the sage hill structures.")
		@Config.RequiresMcRestart
		public int[] sageHillDimensions = {0};

		@Config.Name("Warlock Camp Rarity")
		@Config.Comment("[Server-only] The rarity of warlock camps, used by the world generator. Larger numbers are rarer.")
		@Config.RequiresMcRestart
		public int warlockCampRarity = 3200;

		@Config.Name("Book Vault Rarity")
		@Config.Comment("[Server-only] The rarity of battlemage camps, used by the world generator. Larger numbers are rarer.")
		@Config.RequiresMcRestart
		public int bookVaultRarity = 3200;

		@Config.Name("Battlemage Keep Rarity")
		@Config.Comment("[Server-only] The rarity of battlemage keeps, used by the world generator. Larger numbers are rarer.")
		@Config.RequiresMcRestart
		public int battlemageKeepRarity = 3000;

		@Config.Name("Sage hill Rarity")
		@Config.Comment("[Server-only] The rarity of the sage hill structure, used by the world generator. Larger numbers are rarer.")
		@Config.RequiresMcRestart
		public int sageHillRarity = 3000;

		@Config.Name("Ancient Vault Structure")
		@Config.Comment("[Server-only] The rarity of the ancient vault and others (e.g. ancient temple) structures, used by the world generator. Larger numbers are rarer.")
		@Config.RequiresMcRestart
		public int ancientVaultRarity = 4000;

		@Config.Name("Fallen Tower Rarity")
		@Config.Comment("[Server-only] The rarity of the ancient vault and others (e.g. ancient temple) structures, used by the world generator. Larger numbers are rarer.")
		@Config.RequiresMcRestart
		public int fallenTowerRarity = 3800;

		@Config.Name("Warlock Structure Rarity")
		@Config.Comment("[Server-only] The rarity of the warlock structures. Larger numbers are rarer.")
		@Config.RequiresMcRestart
		public int warlockStructureRarity = 3600;

		@Config.Name("Elemental Ore Worldgen Size")
		@Config.RequiresMcRestart
		public int elementalOreWorldgenSize = 4;

		@Config.Name("Elemental Ore Frequency Min")
		@Config.RequiresMcRestart
		public int elementalOreFrequencyMin = 2;

		@Config.Name("Elemental Ore Frequency Max")
		@Config.RequiresMcRestart
		public int elementalOreFrequencyMax = 8;

		@Config.Name("Elemental Ore Rarity")
		@Config.RequiresMcRestart
		public int elementalOreRarity = 1;

		@Config.Name("Elemental Ore Y Layer Min")
		@Config.RequiresMcRestart
		public int elementalOreYLayerMin = 5;

		@Config.Name("Elemental Ore Y Layer Max")
		@Config.RequiresMcRestart
		public int elementalOreYLayerMax = 30;

		@Config.Name("Devoritium Ore Max Vein Size")
		@Config.RequiresMcRestart
		public int devoritiumOreMaxVeinSize = 3;

		@Config.Name("Devoritium Ore Y Layer Min")
		@Config.RequiresMcRestart
		public int devoritiumOreYLayerMin = 5;

		@Config.Name("Devoritium Ore Y Layer Max")
		@Config.RequiresMcRestart
		public int devoritiumOreYLayerMax = 30;

		@Config.Name("Devoritium Ore Chances to Spawn")
		@Config.RequiresMcRestart
		public int devoritiumOreChancesToSpawn = 4;

		@Config.Name("Generate Crystal Silver Ore")
		@Config.RequiresMcRestart
		public boolean generateCrystalSilverOre = true;

		@Config.Name("Crystal Silver Ore Ore Dimensions")
		@Config.Comment("[Server-only] List of dimension ids")
		@Config.RequiresMcRestart
		public int[] crystalSilverOreDimensions = {0};

		@Config.Name("Crystal Silver Ore Max Vein Size")
		@Config.RequiresMcRestart
		public int crystalSilverOreMaxVeinSize = 3;

		@Config.Name("Crystal Silver Ore Y Layer Min")
		@Config.RequiresMcRestart
		public int crystalSilverOreYLayerMin = 5;

		@Config.Name("Crystal Silver Ore Y Layer Max")
		@Config.RequiresMcRestart
		public int crystalSilverOreYLayerMax = 20;

		@Config.Name("Crystal Silver Ore Chances to Spawn")
		@Config.RequiresMcRestart
		public int crystalSilverOreChancesToSpawn = 4;

		@Config.Name("Generate Astral Diamond Ore")
		@Config.RequiresMcRestart
		public boolean generateAstralDiamondOre = true;

		@Config.Name("Astral Diamond Ore Dimensions")
		@Config.Comment("[Server-only] List of dimension ids")
		@Config.RequiresMcRestart
		public int[] astralDiamondOreDimensions = {0};

		@Config.Name("Astral Diamond Ore Max Vein Size")
		@Config.RequiresMcRestart
		public int astralDiamondOreMaxVeinSize = 3;

		@Config.Name("Astral Diamond Ore Y Layer Min")
		@Config.RequiresMcRestart
		public int astralDiamondOreYLayerMin = 5;

		@Config.Name("Astral Diamond Ore Y Layer Max")
		@Config.RequiresMcRestart
		public int astralDiamondOreYLayerMax = 15;

		@Config.Name("Astral Diamond Ore Chances to Spawn")
		@Config.RequiresMcRestart
		public int astralDiamondOreChancesToSpawn = 3;

	}

}