package com.windanesz.ancientspellcraft.registry;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.worldgen.pocketdim.WorldProviderPocketDim;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

import javax.annotation.Nullable;

public class AncientSpellcraftDimensions {

	public static final String POCKET_DIM_NAME = "pocket";
	public static final int POCKET_DIM_ID = findNextFreeDimensionID();
	public static final DimensionType POCKET_DIM_TYPE = DimensionType.register(POCKET_DIM_NAME, "_" + POCKET_DIM_NAME, POCKET_DIM_ID, WorldProviderPocketDim.class, true);

	/**
	 * Registers the mod dimensions.
	 * Called by {@link AncientSpellcraft#preInit}
	 */
	public static final void init() {
		DimensionManager.registerDimension(POCKET_DIM_ID, POCKET_DIM_TYPE);
	}

	/**
	 * Register world generators.
	 * Called by {@link AncientSpellcraft#init}
	 */
	public static void registerWorldGenerators() {}

	@Nullable
	private static Integer findNextFreeDimensionID() {
		if (!DimensionManager.isDimensionRegistered(312)) {
			return 312;
		}

		for (int i = 2; i < Integer.MAX_VALUE; i++) {
			if (!DimensionManager.isDimensionRegistered(i)) {
				return i;
			}
		}

		System.out.println("ERROR: Could not find free dimension ID");
		return null;
	}

}