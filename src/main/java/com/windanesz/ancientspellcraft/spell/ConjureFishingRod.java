package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.item.IConjuredItem;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WizardryUtilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class ConjureFishingRod extends SpellConjurationAS {

	public ConjureFishingRod() {
		super("conjure_fishing_rod", AncientSpellcraftItems.spectral_fishing_rod);
	}

	/**
	 * Adds this spell's item to the given player's inventory, placing it in the main hand if the main hand is empty.
	 * Returns true if the item was successfully added to the player's inventory, false if there as no space or if the
	 * player already had the item. Override to add special conjuring behaviour.
	 */
	@Override
	protected boolean conjureItem(EntityPlayer caster, SpellModifiers modifiers) {
		if (WizardryUtilities.doesPlayerHaveItem(caster, item))
			return false;

		ItemStack stack = new ItemStack(item);

		IConjuredItem.setDurationMultiplier(stack, modifiers.get(WizardryItems.duration_upgrade));
		IConjuredItem.setDamageMultiplier(stack, modifiers.get(SpellModifiers.POTENCY));

		float potency = modifiers.get(SpellModifiers.POTENCY);

		int level = Math.min((int) (5.5 * (potency - 1.05)), 4); // Apprentice lvl 1, Adv. lvl 2, Master lvl 3, max 4

		if (level > 0) {
			stack.addEnchantment(Enchantments.LURE, level);
			stack.addEnchantment(Enchantments.LUCK_OF_THE_SEA, level);
		}

		if (caster.getHeldItemMainhand().isEmpty()) {
			caster.setHeldItem(EnumHand.MAIN_HAND, stack);
			return true;
		} else {
			return caster.inventory.addItemStackToInventory(stack);
		}
	}
}
