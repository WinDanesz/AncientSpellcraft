package com.windanesz.ancientspellcraft.spell;

import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class RunewordDisarm extends Runeword {

	public RunewordDisarm() {
		super("runeword_disarm", SpellActions.POINT_UP, false);
		addProperties(CHARGES, EFFECT_DURATION);
		affectsDamage();
	}

	@Override
	public boolean onAboutToHitEntity(World world, EntityLivingBase caster, EntityLivingBase target, EnumHand hand, ItemStack sword, SpellModifiers modifiers, boolean charged) {
		int duration = (int) (getProperty(EFFECT_DURATION).intValue() * modifiers.get(WizardryItems.duration_upgrade));
		if (target instanceof EntityPlayer) {
			EntityPlayer targetPlayer = (EntityPlayer) target;
			if (!targetPlayer.getHeldItemMainhand().isEmpty()) {
				targetPlayer.getCooldownTracker().setCooldown(targetPlayer.getHeldItemMainhand().getItem(), duration);
			}
			if (!targetPlayer.getHeldItemOffhand().isEmpty()) {
				targetPlayer.getCooldownTracker().setCooldown(targetPlayer.getHeldItemOffhand().getItem(), duration);
			}
		}
		spendCharge(sword);
		return true;
	}
}
