package com.windanesz.ancientspellcraft.spell;

import electroblob.wizardry.item.SpellActions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class RunewordArcane extends Runeword {

	public static final String WEAPON_DAMAGE_PERCENTAGE = "weapon_damage_percentage";

	public RunewordArcane() {
		super("runeword_arcane", SpellActions.POINT_UP, false);
		addProperties(CHARGES, WEAPON_DAMAGE_PERCENTAGE);
		affectsDamage();
	}

	@Override
	public float affectDamage(DamageSource source, float damage, EntityPlayer player, EntityLivingBase target, ItemStack sword) {
		damage = damage * getProperty(WEAPON_DAMAGE_PERCENTAGE).floatValue();
		source.setDamageBypassesArmor();
		source.setMagicDamage();
		spendCharge(sword);
		return damage;
	}
}
