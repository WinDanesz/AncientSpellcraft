package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import electroblob.wizardry.potion.PotionMagicEffect;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PotionBubbleHead extends PotionMagicEffect {

	private static int MAX_AIR_AMOUNT = 300;

	public PotionBubbleHead() {

		super(false, 0xE6E6FF, new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/potion_icon_bubble_head.png"));
		this.setPotionName("potion." + AncientSpellcraft.MODID + ":bubble_head");
		this.setBeneficial();
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

	@Override
	public void performEffect(EntityLivingBase entitylivingbase, int strength) {
		if (entitylivingbase.world.getTotalWorldTime() % 10 == 0) {
			if (entitylivingbase.getAir() < MAX_AIR_AMOUNT) {
				int newAmount = Math.min(MAX_AIR_AMOUNT, entitylivingbase.getAir() + 20);
				entitylivingbase.setAir(newAmount);
			}
		}
	}

	@SubscribeEvent
	public static void onLivingDamageEvent(LivingDamageEvent event) {
		// We have no way of checking if it's a spawner in getCanSpawnHere() so this has to be done here instead
		if (event.getEntityLiving().isPotionActive(AncientSpellcraftPotions.bubble_head)) {
			event.getEntityLiving().removePotionEffect(AncientSpellcraftPotions.bubble_head);
		}

	}
}


