package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.item.ItemBattlemageSword;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class RunewordRestoration extends Runeword {

	public static final String MISSING_PERCENT_HP_RESTORED = "missing_percent_hp_restored";

	public RunewordRestoration() {
		super("runeword_restoration", SpellActions.POINT_UP, false);
		addProperties(MISSING_PERCENT_HP_RESTORED);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		if (caster.getHeldItem(hand).getItem() instanceof ItemBattlemageSword) {
			float percent = getProperty(MISSING_PERCENT_HP_RESTORED).floatValue() * modifiers.get(SpellModifiers.POTENCY);
			if (!world.isRemote) {
				caster.heal(caster.getMaxHealth() * percent);
			}
			return true;
		}
		return false;
	}

}