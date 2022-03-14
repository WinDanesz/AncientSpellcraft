package com.windanesz.ancientspellcraft.worldgen.pocketdim;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASDimensions;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BiomePocket extends Biome {

	/**
	 * Instantiates a new biome pocket.
	 */
	public BiomePocket() {
		super(new BiomeProperties(ASDimensions.POCKET_DIM_NAME)
				.setRainDisabled()
				.setWaterColor(Color.CYAN.getRGB())
				.setTemperature(0.2f)
				.setBaseHeight(1F)
		);
		this.setRegistryName(new ResourceLocation(AncientSpellcraft.MODID, ASDimensions.POCKET_DIM_NAME));

		spawnableCreatureList.clear();
		spawnableMonsterList.clear();
		spawnableWaterCreatureList.clear();
		spawnableCaveCreatureList.clear();

	}

	@Override
	public List<SpawnListEntry> getSpawnableList(EnumCreatureType creatureType) {
		return new ArrayList<>();
	}
}
