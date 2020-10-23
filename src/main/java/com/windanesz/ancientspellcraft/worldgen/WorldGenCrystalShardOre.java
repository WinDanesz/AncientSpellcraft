package com.windanesz.ancientspellcraft.worldgen;

import com.google.common.base.Predicate;
import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeForest;
import net.minecraft.world.biome.BiomeHills;
import net.minecraft.world.biome.BiomeJungle;
import net.minecraft.world.biome.BiomePlains;
import net.minecraft.world.biome.BiomeSwamp;
import net.minecraft.world.biome.BiomeTaiga;
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


			if (biome == Biomes.DESERT || biome == Biomes.DESERT_HILLS || biome == Biomes.MUTATED_DESERT) {
				generateOre(random, chunkX,chunkZ, world, AncientSpellcraftBlocks.crystal_ore_fire, BlockMatcher.forBlock(Blocks.STONE), 4, 2, 8, 1, 5, 30);

			} else if (biome instanceof BiomeSwamp) {
				generateOre(random, chunkX,chunkZ, world, AncientSpellcraftBlocks.crystal_ore_necromancy, BlockMatcher.forBlock(Blocks.STONE), 4, 2, 8, 1, 5, 30);

			} else if (biome instanceof BiomeJungle) {
				generateOre(random, chunkX,chunkZ, world, AncientSpellcraftBlocks.crystal_ore_healing, BlockMatcher.forBlock(Blocks.STONE), 4, 2, 8, 1, 5, 30);

			} else if (biome instanceof BiomeForest) {
				generateOre(random, chunkX,chunkZ, world, AncientSpellcraftBlocks.crystal_ore_earth, BlockMatcher.forBlock(Blocks.STONE), 4, 2, 8, 1, 5, 30);

			} else if (biome instanceof BiomeHills) {
				generateOre(random, chunkX,chunkZ, world, AncientSpellcraftBlocks.crystal_ore_lightning, BlockMatcher.forBlock(Blocks.STONE), 4, 2, 8, 1, 5, 30);

			} else if (biome instanceof BiomeTaiga) {
				generateOre(random, chunkX,chunkZ, world, AncientSpellcraftBlocks.crystal_ore_ice, BlockMatcher.forBlock(Blocks.STONE), 4, 2, 8, 1, 5, 30);

			} else if (biome instanceof BiomePlains) {
				// correct formula 				generateOre(random, chunkX,chunkZ, world, AncientSpellcraftBlocks.crystal_ore_sorcery, BlockMatcher.forBlock(Blocks.STONE), 4, 8, 20, 1, 5, 30);
				generateOre(random, chunkX,chunkZ, world, AncientSpellcraftBlocks.crystal_ore_sorcery, BlockMatcher.forBlock(Blocks.STONE), 4, 2, 8, 1, 5, 30);
			}
		}
	}

	/**
//	 * Adds an Ore Spawn to Minecraft. Simply register all Ores to spawn with this method in your Generation method in
//	 * your IWorldGeneration extending Class
//	 *
//	 * @param world          The World to spawn in
//	 * @param random         A Random object for retrieving random positions within the world to spawn the Block
//	 * @param blockXPos      An int for passing the X-Coordinate for the Generation method
//	 * @param blockZPos      An int for passing the Z-Coordinate for the Generation method
//	 * @param maxX           An int for setting the maximum X-Coordinate values for spawning on the X-Axis on a Per-Chunk basis
//	 * @param maxZ           An int for setting the maximum Z-Coordinate values for spawning on the Z-Axis on a Per-Chunk basis
//	 * @param maxVeinSize    An int for setting the maximum size of a vein
//	 * @param chancesToSpawn An int for the Number of chances available for the Block to spawn per-chunk
//	 * @param minY           An int for the minimum Y-Coordinate height at which this block may spawn
//	 * @param maxY           An int for the maximum Y-Coordinate height at which this block may spawn
	 **/
//	public void addOreSpawn(IBlockState state, World world, Random random, int blockXPos, int blockZPos, int maxX,
//			int maxZ, int maxVeinSize, int chancesToSpawn, int minY, int maxY) {

		// int maxPossY = minY + (maxY - 1);
//		assert maxY > minY : "The maximum Y must be greater than the Minimum Y";
//		assert maxX > 0 && maxX <= 16 : "addOreSpawn: The Maximum X must be greater than 0 and less than 16";
//		assert minY > 0 : "addOreSpawn: The Minimum Y must be greater than 0";
//		assert maxY < 256 && maxY > 0 : "addOreSpawn: The Maximum Y must be less than 256 but greater than 0";
//		assert maxZ > 0 && maxZ <= 16 : "addOreSpawn: The Maximum Z must be greater than 0 and less than 16";
//
//		int diffBtwnMinMaxY = maxY - minY;
//		for (int x = 0; x < chancesToSpawn; x++) {
//			int posX = blockXPos + random.nextInt(maxX);
//			int posY = minY + random.nextInt(diffBtwnMinMaxY);
//			int posZ = blockZPos + random.nextInt(maxZ);
//			// N.B. This method applies the anti-cascading-lag offset itself
//
//			System.out.println("te szar");
//			WorldGenMinable a = new WorldGenMinable(state, maxVeinSize);
//					a.generate(world, random, new BlockPos(posX, posY, posZ));
//		}

//	}

	public static void generate(Random seed, int chunkX, int chunkZ, World world, int dimension) {
		generateOre(seed, chunkX, chunkZ, world, AncientSpellcraftBlocks.crystal_ore_lightning, BlockMatcher.forBlock(Blocks.STONE),
				2, 0, 30,
				1.0F, 5, 30);
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
