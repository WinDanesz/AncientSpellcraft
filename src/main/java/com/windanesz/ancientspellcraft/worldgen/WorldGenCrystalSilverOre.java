package com.windanesz.ancientspellcraft.worldgen;

import com.google.common.primitives.Ints;
import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

/**
 * Based on {@link electroblob.wizardry.worldgen.WorldGenCrystalOre) - author: Electroblob
 */
public class WorldGenCrystalSilverOre extends WorldGenOreBase implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {

		if (Settings.worldgenSettings.generateCrystalSilverOre && Ints.contains(Settings.worldgenSettings.crystalSilverOreDimensions, world.provider.getDimension())) {
			this.addOreSpawn(ASBlocks.CRYSTAL_SILVER_ORE.getDefaultState(), world, random, chunkX * 16, chunkZ * 16, 16, 16,
					Settings.worldgenSettings.crystalSilverOreMaxVeinSize,
					Settings.worldgenSettings.crystalSilverOreChancesToSpawn,
					Settings.worldgenSettings.crystalSilverOreYLayerMin,
					Settings.worldgenSettings.crystalSilverOreYLayerMax);
		}
	}
}