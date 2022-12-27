package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class RunewordExorcise extends Runeword {

	public RunewordExorcise() {
		super("runeword_exorcise", SpellActions.POINT_UP, false);
		addProperties(CHARGES, EFFECT_DURATION, DAMAGE_MULTIPLIER);
		affectsDamage();
	}

	@Override
	public boolean onAboutToHitEntity(World world, EntityLivingBase caster, EntityLivingBase target, EnumHand hand, ItemStack sword, SpellModifiers modifiers, boolean charged) {
		if (ASUtils.isEntityConsideredUndead(target)) {
			target.setFire(getProperty(EFFECT_DURATION).intValue() / 20);
			target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, getProperty(EFFECT_DURATION).intValue()));
		}
		return true;
	}

	@Override
	public float affectDamage(DamageSource source, float damage, EntityPlayer player, EntityLivingBase target, ItemStack sword) {
		if (ASUtils.isEntityConsideredUndead(target)) {
			damage = damage * getProperty(DAMAGE_MULTIPLIER).floatValue();
			spendCharge(sword);
		}

		return damage;
	}
}
