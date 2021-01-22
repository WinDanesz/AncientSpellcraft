package com.windanesz.ancientspellcraft.worldgen;

import com.google.common.base.Predicate;
import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

/**
 * Based on {@link electroblob.wizardry.worldgen.WorldGenCrystalOre) - author: Electroblob
 */
public class WorldGenCrystalShardOre implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {

		if (world.provider.getDimension() == 0 && Settings.generalSettings.generate_ore_shards) {
			Biome biome = world.getBiome(new BlockPos(chunkX * 16, 0, chunkZ * 16));

			if (AncientSpellcraft.settings.shardFireBiomeWhitelist.contains(biome.getRegistryName())) {
				generateOre(random, chunkX, chunkZ, world, AncientSpellcraftBlocks.CRYSTAL_ORE_FIRE, BlockMatcher.forBlock(Blocks.STONE), 4, 2, 8, 1, 5, 30);
			}
			if (AncientSpellcraft.settings.shardNecromancyShardBiomeWhitelist.contains(biome.getRegistryName())) {
				generateOre(random, chunkX, chunkZ, world, AncientSpellcraftBlocks.CRYSTAL_ORE_NECROMANCY, BlockMatcher.forBlock(Blocks.STONE), 4, 2, 8, 1, 5, 30);
			}
			if (AncientSpellcraft.settings.shardHealingBiomeWhitelist.contains(biome.getRegistryName())) {
				generateOre(random, chunkX, chunkZ, world, AncientSpellcraftBlocks.CRYSTAL_ORE_HEALING, BlockMatcher.forBlock(Blocks.STONE), 4, 2, 8, 1, 5, 30);
			}
			if (AncientSpellcraft.settings.shardEarthShardBiomeWhitelist.contains(biome.getRegistryName())) {
				generateOre(random, chunkX, chunkZ, world, AncientSpellcraftBlocks.CRYSTAL_ORE_EARTH, BlockMatcher.forBlock(Blocks.STONE), 4, 2, 8, 1, 5, 30);
			}
			if (AncientSpellcraft.settings.shardLightningBiomeWhitelist.contains(biome.getRegistryName())) {
				generateOre(random, chunkX, chunkZ, world, AncientSpellcraftBlocks.CRYSTAL_ORE_LIGHTNING, BlockMatcher.forBlock(Blocks.STONE), 4, 2, 8, 1, 5, 30);
			}
			if (AncientSpellcraft.settings.shardIceBiomeWhitelist.contains(biome.getRegistryName())) {
				generateOre(random, chunkX, chunkZ, world, AncientSpellcraftBlocks.CRYSTAL_ORE_ICE, BlockMatcher.forBlock(Blocks.STONE), 4, 2, 8, 1, 5, 30);
			}
			if (AncientSpellcraft.settings.shardSorceryShardBiomeWhitelist.contains(biome.getRegistryName())) {
				generateOre(random, chunkX, chunkZ, world, AncientSpellcraftBlocks.CRYSTAL_ORE_SORCERY, BlockMatcher.forBlock(Blocks.STONE), 4, 2, 8, 1, 5, 30);
			}
		}
	}

	private static void generateOre(Random seed, int chunkX, int chunkZ, World world, Block ore,
			Predicate<IBlockState> bed, int size, int frequencyMin, int frequencyMax, float rarity, int layerMin,
			int layerMax) {
		int frequency = MathHelper.getInt(seed, frequencyMin, frequencyMax);
		if (seed.nextFloat() < rarity) {
			for (int count = 0; count < frequency; count++) {
				BlockPos pos = new BlockPos(chunkX * 16 + seed.nextInt(16), MathHelper.getInt(seed, layerMin, layerMax),
						chunkZ * 16 + seed.nextInt(16));
				(new WorldGenMinable(ore.getDefaultState(), size, bed)).generate(world, seed, pos);
			}
		}
	}

}
