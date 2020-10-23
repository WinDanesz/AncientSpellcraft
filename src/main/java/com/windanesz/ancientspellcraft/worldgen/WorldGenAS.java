package com.windanesz.ancientspellcraft.worldgen;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class WorldGenAS implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		generate(random, chunkX, chunkZ, world, world.provider.getDimension());
	}

	public void generate(Random random, int chunkX, int chunkZ, World world, int dimension) {
//		WorldGenCrystalShardOre.generate(random, chunkX, chunkZ, world, dimension);
	}
}
