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

@Config(modid = AncientSpellcraft.MODID, name = "AncientSpellcraft") // No fancy configs here so we can use the annotation, hurrah!
public class Settings {

	public ResourceLocation[] lootInjectionLocations = toResourceLocations(generalSettings.DEFAULT_LOOT_INJECTION_LOCATIONS);

	public ResourceLocation[] artefactInjectionLocations = toResourceLocations(generalSettings.ARTEFACT_INJECTION_LOCATIONS);

	public ResourceLocation[] voidCreeperBiomeBlacklist = toResourceLocations(generalSettings.void_creeper_biome_blacklist);

	public List<ResourceLocation> shardEarthShardBiomeWhitelist = Arrays.asList(toResourceLocations(generalSettings.earth_shard_biome_whitelist));
	public List<ResourceLocation> shardSorceryShardBiomeWhitelist = Arrays.asList(toResourceLocations(generalSettings.sorcery_shard_biome_whitelist));
	public List<ResourceLocation> shardNecromancyShardBiomeWhitelist = Arrays.asList(toResourceLocations(generalSettings.necromancy_shard_biome_whitelist));
	public List<ResourceLocation> shardHealingBiomeWhitelist = Arrays.asList(toResourceLocations(generalSettings.healing_shard_biome_whitelist));
	public List<ResourceLocation> shardLightningBiomeWhitelist = Arrays.asList(toResourceLocations(generalSettings.lightning_shard_biome_whitelist));
	public List<ResourceLocation> shardFireBiomeWhitelist = Arrays.asList(toResourceLocations(generalSettings.fire_shard_biome_whitelist));
	public List<ResourceLocation> shardIceBiomeWhitelist = Arrays.asList(toResourceLocations(generalSettings.ice_shard_biome_whitelist));

	public ResourceLocation[] battlemageCampWithChestFiles = {new ResourceLocation(AncientSpellcraft.MODID, "battlemage_camp_chest_0")};
	public ResourceLocation[] battlemageKeepWithChestFiles = {new ResourceLocation(AncientSpellcraft.MODID, "battlemage_keep_chest_0")};

