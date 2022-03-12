package com.windanesz.ancientspellcraft.spell;

import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class RunewordEndure extends Runeword {

	public RunewordEndure() {
		super("runeword_endure", SpellActions.IMBUE, false);
		addProperties(EFFECT_DURATION);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		apply(caster);
		return true;
	}

	@Override
	public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target, SpellModifiers modifiers) {
		apply(caster);
		return true;
	}

	@Override
	public boolean cast(World world, double x, double y, double z, EnumFacing direction, int ticksInUse, int duration, SpellModifiers modifiers) {
		return false;
	}

	private void apply(EntityLivingBase caster) {
		int effectDuration = getProperty(EFFECT_DURATION).intValue();
		caster.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, effectDuration, 1));
		caster.addPotionEffect(new PotionEffect(WizardryPotions.ward, effectDuration, 1));
		caster.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, effectDuration, 3));
	}
}
