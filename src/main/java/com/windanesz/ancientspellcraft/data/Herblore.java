package com.windanesz.ancientspellcraft.data;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import electroblob.wizardry.registry.WizardryPotions;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;

public class Herblore {

	public enum PlantEffect {

		ALLIUM(MobEffects.REGENERATION),
		AZURE_BLUET(AncientSpellcraftPotions.feather_fall),
		BLUE_ORCHID(MobEffects.WATER_BREATHING),
		DANDELION(MobEffects.SATURATION),
		LILAC(MobEffects.JUMP_BOOST),
		ORANGE_TULIP(AncientSpellcraftPotions.candlelight),
		OXEYE_DAISY(MobEffects.RESISTANCE),
		PEONY(MobEffects.NIGHT_VISION),
		PINK_TULIP(MobEffects.INSTANT_HEALTH),
		POPPY(MobEffects.SPEED),
		RED_TULIP(MobEffects.FIRE_RESISTANCE),
		ROSE_BUSH(MobEffects.HEALTH_BOOST),
		SUNFLOWER(WizardryPotions.fireskin),
		WHITE_TULIP(WizardryPotions.ice_shroud);

		Potion beneficialEffect;

		PlantEffect(Potion beneficialEffect) {
			this.beneficialEffect = beneficialEffect;
		}
	}

}




			/*
  			SPEED;
			SLOWNESS;
			HASTE;
			MINING_FATIGUE;
			STRENGTH;
			INSTANT_HEALTH;
			INSTANT_DAMAGE;
			JUMP_BOOST;
			NAUSEA;
			REGENERATION;
			RESISTANCE;
			FIRE_RESISTANCE;
			WATER_BREATHING;
			INVISIBILITY;
			BLINDNESS;
			NIGHT_VISION;
			HUNGER;
			WEAKNESS;
			POISON;
			WITHER;
			HEALTH_BOOST;
			ABSORPTION;
			SATURATION;
			GLOWING;
			LEVITATION;
			LUCK;
			UNLUCK;

			frost
			transience
			fireskin
			ice_shroud
			static_aura
			decay
			sixth_sense
			arcane_jammer
			mind_trick
			mind_control
			font_of_mana
			fear
			curse_of_soulbinding
			paralysis
			muffle
			ward
			slow_time
			empowerment
			curse_of_enfeeblement
			curse_of_undeath
			containment
			frost_step
			mark_of_sacrifice
			mirage

			curse_of_ender
			unlimited_power
			martyr
			martyr_beneficial
			aquatic_agility
			lava_vision
			magma_strider
			magelight
			candlelight
			curse_ward
			curse_of_death
			time_knot
			curse_temporal_casualty
			feather_fall
			water_walking
			arcane_augmentation
			intensifying_focus
			continuity_charm
			projectile_ward
			bulwark
			arcane_aegis
			fortified_archery*/