package com.windanesz.ancientspellcraft;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;

import static electroblob.wizardry.Settings.toResourceLocations;

@Config(modid = AncientSpellcraft.MODID) // No fancy configs here so we can use the annotation, hurrah!
public class Settings {

	public ResourceLocation[] lootInjectionLocations = toResourceLocations(DEFAULT_LOOT_INJECTION_LOCATIONS);

	public ResourceLocation[] artefactInjectionLocations = toResourceLocations(EPIC_ARTEFACT_INJECTION_LOCATIONS);

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