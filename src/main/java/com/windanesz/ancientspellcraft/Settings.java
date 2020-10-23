package com.windanesz.ancientspellcraft;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static electroblob.wizardry.Settings.toResourceLocations;

@Config(modid = AncientSpellcraft.MODID, name = "AncientSpellcraft") // No fancy configs here so we can use the annotation, hurrah!
public class Settings {

	public ResourceLocation[] lootInjectionLocations = toResourceLocations(DEFAULT_LOOT_INJECTION_LOCATIONS);

	public ResourceLocation[] artefactInjectionLocations = toResourceLocations(EPIC_ARTEFACT_INJECTION_LOCATIONS);

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

	@Config.Ignore
	public static final String ClientAndServerSideNote = "Affect both client and server. These configs should match for client and server.";


	@Config.Name("General Settings")
	@Config.LangKey("settings.ancientspellcraft:general_settings")
	@Config.Comment(ClientAndServerSideNote)
	public static GeneralSettings generalSettings = new GeneralSettings();

	public static class GeneralSettings {

		@Config.Name("Generate Crystal Ore Shards")
		@Config.Comment("Determines whether to generate elemental crystal shards in the Overworld or not")
		@Config.RequiresMcRestart
		public boolean generate_ore_shards = true;

//		@Config.Name("Crystal Ore Shard Rarity")
//		@Config.Comment("The chance for ore shards to spawn in a chunk. 0 = never spawn, 1.0 = always")
//		@Config.RequiresMcRestart
//		@Config.RangeDouble(min = 0D, max = 1D)
//		@Config.SlidingOption()
//		public double crystal_ore_rarity = 0.8D;
	}


	@Config.Comment("List of loot tables to inject Ancient Spellcraft loot (as specified in loot_tables/chests/dungeon_additions.json) into.")
	@Config.LangKey("config.ancientspellcraft.loot_injection_locations")

	private static final String[] DEFAULT_LOOT_INJECTION_LOCATIONS = {
			// wizardry loot tables
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

	private static final String[] EPIC_ARTEFACT_INJECTION_LOCATIONS = {
			// wizardry artefacts
			"ebwizardry:chests/shrine"
	};
}