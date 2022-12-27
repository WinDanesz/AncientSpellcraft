package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.item.ItemBattlemageSword;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.Banish;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class RunewordDisplace extends Runeword {

	public static final String MIN_DISTANCE = "min_distance";
	public static final String MAX_DISTANCE = "max_distance";

	public RunewordDisplace() {
		super("runeword_displace", EnumAction.NONE, false);
		addProperties(CHARGES, MIN_DISTANCE, MAX_DISTANCE);
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
		int charges = ItemBattlemageSword.getRunewordCharges(sword, this);
		if (charges > 0) {
			if(target != null){

				double minRadius = getProperty(MIN_DISTANCE).doubleValue();
				double maxRadius = getProperty(MAX_DISTANCE).doubleValue();
				double radius = (minRadius + world.rand.nextDouble() * maxRadius-minRadius);

				boolean success = ((Banish) Spells.banish).teleport(target, world, radius);
				if (success) {
					spendCharge(sword);
				}
			}
		}

		return false;
	}

}
