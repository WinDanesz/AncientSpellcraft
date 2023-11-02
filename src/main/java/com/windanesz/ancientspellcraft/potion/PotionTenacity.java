package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASPotions;
import electroblob.wizardry.potion.PotionMagicEffect;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.stream.Collectors;

public class PotionTenacity extends PotionMagicEffect {

	private static int MAX_AIR_AMOUNT = 300;

	public PotionTenacity() {

		super(false, 0xE6E6FF, new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/potion_icon_tenacity.png"));
		this.setPotionName("potion." + AncientSpellcraft.MODID + ":tenacity");
		this.setBeneficial();
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

	// Halves the current duration of the marked potion effect
	@Override
	public void performEffect(EntityLivingBase entitylivingbase, int strength) {
//		if (entitylivingbase.world.getTotalWorldTime() % 5 == 0) {
//			String prefix = "TenacityShouldReduceDuration_";
//			List<String> potionRegistryNameList = entitylivingbase.getTags()
//					.stream()
//					.filter(s -> s.matches("TenacityShouldReduceDuration_.*"))
//					.map(s -> s.substring(prefix.length()))
//					.collect(Collectors.toList());
//
//			for (String effectName : potionRegistryNameList) {
//				Potion potion = Potion.getPotionFromResourceLocation(effectName);
//
//				if (potion != null) {
//					PotionEffect effect = entitylivingbase.getActivePotionEffect(potion);
//					if (effect != null) {
//						int newDuration = effect.getDuration() / 2;
//						if (newDuration > 0) {
//							entitylivingbase.addPotionEffect(new PotionEffect(potion, newDuration, effect.getAmplifier(), effect.getIsAmbient(), effect.doesShowParticles()));
//						} else {
//							entitylivingbase.removePotionEffect(potion);
//						}
//					}
//				}
//			//	entitylivingbase.removeTag(prefix + effectName);
//			}
//		}
	}

}


