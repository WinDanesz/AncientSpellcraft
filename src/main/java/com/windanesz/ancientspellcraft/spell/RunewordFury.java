package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.item.ItemBattlemageSword;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.HashMap;

public class RunewordFury extends Runeword {

	public static final String CHARGES_TAG = "charges";
	public static final String MAX_CHARGE_STACKS = "max_charge_stacks";
	public static final String DMG_PERCENT_INCREASE_PER_HIT = "dmg_percent_increase_per_hit";

	public RunewordFury() {
		super("runeword_fury", EnumAction.NONE, false);
		addProperties(DMG_PERCENT_INCREASE_PER_HIT, MAX_CHARGE_STACKS);
		setPassive();
		affectsAttributes();
		enableTickEffect();
	}

	@Override
	public void tick(EntityLivingBase wielder, ItemStack sword) {
		if (wielder.ticksExisted % 40 == 0) {
			decrementFury(sword);
		}
	}

	@Override
	public boolean onAboutToHitEntity(World world, EntityLivingBase caster, EntityLivingBase target, EnumHand hand, ItemStack sword, SpellModifiers modifiers, boolean charged) {
		if (!caster.world.isRemote) {
			incrementFury(sword);
		}
		return true;
	}

	public void incrementFury(ItemStack sword) {
		HashMap<Runeword, NBTTagCompound> data = ItemBattlemageSword.getTemporaryRunewordData(sword);
		if (data.containsKey(this)) {
			NBTTagCompound fury = data.get(this);
			int charges;
			if (fury.hasKey(CHARGES_TAG)) {
				charges = fury.getInteger(CHARGES_TAG);
				if (charges < getProperty(MAX_CHARGE_STACKS).intValue()) {
					charges++;
					fury.setInteger(CHARGES_TAG, charges);
					ItemBattlemageSword.setTemporaryRuneWordData(sword, this, fury);
				}
			}
		} else {
			NBTTagCompound fury = new NBTTagCompound();
			fury.setInteger(CHARGES_TAG, 1);
			ItemBattlemageSword.setTemporaryRuneWordData(sword, this, fury);
		}
	}

	public void decrementFury(ItemStack sword) {
		HashMap<Runeword, NBTTagCompound> data = ItemBattlemageSword.getTemporaryRunewordData(sword);
		if (data.containsKey(this)) {
			NBTTagCompound fury = data.get(this);
			int charges;
			if (fury.hasKey(CHARGES_TAG)) {
				charges = fury.getInteger(CHARGES_TAG);
				if (charges > 0) {
					charges--;
					fury.setInteger(CHARGES_TAG, charges);
					ItemBattlemageSword.setTemporaryRuneWordData(sword, this, fury);
				} else {
					ItemBattlemageSword.setTemporaryRuneWordData(sword, this, null);
				}
			}
		}
	}
}
