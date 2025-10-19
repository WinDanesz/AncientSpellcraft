package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import electroblob.wizardry.potion.Curse;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class PotionCurseOfEternalCombustion extends Curse {

    public PotionCurseOfEternalCombustion() {
        super(true, 0xFF4500, new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/potion_icon_curse_of_eternal_combustion.png"));
        this.setPotionName("potion." + AncientSpellcraft.MODID + ":curse_of_eternal_combustion");
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        // Every second
        return true;
    }

    @Override
    public void performEffect(EntityLivingBase entitylivingbase, int strength) {
        if (!entitylivingbase.world.isRemote) {
            // Average of 45 seconds.
            if (entitylivingbase.world.rand.nextInt(400) == 0) {
                int fireDuration = 2 + entitylivingbase.world.rand.nextInt(50);
                entitylivingbase.setFire(fireDuration);
            }
        }
    }
}
