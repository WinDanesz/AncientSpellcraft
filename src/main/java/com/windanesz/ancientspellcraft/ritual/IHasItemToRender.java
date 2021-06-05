package com.windanesz.ancientspellcraft.ritual;

import com.windanesz.ancientspellcraft.tileentity.TileRune;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IHasItemToRender {

	@SideOnly(Side.CLIENT)
	void renderItem(TileRune tileentity, float partialTicks, int destroyStage, float alpha);

}



