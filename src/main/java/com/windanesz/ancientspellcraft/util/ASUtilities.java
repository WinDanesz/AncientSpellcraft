package com.windanesz.ancientspellcraft.util;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * A class for common utilities used in the mod
 *
 * @autor Dan (WinDanesz)
 **/
public final class ASUtilities {

	/**
	 * Get a Map of all biomes with their Ids
	 *
	 * @return A Map with all registered biome names with their corresponding biome Ids.
	 **/
	public static Map<String, Integer> getAllBiomes() {
		Map<String, Integer> biomeMap = ForgeRegistries.BIOMES.getValuesCollection().stream().collect(Collectors.toMap(Biome::getBiomeName, Biome::getIdForBiome));
		return biomeMap;
	}

	/**
	 * Get a Map of all biomes with their registry names
	 *
	 * @return A Map with all registered biome names with their corresponding biome registry names.
	 **/
	public static Map<String, ResourceLocation> getAllBiomesWithRegnames() {
		Map<String, ResourceLocation> biomeMap = ForgeRegistries.BIOMES.getValuesCollection().stream().collect(Collectors.toMap(Biome::getBiomeName, Biome::getRegistryName));
		return biomeMap;
	}

	/**
	 * Check if a biome name is registered
	 *
	 * @return A Map with all registered biome names with their corresponding biome Ids.
	 **/
	public static boolean isBiomeNameRegistered(String biomeName) {
		Map<String, Integer> biomes = getAllBiomes();
		return biomes.containsKey(biomeName);
	}

	public static int getBiomeIdFromName(String biomeName) {
		Map<String, Integer> biomes = getAllBiomes();
		return biomes.get(biomeName);
	}

	public static ResourceLocation getBiomeRegistryNameFromName(String biomeName) {
		Map<String, ResourceLocation> biomes = getAllBiomesWithRegnames();
		return biomes.get(biomeName);
	}

}
