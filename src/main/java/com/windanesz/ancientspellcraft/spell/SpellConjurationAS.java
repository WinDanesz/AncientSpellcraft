package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.IConjuredItem;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellConjuration;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

import static electroblob.wizardry.util.InventoryUtils.doesPlayerHaveItem;

public class SpellConjurationAS extends SpellConjuration {

	public SpellConjurationAS(String name, Item item) {
		super(AncientSpellcraft.MODID, name, item);
	}

	/**
	 * Adds this spell's item to the given player's inventory, placing it in the main hand if the main hand is empty.
	 * Returns true if the item was successfully added to the player's inventory, false if there as no space or if the
	 * player already had the item. Override to add special conjuring behaviour.
	 */
	@Override
	protected boolean conjureItem(EntityPlayer caster, SpellModifiers modifiers) {

		ItemStack stack = new ItemStack(item);

		if (doesPlayerHaveItem(caster, item)) return false;

		IConjuredItem.setDurationMultiplier(stack, modifiers.get(WizardryItems.duration_upgrade));
		IConjuredItem.setDamageMultiplier(stack, modifiers.get(SpellModifiers.POTENCY));

		addItemExtras(caster, stack, modifiers);

		if (caster.getHeldItemMainhand().isEmpty()) {
			caster.setHeldItem(EnumHand.MAIN_HAND, stack);
			return true;
		} else if (caster.getHeldItemOffhand().isEmpty()) {
			caster.setHeldItem(EnumHand.OFF_HAND, stack);
			return true;
		} else {
			return caster.inventory.addItemStackToInventory(stack);
		}
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
