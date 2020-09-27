package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import electroblob.wizardry.potion.Curse;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class PotionCurseEnder extends Curse {

	public PotionCurseEnder() {
		super(true, 0x571e65, new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/potion_icon_curse_of_ender.png"));
		// This needs to be here because registerPotionAttributeModifier doesn't like it if the potion has no name yet.
		this.setPotionName("potion." + AncientSpellcraft.MODID + ":curse_of_ender");
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

	@Override
	public void performEffect(EntityLivingBase entitylivingbase, int strength) {

		// Adapted from EntityEnderman
		if (!entitylivingbase.world.isRemote) {
			if (entitylivingbase.isWet()) {
				entitylivingbase.attackEntityFrom(DamageSource.DROWN, 1.0F);
			}

			// Adapted from EntityZombie
			if (entitylivingbase.world.isDaytime()) {

				float f = entitylivingbase.getBrightness();

				if (f > 0.5F && entitylivingbase.world.rand.nextFloat() * 30.0F < (f - 0.4F) * 4.0F
						&& entitylivingbase.world.canSeeSky(new BlockPos(entitylivingbase.posX,
						entitylivingbase.posY + (double) entitylivingbase.getEyeHeight(), entitylivingbase.posZ))) {

					entitylivingbase.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 40));
				}
			}
		}
	}
}
