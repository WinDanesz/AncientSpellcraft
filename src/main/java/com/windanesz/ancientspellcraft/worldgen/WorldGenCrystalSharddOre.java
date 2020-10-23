//package com.windanesz.ancientspellcraft.worldgen;
//
//import com.windanesz.ancientspellcraft.Settings;
//import com.windanesz.ancientspellcraft.registry.AncientSpellcraftBlocks;
//import net.minecraft.block.state.IBlockState;
//import net.minecraft.init.Blocks;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//import net.minecraft.world.biome.Biome;
//import net.minecraft.world.biome.BiomeDesert;
//import net.minecraft.world.biome.BiomeForest;
//import net.minecraft.world.biome.BiomeHills;
//import net.minecraft.world.biome.BiomeJungle;
//import net.minecraft.world.biome.BiomePlains;
//import net.minecraft.world.biome.BiomeSwamp;
//import net.minecraft.world.biome.BiomeTaiga;
//import net.minecraft.world.chunk.IChunkProvider;
//import net.minecraft.world.gen.IChunkGenerator;
//import net.minecraft.world.gen.feature.WorldGenMinable;
//import net.minecraftforge.fml.common.IWorldGenerator;
//
//import java.util.Random;
//
//public class WorldGenCrystalSharddOre implements IWorldGenerator {
//
//	@Override
//	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
//		if (world.provider.getDimension() == 0) {
//
//			Biome biome = world.getBiome(new BlockPos(chunkX * 16, 0, chunkX * 16));
//			IBlockState state;
//
//			if (biome instanceof BiomeDesert) {
//						state = Blocks.ACACIA_STAIRS.getDefaultState();
////				state = AncientSpellcraftBlocks.crystal_ore_fire.getDefaultState();
//				System.out.println(state.getBlock());
//			} else if (biome instanceof BiomeSwamp) {
//				//				state = Blocks.BIRCH_STAIRS.getDefaultState();
//				state = AncientSpellcraftBlocks.crystal_ore_necromancy.getDefaultState();
//				System.out.println(state.getBlock());
//			} else if (biome instanceof BiomeJungle) {
//				//				state = Blocks.BRICK_STAIRS.getDefaultState();
//				state = AncientSpellcraftBlocks.crystal_ore_healing.getDefaultState();
//				System.out.println(state.getBlock());
//			} else if (biome instanceof BiomeForest) {
//				//				state = Blocks.JUNGLE_STAIRS.getDefaultState();
//				state = AncientSpellcraftBlocks.crystal_ore_earth.getDefaultState();
//				System.out.println(state.getBlock());
//			} else if (biome instanceof BiomeHills) {
//				//				state = Blocks.SANDSTONE_STAIRS.getDefaultState();
//				state = AncientSpellcraftBlocks.crystal_ore_lightning.getDefaultState();
//				System.out.println(state.getBlock());
//			} else if (biome instanceof BiomeTaiga) {
//				//				state = Blocks.NETHER_BRICK_STAIRS.getDefaultState();
//				state = AncientSpellcraftBlocks.crystal_ore_ice.getDefaultState();
//				System.out.println(state.getBlock());
//			} else if (biome instanceof BiomePlains) {
//				//				state = Blocks.DARK_OAK_STAIRS.getDefaultState();
//				state = AncientSpellcraftBlocks.crystal_ore_sorcery.getDefaultState();
//				System.out.println(state.getBlock());
//			} else {
//				return;
//			}
//
//			generateOverworld(state, random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
//		}
//
//	}
//
//	private void generateOverworld(IBlockState state, Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
//		generateOre(state, world, random, chunkX * 16, chunkZ * 16, 5, 30, random.nextInt(7) + 4, Settings.generalSettings.crystal_ore_rarity);
//	}
//
//	private void generateOre(IBlockState ore, World world, Random random, int x, int z, int minY, int maxY, int size, int chances) {
//		int deltaY = maxY - minY;
//
//		for (int i = 0; i < chances; i++) {
//			BlockPos pos = new BlockPos(x + random.nextInt(16), minY + random.nextInt(deltaY), z + random.nextInt(16));
//
//			WorldGenMinable generator = new WorldGenMinable(ore, size);
//			generator.generate(world, random, pos);
//		}
//	}
//}
