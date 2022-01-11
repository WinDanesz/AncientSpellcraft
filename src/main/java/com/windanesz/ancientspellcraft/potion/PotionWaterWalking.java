package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import electroblob.wizardry.potion.PotionMagicEffect;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class PotionWaterWalking extends PotionMagicEffect {

	public PotionWaterWalking() {
		super(false, 0xE6E6FF, new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/potion_icon_water_walking.png"));
		this.setPotionName("potion." + AncientSpellcraft.MODID + ":water_walking");
		this.setBeneficial();
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true; // Execute the effect every tick
	}

	@Override
	public void performEffect(EntityLivingBase entity, int strength) {

		if (entity != null) {
			if (entity.isInWater()) {

				entity.motionY = entity.motionY < 0.0f ? 0.001f : entity.motionY;

				// increase speed on water
				entity.motionX *= (1.1f + 0.025 * 1.2);
				entity.motionZ *= (1.1f + 0.025 * 1.2);

			}
		}
	}


}

