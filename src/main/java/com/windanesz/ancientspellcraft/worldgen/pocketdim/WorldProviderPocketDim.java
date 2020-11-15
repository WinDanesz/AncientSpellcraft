package com.windanesz.ancientspellcraft.worldgen.pocketdim;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftBiomes;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftDimensions;
import com.windanesz.ancientspellcraft.util.PocketDimUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldProviderPocketDim extends WorldProvider {

	@Override
	public boolean hasSkyLight() { return false; }

	@Override
	public IChunkGenerator createChunkGenerator() {
		return new ChunkProviderPocketDim(this.world);
	}

	@Override
	public DimensionType getDimensionType() {
		return AncientSpellcraftDimensions.POCKET_DIM_TYPE;
	}

	@Override
	public boolean isSurfaceWorld() { return false; }

	@Override
	@SideOnly(Side.CLIENT)
	public boolean doesXZShowFog(int x, int z) { return false; }

	@Override
	public float getCloudHeight() { return -10; }

	@Override
	public boolean canDoLightning(Chunk chunk) {
		return false;
	}

	@Override
	public boolean canDoRainSnowIce(Chunk chunk) {
		return false;
	}

	@Override
	public boolean canSnowAt(BlockPos pos, boolean checkLight) {
		return false;
	}

	@Override
	public WorldSleepResult canSleepAt(EntityPlayer player, BlockPos pos) {
		return WorldSleepResult.ALLOW;
	}

	@Override
	public boolean canRespawnHere() { return false; }

	@Override
	public boolean canCoordinateBeSpawn(int x, int z) {
		return false;
	}

	@Override
	public Biome getBiomeForCoords(BlockPos pos) {
		return AncientSpellcraftBiomes.pocket;
	}

	@Override
	public boolean canMineBlock(EntityPlayer player, BlockPos pos) {
		return !(world.getBlockState(pos).getBlock() == Blocks.GRASS);
	}

	@Override
	public boolean isSkyColored() {
		return false;
	}

	/**
	 * Called when a Player is added to the provider's world.
	 */
	@Override
	public void onPlayerAdded(net.minecraft.entity.player.EntityPlayerMP player) {
		PocketDimUtils.onPlayerEnteredPocketDim(player, world);
	}

	/**
	 * Called when a Player is removed from the provider's world.
	 */
	@Override
	public void onPlayerRemoved(net.minecraft.entity.player.EntityPlayerMP player) {
		PocketDimUtils.onPlayerLeftPocketDim(player, world);
	}
}
