package com.windanesz.ancientspellcraft.ritual;

import com.windanesz.ancientspellcraft.tileentity.TileRune;
import net.minecraft.entity.EntityLivingBase;

import javax.annotation.Nullable;

public interface IHasRightClickEffect {

	boolean onRightClick(TileRune centerPiece, @Nullable EntityLivingBase breakerEntity);

	/**
	 * By default this applies the same effect as onRightClick
	 */
	default void onBreak(TileRune centerPiece, @Nullable EntityLivingBase breakerEntity) {
		onRightClick(centerPiece, breakerEntity);
	}
}
