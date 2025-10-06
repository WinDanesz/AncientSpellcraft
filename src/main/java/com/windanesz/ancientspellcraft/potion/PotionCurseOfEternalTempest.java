package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import electroblob.wizardry.potion.Curse;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PotionCurseOfEternalTempest extends Curse {

    public PotionCurseOfEternalTempest() {
        super(true, 0xADD8E6, new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/potion_icon_curse_of_death.png")); // Light Blue
        this.setPotionName("potion." + AncientSpellcraft.MODID + ":curse_of_eternal_tempest");
    }

    @Override
    public void performEffect(EntityLivingBase entity, int strength) {
        World world = entity.world;

        // Cloud particles are spawned on both client and server by ParticleBuilder, so no isRemote check needed here for that.
        for (int i = 0; i < 2; i++) {
            double x = entity.posX + (world.rand.nextDouble() - 0.5) * 1.2;
            double y = entity.posY + entity.height + 0.7 + (world.rand.nextDouble() - 0.5) * 0.4;
            double z = entity.posZ + (world.rand.nextDouble() - 0.5) * 1.2;
            ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(x, y, z).clr(0.9f, 0.9f, 0.9f).scale(2.5f).time(20).spawn(world);
        }

        // Lightning strike logic should only run on the server
        if (!world.isRemote) {
            // Average of once every 5 minutes (6000 ticks)
            if (world.canSeeSky(new BlockPos(entity)) && world.rand.nextInt(6000) == 0) {
                EntityLightningBolt lightning = new EntityLightningBolt(world, entity.posX, entity.posY, entity.posZ, false);
                world.addWeatherEffect(lightning);
            }
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        // Run every 20 ticks (1 second)
        return duration % 20 == 0;
    }
}