	/**
	 * Helper method to figure out if an item was disabled in the ebwiz configs, as unfortunately temArtefact#enabled private and has no getter method
	 * @param artefact to check
	 * @return true if the item is enabled (or if it has no config)
	 */
	public static boolean isArtefactEnabled(Item artefact) {
		if (artefact instanceof ItemArtefact &&
				(Wizardry.settings.getConfigCategory(ARTEFACTS_CATEGORY).containsKey(artefact.getRegistryName().toString()))) {
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
		public String[] DEFAULT_LOOT_INJECTION_LOCATIONS = {
				"ebwizardry:chests/wizard_tower",
				"ebwizardry:chests/shrine",
				"ebwizardry:chests/obelisk",

				"minecraft:chests/desert_pyramid",
				"minecraft:chests/jungle_temple",
				"minecraft:chests/stronghold_corridor",
				"minecraft:chests/stronghold_crossing",
				"minecraft:chests/stronghold_library",
				"minecraft:chests/igloo_chest",
				"minecraft:chests/woodland_mansion",
				"minecraft:chests/end_city_treasure"};

		@Config.Name("Artefact Inject locations")
		@Config.Comment("List of loot tables to inject Ancient Spellcraft artefacts into.")
		private String[] ARTEFACT_INJECTION_LOCATIONS = {
				"ebwizardry:subsets/uncommon_artefacts",
				"ebwizardry:subsets/rare_artefacts",
				"ebwizardry:subsets/epic_artefacts"
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

		@Config.Name("Spellblade base hit mana cost per tier")
		@Config.Comment("The amount of mana required & consumed when the spellblade is used to hit a target, based on the tier of the blade\n"
				+ "Example: if the cost is 5, a novice blade will consume 5 mana, an apprentice sword will consume 2x5 mana, a master blade will consume 20 mana")
		@Config.RequiresMcRestart
		@Config.RangeInt(min = 1, max = 100)
		public int spellblade_base_mana_cost = 5;

		@Config.Name("Orb Artefact Potency Percent Bonus")
		@Config.Comment("Determines the potency bonus of the elemental orb artefacts in a percentage value")
		@Config.RequiresMcRestart
		@Config.RangeInt(min = 0, max = 100)
		public int orb_artefact_potency_bonus = 30;

		@Config.Name("Void Creeper Biome Blacklist")
		@Config.Comment("List of Biomes where Void Creepers will never spawn.")
		@Config.RequiresMcRestart
		public String[] void_creeper_biome_blacklist = {"mushroom_island", "mushroom_island_shore"};

		@Config.Name("Void Creeper Dimension Whitelist")
		@Config.Comment("List of Dimensions where Void Creepers are allowed to spawn. Defaults to Overworld only."
				+ "\n make")
		@Config.RequiresMcRestart
		public Integer[] void_creeper_dimension_whitelist = {0};

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
		public String[] lightning_shard_biome_whitelist = {"extreme_hills",
				"smaller_extreme_hills",
				"extreme_hills_with_trees",
				"mutated_extreme_hills",
				"mutated_extreme_hills_with_trees"};

		@Config.Name("Elemental Ice Crystal Shard Biome List")
		@Config.Comment("List of Biomes where Ice Crystal Shards can spawn.")
		@Config.RequiresMcRestart
		public String[] ice_shard_biome_whitelist = {"taiga", "taiga_hills", "taiga_cold", "taiga_cold_hills", "mutated_taiga", "mutated_taiga_cold"};

		@Config.Name("[UNUSED] Pocket Biome registry ID")
		@Config.Comment("Allows you to change the pocket biome registry ID if you encounter biome ID conflicts")
		@Config.RequiresMcRestart
		public int pocket_biome_registry_id = 168;

		@Config.Name("Immobility Contingency Spell Trigger Effects")
		@Config.Comment("List of potion effects which can be considered as an immobilizing effect. Receiving one of these will trigger the stored Contingency - Immobility spell")
		@Config.RequiresMcRestart
		public String[] immobility_contingency_effects = {
				"ebwizardry:paralysis",
				"ebwizardry:containment",
				"ebwizardry:slow_time",
				"ebwizardry:frost",
				"minecraft:slowness"
		};

		@Config.Name("Duplication Scroll Additonal Items")
		@Config.Comment("List of registry names (in a 'modid:itemname' format) of additional items that can be duplicated by the Scroll of Duplication")
		@Config.RequiresMcRestart
		public String[] duplication_scroll_additional_items = { };

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
		@Config.Comment("List of item registry names that are allowed to be placed on the sage lectern. Each entry has a format of 'modid:item_name', example: antiqueatlas:antique_atlas. All spell books are allowed by default, without listing them."
				+ "NOTE that this probably won't work with many items as they expect the player to hold the item in their hand to function, so only experiment with this in a testworld.")
		@Config.RequiresMcRestart
		public String[] sage_lectern_item_whitelist = {"antiqueatlas:antique_atlas"};

		@Config.Name("Sage Tome Required Enchanted Pages Per Tier")
		@Config.Comment("The required amount of Enchanted Pages to progress a Sage Tome to the next tier.")
		@Config.RequiresMcRestart
		@Config.RangeInt(min = 1, max = 100)
		public int sage_tome_enchanted_page_requirement = 2;

		@Config.Name("Sage Tome Required Enchanted Pages Per Tier Multiplier")
		@Config.Comment("Works in conjunction with \"Sage Tome Required Enchanted Pages Per Tier\"."
				+ "This is basically a multiplier, to require more and more Enchanted Pages for each tier. If set to 0, all tiers will require the amount of Enchanted Pages defined in \"Sage Tome Required Enchanted Pages Per Tier\"."
				+ "The formula: (\"Sage Tome Required Enchanted Pages Per Tier\") * (this multiplier) * (tierNumber (1->3, Apprentice->Master))")
		@Config.RequiresMcRestart
		@Config.RangeInt(min = 0, max = 10)
		public int sage_tome_enchanted_page_requirement_multiplier = 2;
		
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
		@Config.Comment("If enabled, Ancient Spellcraft will override the base Wizardry mod's Mine spell to add compatibility to the Fortune related artefact."
				+ "Disabling this feature will cause the game to load the default Mine spell class which can be helpful if you are having issues, but it also makes the Circlet of Fortune artefact useless!")
		@Config.RequiresMcRestart
		public boolean mineSpellOverride = true;

		@Config.Name("Mine Spell Network ID")
		@Config.Comment("WARNING! Don't change this value unless you are told you so, otherwise your world won't start! "
				+ "\nThe reason this value exists as a settings is to provide a quick way to fix compatibility (until the A.S. update is released to fix it) if the network ID of the spell is changed by an EBWiz update. "
				+ "\nThis could possibly happen if new spells are added by the base mod and the NetworkIDs shift.")
		@Config.RequiresMcRestart
		public int mineSpellNetworkID = 141;

		@Config.Name("Charge Spell Network ID")
		@Config.Comment("WARNING! Don't change this value unless you are told you so, otherwise your world won't start! "
				+ "\nThe reason this value exists as a settings is to provide a quick way to fix compatibility (until the A.S. update is released to fix it) if the network ID of the spell is changed by an EBWiz update. "
				+ "\nThis could possibly happen if new spells are added by the base mod and the NetworkIDs shift.")
		@Config.RequiresMcRestart
		public int chargeSpellNetworkID = 147;

		@Config.Name("Charge Spell Override")
		@Config.Comment("If enabled, Ancient Spellcraft will override the base Wizardry mod's Charge spell to add compatibility for the related artefact."
				+ "\nDisabling this feature will cause the game to load the default Charge spell class which can be helpful if you are having issues, but it also makes the related artefact useless!")
		@Config.RequiresMcRestart
		public boolean chargeSpellOverride = true;

		@Config.Name("Conjure Pickaxe Spell Override")
		@Config.Comment("If enabled, Ancient Spellcraft will override the base Wizardry mod's Conjure Pickaxe spell to add compatibility to the Fortune related artefact."
				+ "\nDisabling this feature will cause the game to load the default Conjure Pickaxe spell class which can be helpful if you are having issues, but it also makes the Circlet of Fortune artefact useless!")
		@Config.RequiresMcRestart
		public boolean conjurePickaxeSpellOverride = true;

		@Config.Name("Conjure Pickaxe Spell Network ID")
		@Config.Comment("WARNING! Don't change this value unless you are told you so, otherwise your world won't start! "
				+ "\nThe reason this value exists as a settings is to provide a quick way to fix compatibility (until the A.S. update is released to fix it) if the network ID of the spell is changed by an EBWiz update."
				+ "\nThis could possibly happen if new spells are added by the base mod and the NetworkIDs shift.")
		@Config.RequiresMcRestart
		public int conjurePickaxeSpellNetworkID = 41;

	}

	@Config.Name("Worldgen Settings")
	@Config.LangKey("settings.ancientspellcraft:worldgen_settings")
	public static WorldgenSettings worldgenSettings = new WorldgenSettings();

	public static class WorldgenSettings {

		@Config.Name("Battlemage Camp Dimensions")
		@Config.Comment("[Server-only] List of dimension ids in which to spawn battlemage camps.")
		@Config.RequiresMcRestart
		public int[] battlemageCampDimensions = {0};

		@Config.Name("Battlemage Camp Rarity")
		@Config.Comment("[Server-only] The rarity of battlemage camps, used by the world generator. Larger numbers are rarer.")
		@Config.RequiresMcRestart
		public int battlemageCampRarity = 1800;

		@Config.Name("Battlemage Keep Dimensions")
		@Config.Comment("[Server-only] List of dimension ids in which to spawn battlemage keeps.")
		@Config.RequiresMcRestart
		public int[] battlemageKeepDimensions = {0};

		@Config.Name("Battlemage Keep Rarity")
		@Config.Comment("[Server-only] The rarity of battlemage keeps, used by the world generator. Larger numbers are rarer.")
		@Config.RequiresMcRestart
		public int battlemageKeepRarity = 2000;

	}

}