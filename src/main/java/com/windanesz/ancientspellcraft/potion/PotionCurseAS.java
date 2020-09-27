package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import electroblob.wizardry.potion.Curse;
import net.minecraft.util.ResourceLocation;

public class PotionCurseAS extends Curse {

	public PotionCurseAS(String name, boolean isBadEffect, int liquidColour, ResourceLocation texture) {
		super(isBadEffect, liquidColour, texture);
		this.setPotionName("potion." + AncientSpellcraft.MODID + ":" + name);

	}
}
