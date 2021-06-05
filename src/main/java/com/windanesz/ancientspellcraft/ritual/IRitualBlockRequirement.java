package com.windanesz.ancientspellcraft.ritual;

import com.windanesz.ancientspellcraft.tileentity.TileRune;
import net.minecraft.world.World;

public interface IRitualBlockRequirement {

	default boolean areInitialRequirementsMet(World world, TileRune centerPiece) {
		return true;
	}

	default boolean areContinuousRequirementsMet(World world, TileRune centerPiece) {
		return true;
	}

}