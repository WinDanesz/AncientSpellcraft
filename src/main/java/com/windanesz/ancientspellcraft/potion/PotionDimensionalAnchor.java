package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class PotionDimensionalAnchor extends PotionMagicEffectAS {

    private static final int DEFAULT_COLOR = 0xc558d6; // Original purple color

    public PotionDimensionalAnchor() {
        super("dimensional_anchor", true, DEFAULT_COLOR,
              new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/potion_icon_dimensional_anchor.png"));
    }

    @Override
    public int getLiquidColor() {
        // Use proxy method to safely check if player is in pocket dimension
        if (AncientSpellcraft.proxy.isPlayerInPocketDimension()) {
            return 0x000000; // Black color - stops particles from rendering
        }
        return DEFAULT_COLOR; // Default purple color
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true; // Execute performEffect every tick
    }

    @Override
    public void performEffect(EntityLivingBase entity, int strength) {
        super.performEffect(entity, strength);
    }
}
