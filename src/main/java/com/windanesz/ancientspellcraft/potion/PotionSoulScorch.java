package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.registry.ASPotions;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class PotionSoulScorch extends PotionMagicEffectAS {
	public PotionSoulScorch(String name, boolean isBadEffect, int liquidColour, ResourceLocation texture) {
		super(name, isBadEffect, liquidColour, texture);
	}

	@SubscribeEvent
	public static void onLivingHeal(LivingHealEvent event) {
		if (event.getEntityLiving() != null && event.getEntityLiving().isPotionActive(ASPotions.soul_scorch)) {
			float newAmount = event.getAmount() * 0.4f;
			event.setAmount(newAmount);
		}
	}

}


