package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.integration.artemislib.ASArtemisLibIntegration;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import java.util.UUID;

public class PotionShrinkage extends PotionMagicEffectAS {

	public static final int MAX_LEVEL = 1;

	public static final UUID shrinkageUUID = UUID.fromString("8749e55d-78fa-455d-9680-19997feb3df9");

	public PotionShrinkage(String name, boolean isBadEffect, int liquidColour, ResourceLocation texture) {
		super(name, isBadEffect, liquidColour, texture);

		if (ASArtemisLibIntegration.enabled()) {
			//noinspection ConstantConditions
			this.registerPotionAttributeModifier(ASArtemisLibIntegration.getHeightAttribute(), shrinkageUUID.toString(), -0.3D, Constants.AttributeModifierOperation.MULTIPLY);
			//noinspection ConstantConditions
			this.registerPotionAttributeModifier(ASArtemisLibIntegration.getWidthAttribute(), shrinkageUUID.toString(), -0.3D, Constants.AttributeModifierOperation.MULTIPLY);

			// vanilla attributes
			this.registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED, shrinkageUUID.toString(), 0.25D, Constants.AttributeModifierOperation.MULTIPLY);
			this.registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, shrinkageUUID.toString(), 0.1D, Constants.AttributeModifierOperation.MULTIPLY);

			this.registerPotionAttributeModifier(SharedMonsterAttributes.KNOCKBACK_RESISTANCE, shrinkageUUID.toString(), -0.5D, Constants.AttributeModifierOperation.MULTIPLY);
			this.registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE, shrinkageUUID.toString(), -0.25D, Constants.AttributeModifierOperation.MULTIPLY);
			this.registerPotionAttributeModifier(SharedMonsterAttributes.MAX_HEALTH, shrinkageUUID.toString(), -0.25D, Constants.AttributeModifierOperation.MULTIPLY);

			this.registerPotionAttributeModifier(SharedMonsterAttributes.ARMOR, shrinkageUUID.toString(), -2D, Constants.AttributeModifierOperation.ADD);
		}
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

	@Override
	public void applyAttributesModifiersToEntity(EntityLivingBase entity, AbstractAttributeMap attributeMapIn, int amplifier) {
		if (amplifier <= MAX_LEVEL) {
			super.applyAttributesModifiersToEntity(entity, attributeMapIn, amplifier);
			if (entity.getHealth() > entity.getMaxHealth()) {
				entity.setHealth(entity.getMaxHealth());
			}
		}
	}

	@Override
	public void performEffect(EntityLivingBase entitylivingbase, int strength) {
		if (strength > MAX_LEVEL) {
			if (entitylivingbase.isPotionActive(AncientSpellcraftPotions.shrinkage)) {
				PotionEffect effect = entitylivingbase.getActivePotionEffect(AncientSpellcraftPotions.shrinkage);
				if (effect != null) {
					int duration = effect.getDuration();
					entitylivingbase.removePotionEffect(AncientSpellcraftPotions.shrinkage);
					entitylivingbase.addPotionEffect(new PotionEffect(AncientSpellcraftPotions.shrinkage, duration, MAX_LEVEL));
				}
			}
		}

		if (entitylivingbase.ticksExisted % 39 == 0) {

			if (entitylivingbase.isPotionActive(AncientSpellcraftPotions.growth)) {
				entitylivingbase.removePotionEffect(AncientSpellcraftPotions.shrinkage);
				entitylivingbase.removePotionEffect(AncientSpellcraftPotions.growth);
			}

			entitylivingbase.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 40, Math.max(0, strength - 1), true, false));

			if (strength > 0) {
				entitylivingbase.addPotionEffect(new PotionEffect(AncientSpellcraftPotions.feather_fall, 40, 0, true, false));
			}
		}
	}

}
