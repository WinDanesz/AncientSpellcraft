package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.potion.PotionMagicEffect;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class PotionFeatherFall extends PotionMagicEffect {

	public PotionFeatherFall() {
		super(false, 0xE6E6FF, new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/potion_icon_feather_fall.png"));
		this.setPotionName("potion." + AncientSpellcraft.MODID + ":feather_fall");
		this.setBeneficial();
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true; // Execute the effect every tick
	}

	@Override
	public void performEffect(EntityLivingBase entity, int strength) {

		if (entity != null) {

			if (!Wizardry.settings.replaceVanillaFallDamage) {
				entity.fallDistance = 0;
			}
			entity.motionY = entity.motionY < -0.3f ? entity.motionY + 0.1f : entity.motionY;
		}
	}


}

