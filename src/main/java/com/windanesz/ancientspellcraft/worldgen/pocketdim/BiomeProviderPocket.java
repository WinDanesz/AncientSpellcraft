package com.windanesz.ancientspellcraft.worldgen.pocketdim;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftBiomes;
import net.minecraft.world.biome.BiomeProviderSingle;

public class BiomeProviderPocket extends BiomeProviderSingle
{
    /**
     * Instantiates a new biome provider pocket.
     */
    public BiomeProviderPocket() { super(AncientSpellcraftBiomes.pocket); }
}
