package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.entity.construct.EntitySpellTicker;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

/**
 * Implemented by spells which needs a technical entity to place randomly
 */
public interface ISpellTickerConstruct {

	void onUpdate(World world, EntitySpellTicker entityMushroomForest);

	IBlockState getBlock(World world, EntitySpellTicker entityMushroomForest);


}
