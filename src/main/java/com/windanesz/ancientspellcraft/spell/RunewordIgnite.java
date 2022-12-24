package com.windanesz.ancientspellcraft.spell;

import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class RunewordIgnite extends Runeword {

	public static final String BURN_DURATION = "burn_duration";

	public RunewordIgnite() {
		super("runeword_ignite", SpellActions.POINT_UP, false);
		addProperties(CHARGES, BURN_DURATION);
	}

	@Override
	public boolean onAboutToHitEntity(World world, EntityLivingBase caster, EntityLivingBase target, EnumHand hand, ItemStack sword, SpellModifiers modifiers, boolean charged) {
		target.setFire((int) (getProperty(BURN_DURATION).intValue() * modifiers.get(WizardryItems.duration_upgrade)));
		return true;
	}
}
