/*******************************************************************************
 * Copyright 2015-2016, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 *
 * Original: https://github.com/Glitchfiend/BiomesOPlenty/blob/BOP-1.12.x-7.0.x/src/main/java/biomesoplenty/common/util/biome/BiomeUtils.java
 * (Adapted for formatting consistency)
 ******************************************************************************/
package com.windanesz.ancientspellcraft.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.ChunkGeneratorSettings;

public class BiomeLocator {

	public static BlockPos spiralOutwardsLookingForBiome(World world, Biome biomeToFind, double startX, double startZ) {
		int sampleSpacing = 4 << getBiomeSize(world);
		int maxDist = sampleSpacing * 100;
		return spiralOutwardsLookingForBiome(world, biomeToFind, startX, startZ, maxDist, sampleSpacing);
	}

	private static BlockPos spiralOutwardsLookingForBiome(World world, Biome biomeToFind, double startX, double startZ, int maxDist, int sampleSpace) {
		if(maxDist <= 0 || sampleSpace <= 0)
			throw new IllegalArgumentException("maxDist and sampleSpace must be positive");

		if (world.isRemote)
			return null;

		BiomeProvider chunkManager = world.getBiomeProvider();

		double a = sampleSpace / Math.sqrt(Math.PI);
		double b = 2 * Math.sqrt(Math.PI);
		double dist = 0;
		for (int n = 0; dist < maxDist; ++n) {
			double rootN = Math.sqrt(n);
			dist = a * rootN;
			double x = startX + (dist * Math.sin(b * rootN));
			double z = startZ + (dist * Math.cos(b * rootN));

			// chunkManager.genBiomes is the first layer returned from initializeAllBiomeGenerators()
			// chunkManager.biomeIndexLayer is the second layer returned from initializeAllBiomeGenerators(), it's zoomed twice from genBiomes (>> 2) this one is actual size
			// chunkManager.getBiomeGenAt uses biomeIndexLayer to get the biome
			Biome[] biomesAtSample = chunkManager.getBiomes(null, (int) x, (int) z, 1, 1, false);
			if (biomesAtSample[0] == biomeToFind)
				return new BlockPos((int) x, 0, (int) z);
		}

		return null;
	}

	private static int getBiomeSize(World world) {
		String generatorSettingsJson = world.getWorldInfo().getGeneratorOptions();
		return ChunkGeneratorSettings.Factory.jsonToFactory(generatorSettingsJson).build().biomeSize;
	}

}