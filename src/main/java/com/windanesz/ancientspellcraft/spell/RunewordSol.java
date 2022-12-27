package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.item.ItemBattlemageSword;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class RunewordSol extends Runeword {

	public RunewordSol() {
		super("runeword_sol", SpellActions.POINT_UP, false);
		addProperties(EFFECT_DURATION, CHARGES);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		if (caster.getHeldItem(hand).getItem() instanceof ItemBattlemageSword) {
			ItemBattlemageSword.setActiveRuneword(caster.getHeldItem(hand), this, getProperty(CHARGES).intValue());
			return true;
		}
		return false;
	}
	@Override
	public boolean onAboutToHitEntity(World world, EntityLivingBase caster, EntityLivingBase target, EnumHand hand, ItemStack sword, SpellModifiers modifiers, boolean charged) {
		target.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, getProperty(EFFECT_DURATION).intValue(), 0));
		spendCharge(sword);

		return true;
	}
}
