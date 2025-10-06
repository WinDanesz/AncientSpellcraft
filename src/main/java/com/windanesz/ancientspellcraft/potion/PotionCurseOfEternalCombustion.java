package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import electroblob.wizardry.potion.Curse;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class PotionCurseOfEternalCombustion extends Curse {

    public PotionCurseOfEternalCombustion() {
        super(true, 0xFF4500, new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/potion_icon_curse_of_death.png")); // TODO: use a proper icon
        this.setPotionName("potion." + AncientSpellcraft.MODID + ":curse_of_eternal_combustion");
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        // Every second
        return duration % 20 == 0;
    }

    @Override
    public void performEffect(EntityLivingBase entitylivingbase, int strength) {
        if (!entitylivingbase.world.isRemote) {
            // Average of 45 seconds.
            if (entitylivingbase.world.rand.nextInt(45) == 0) {
                int fireDuration = 2 + entitylivingbase.world.rand.nextInt(4); // 2-5 seconds
                entitylivingbase.setFire(fireDuration);
            }
        }
    }
}
