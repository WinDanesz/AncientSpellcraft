package com.windanesz.ancientspellcraft;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.List;

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

//	@Config.Ignore
//	public static final String ClientAndServerSideNote = "Affect both client and server. These configs should match for client and server.";


	@Config.Name("General Settings")
	@Config.LangKey("settings.ancientspellcraft:general_settings")
	public static GeneralSettings generalSettings = new GeneralSettings();

	public static class GeneralSettings {

		@Config.Name("JEI integration")
		@Config.Comment("Enables or disables the JEI integration of the mod")
		@Config.RequiresMcRestart
		public boolean jei_integration = true;

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

		@Config.Name("Orb Artefact Potency Percent Bonus")
		@Config.Comment("Determines the potency bonus of the elemental orb artefacts in a percentage value")
		@Config.RequiresMcRestart
		@Config.RangeInt(min = 0, max = 100)
		public int orb_artefact_potency_bonus = 30;

		@Config.Name("Void Creeper Biome Blacklist")
		@Config.Comment("List of Biomes where Void Creepers will never spawn.")
		@Config.RequiresMcRestart
		public String[] void_creeper_biome_blacklist = {"mushroom_island", "mushroom_island_shore"};

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

		@Config.Name("Elemental Ice Crystal Shard Biome List")
		@Config.Comment("List of Biomes where Ice Crystal Shards can spawn.")
		@Config.RequiresMcRestart
		public String[] ice_shard_biome_whitelist = {"taiga", "taiga_hills", "taiga_cold", "taiga_cold_hills", "mutated_taiga", "mutated_taiga_cold"};

		@Config.Name("Pocket Biome registry ID")
		@Config.Comment("Allows you to change the pocket biome registry ID if you encounter biome ID conflicts")
		@Config.RequiresMcRestart
		public int pocket_biome_registry_id = 168;

	}



}