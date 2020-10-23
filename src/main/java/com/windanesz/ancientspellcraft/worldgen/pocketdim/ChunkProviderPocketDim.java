package com.windanesz.ancientspellcraft.worldgen.pocketdim;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;

import java.util.List;

public class ChunkProviderPocketDim implements IChunkGenerator {

	protected World world;

	/**
	 * Instantiates a new chunk generator.
	 */
	public ChunkProviderPocketDim(World worldIn) {
		System.out.println("Constructing ChunkProviderPocketDim");

		world = worldIn;
	}

	/**
	 * Generates the chunk at the specified position, from scratch.
	 *
	 * @param x the par chunk X
	 * @param z the par chunk Z
	 * @return the chunk
	 */
	@Override
	public Chunk generateChunk(int x, int z) {
		ChunkPrimer chunkprimer = new ChunkPrimer();

		Chunk chunk = new Chunk(this.world, chunkprimer, x, z);
		chunk.resetRelightChecks();

		return chunk;
	}

	/**
	 * Generate initial structures in this chunk, e.g. mineshafts, temples, lakes, and dungeons
	 *
	 * @param parChunkX Chunk x coordinate
	 * @param parChunkZ Chunk z coordinate
	 */
	@Override
	public void populate(int parChunkX, int parChunkZ) {}

	@Override
	public boolean generateStructures(Chunk chunkIn, int x, int z) { return false; }

	@Override
	public void recreateStructures(Chunk chunkIn, int x, int z) {
	}

	@Override
	public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
		return null;
	}

	@Override
	public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
		return false;
	}

	@Override
	public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
		Biome biome = this.world.getBiome(pos);
		return biome.getSpawnableList(creatureType);
	}
}