package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.ASPotions;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class RunewordExpose extends Runeword {


	public RunewordExpose() {
		super("runeword_expose", SpellActions.POINT_UP, false);
		addProperties(CHARGES, EFFECT_DURATION);
	}

	@Override
	public boolean onAboutToHitEntity(World world, EntityLivingBase caster, EntityLivingBase target, EnumHand hand, ItemStack sword, SpellModifiers modifiers, boolean charged) {
		int amplifier = 0;
		if (target.isPotionActive(ASPotions.degraded_armor)) {
			//noinspection ConstantConditions
			amplifier = target.getActivePotionEffect(ASPotions.degraded_armor).getAmplifier() + 1;
		}

		// affect target
		target.addPotionEffect(new PotionEffect(ASPotions.degraded_armor, getProperty(EFFECT_DURATION).intValue(), amplifier));

		spendCharge(sword);
		return true;
	}
}
