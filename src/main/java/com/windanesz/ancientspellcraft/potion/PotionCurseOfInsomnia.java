package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import electroblob.wizardry.potion.Curse;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class PotionCurseOfInsomnia extends Curse {

    public PotionCurseOfInsomnia() {
        super(true, 0x483D8B, new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/potion_icon_curse_of_death.png")); // Dark Slate Blue
        this.setPotionName("potion." + AncientSpellcraft.MODID + ":curse_of_insomnia");
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return false; // No periodic effect needed
    }

    @Override
    public void performEffect(EntityLivingBase entitylivingbase, int strength) {
        // The presence of the curse itself prevents sleeping. No further action needed.
    }
}
