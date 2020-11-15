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

	public ResourceLocation[] lootInjectionLocations = toResourceLocations(generalSettings.DEFAULT_LOOT_INJECTION_LOCATIONS);

	public ResourceLocation[] artefactInjectionLocations = toResourceLocations(generalSettings.EPIC_ARTEFACT_INJECTION_LOCATIONS);

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

		@Config.Name("Generate Crystal Ore Shards")
		@Config.Comment("Determines whether to generate elemental crystal shards in the Overworld or not")
		@Config.RequiresMcRestart
		public boolean generate_ore_shards = true;

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

		@Config.Name("Artefact Inject locations Crystal Ore Shards")
		@Config.Comment("List of loot tables to inject Ancient Spellcraft artefacts into.")
		private String[] EPIC_ARTEFACT_INJECTION_LOCATIONS = {
				"ebwizardry:subsets/uncommon_artefacts",
				"ebwizardry:subsets/rare_artefacts",
				"ebwizardry:subsets/epic_artefacts"
		};

		@Config.Name("Essence Extraction Screen Shake")
		@Config.Comment("Determines whether to the Essence Extraction spell shakes the screen while extracting powerful blocks or not.")
		@Config.RequiresMcRestart
		public boolean shake_screen = true;

	}



}