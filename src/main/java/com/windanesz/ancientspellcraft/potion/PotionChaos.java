package com.windanesz.ancientspellcraft.potion;

import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.Banish;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

public class PotionChaos extends PotionMagicEffectAS {
	public PotionChaos(String name, boolean isBadEffect, int liquidColour, ResourceLocation texture) {
		super(name, isBadEffect, liquidColour, texture);
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return duration % 60 == 0;
	}

	@Override
	public void performEffect(EntityLivingBase entity, int strength) {
        if (entity.world.isRemote) {
            return;
        }
		switch (strength) {
			case 0:
				entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 60, 3));
				break;
			case 1:
				entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 60));
				break;
			case 2:
				entity.attackEntityFrom(DamageSource.MAGIC, Math.min(10, entity.getMaxHealth() * 0.2f));
				break;
			case 3:
				entity.heal(entity.getMaxHealth() * 0.1f);
				break;
			case 4:
				entity.world.createExplosion(null, entity.posX, entity.posY, entity.posZ, 0.1f, false);
				break;
			case 5:
				entity.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 60));
				break;
			case 6:
				entity.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 60));
				break;
			case 7:
				entity.addPotionEffect(new PotionEffect(MobEffects.SATURATION, 10));
				break;
			case 8:
				entity.setFire(3);
				break;
			case 9:
				entity.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 60));
				break;
			case 10:
				entity.addPotionEffect(new PotionEffect(MobEffects.LEVITATION, 30));
				break;
			case 11:
				entity.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 60));
				break;
			case 12:
				entity.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 30));
				break;
			case 13:
				entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 60));
				break;
			case 14:
				entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 60));
				break;
			case 15:
				entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 60));
				break;
			case 16:
				((Banish) Spells.banish).teleport(entity, entity.world, 10);
				break;
		}
	}

	public static int variants() {
		return 17;
	}
}
