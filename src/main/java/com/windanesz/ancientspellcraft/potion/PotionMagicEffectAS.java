package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import electroblob.wizardry.potion.PotionMagicEffect;
import net.minecraft.util.ResourceLocation;

public class PotionMagicEffectAS extends PotionMagicEffect {

	public PotionMagicEffectAS(String name, boolean isBadEffect, int liquidColour, ResourceLocation texture) {
		super(isBadEffect, liquidColour, texture);
		this.setPotionName("potion." + AncientSpellcraft.MODID + ":" + name);
	}
}
