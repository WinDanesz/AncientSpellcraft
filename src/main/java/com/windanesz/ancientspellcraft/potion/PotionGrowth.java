package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.integration.artemislib.ASArtemisLibIntegration;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import electroblob.wizardry.item.ItemArtefact;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import java.util.UUID;

public class PotionGrowth extends PotionMagicEffectAS {

	public static final int MAX_LEVEL = 2;

	public static final UUID growthUUID = UUID.fromString("127c4034-9054-4239-82ea-199ed36ab287");

	public PotionGrowth(String name, boolean isBadEffect, int liquidColour, ResourceLocation texture) {
		super(name, isBadEffect, liquidColour, texture);

		if (ASArtemisLibIntegration.enabled()) {
			//noinspection ConstantConditions
			this.registerPotionAttributeModifier(ASArtemisLibIntegration.getHeightAttribute(), growthUUID.toString(), 0.5D, Constants.AttributeModifierOperation.MULTIPLY);
			//noinspection ConstantConditions
			this.registerPotionAttributeModifier(ASArtemisLibIntegration.getWidthAttribute(), growthUUID.toString(), 0.5D, Constants.AttributeModifierOperation.MULTIPLY);

			// vanilla attributes
			this.registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED, growthUUID.toString(), -0.2D, Constants.AttributeModifierOperation.MULTIPLY);
			// this.registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, growthUUID.toString(), -0.1D, Constants.AttributeModifierOperation.MULTIPLY);

			this.registerPotionAttributeModifier(SharedMonsterAttributes.KNOCKBACK_RESISTANCE, growthUUID.toString(), 0.5D, Constants.AttributeModifierOperation.MULTIPLY);
			this.registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE, growthUUID.toString(), 0.25D, Constants.AttributeModifierOperation.MULTIPLY);
			this.registerPotionAttributeModifier(SharedMonsterAttributes.MAX_HEALTH, growthUUID.toString(), 0.3D, Constants.AttributeModifierOperation.MULTIPLY);
			this.registerPotionAttributeModifier(SharedMonsterAttributes.ARMOR, growthUUID.toString(), 2D, Constants.AttributeModifierOperation.ADD);
			this.registerPotionAttributeModifier(SharedMonsterAttributes.ARMOR_TOUGHNESS, growthUUID.toString(), 1D, Constants.AttributeModifierOperation.MULTIPLY);
		}

	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

	@Override
	public void applyAttributesModifiersToEntity(EntityLivingBase entity, AbstractAttributeMap attributeMapIn, int amplifier) {
		if (entity instanceof EntityPlayer) {
			if (ItemArtefact.isArtefactActive((EntityPlayer) entity, AncientSpellcraftItems.amulet_persistence)) {
				entity.removePotionEffect(AncientSpellcraftPotions.growth);
				return;
			}

		}

		if (amplifier <= MAX_LEVEL) {
			super.applyAttributesModifiersToEntity(entity, attributeMapIn, amplifier);
		}
	}

	@Override
	public void performEffect(EntityLivingBase entitylivingbase, int strength) {
		if (strength > MAX_LEVEL) {
			if (entitylivingbase.isPotionActive(AncientSpellcraftPotions.growth)) {
				PotionEffect effect = entitylivingbase.getActivePotionEffect(AncientSpellcraftPotions.growth);
				if (effect != null) {
					int duration = effect.getDuration();
					entitylivingbase.removePotionEffect(AncientSpellcraftPotions.growth);
					entitylivingbase.addPotionEffect(new PotionEffect(AncientSpellcraftPotions.growth, duration, MAX_LEVEL));
				}
			}
		}

		if (entitylivingbase.ticksExisted % 39 == 0) {

			if (entitylivingbase.isPotionActive(AncientSpellcraftPotions.shrinkage)) {
				entitylivingbase.removePotionEffect(AncientSpellcraftPotions.shrinkage);
				entitylivingbase.removePotionEffect(AncientSpellcraftPotions.growth);
			}

			entitylivingbase.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 40, strength + 1, true, true));
			entitylivingbase.addPotionEffect(new PotionEffect(MobEffects.HASTE, 40, 0, true, true));
		}
	}
}
