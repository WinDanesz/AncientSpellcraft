package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.ASPotions;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class RunewordExorcise extends Runeword {

	public RunewordExorcise() {
		super("runeword_exorcise", SpellActions.POINT_UP, false);
		addProperties(CHARGES, EFFECT_DURATION);
	}

	@Override
	public boolean onAboutToHitEntity(World world, EntityLivingBase caster, EntityLivingBase target, EnumHand hand, ItemStack sword, SpellModifiers modifiers, boolean charged) {
		if (ASUtils.isEntityConsideredUndead(target)) {
			target.setFire(getProperty(EFFECT_DURATION).intValue() / 20);
			target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, getProperty(EFFECT_DURATION).intValue()));
		}

		int amplifier = 0;
		if (target.isPotionActive(ASPotions.magical_exhaustion)) {
			//noinspection ConstantConditions
			amplifier = target.getActivePotionEffect(ASPotions.magical_exhaustion).getAmplifier() + 1;

			// if the effect stacks, this backfires on the caster too for a smaller extent
			target.addPotionEffect(new PotionEffect(ASPotions.magical_exhaustion,  (int)(getProperty(EFFECT_DURATION).floatValue()* 0.5f), 0));
		}

		// affect target
		target.addPotionEffect(new PotionEffect(ASPotions.magical_exhaustion, getProperty(EFFECT_DURATION).intValue(), amplifier));

		spendCharge(sword);
		return true;
	}
}
