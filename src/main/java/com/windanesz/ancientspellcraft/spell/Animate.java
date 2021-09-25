package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.entity.living.EntityAnimatedItem;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.item.IConjuredItem;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellMinion;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public abstract class Animate extends SpellMinion<EntityAnimatedItem> {

	public Animate(String modid, String name) {
		super(modid, name, EntityAnimatedItem::new);
	}

	public static void equipFromOffhand(EntityAnimatedItem minion, BlockPos pos,
			@Nullable EntityLivingBase caster, SpellModifiers modifiers) {
		if (caster != null && !caster.getHeldItemOffhand().isEmpty()) {
			ItemStack stack = caster.getHeldItemOffhand().copy();
			stack.setCount(1);

			// remove from caster
			caster.getHeldItemOffhand().shrink(1);

			// give to minion
			minion.setHeldItem(EnumHand.MAIN_HAND, stack);
			minion.setItemType(stack.getItem().getRegistryName().toString());
			minion.setHasArmour(false);
		}
	}

	public static void equipWith(EntityAnimatedItem minion, ItemStack stack) {
		minion.setHeldItem(EnumHand.MAIN_HAND, stack);
		minion.setItemType(stack.getItem().getRegistryName().toString());
		minion.setHasArmour(false);
	}

	public static ItemStack conjureItem(SpellModifiers modifiers, Item item) {

		if (item instanceof IConjuredItem) {

			ItemStack stack = new ItemStack(item);
			IConjuredItem.setDurationMultiplier(stack, modifiers.get(WizardryItems.duration_upgrade));
			IConjuredItem.setDamageMultiplier(stack, modifiers.get(SpellModifiers.POTENCY));

			return stack;
		}
		return ItemStack.EMPTY;
	}

	public static ItemStack findAmmo(EntityPlayer player) {
		if (isArrow(player.getHeldItem(EnumHand.OFF_HAND))) {
			return player.getHeldItem(EnumHand.OFF_HAND);
		} else if (isArrow(player.getHeldItem(EnumHand.MAIN_HAND))) {
			return player.getHeldItem(EnumHand.MAIN_HAND);
		} else {
			for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
				ItemStack itemstack = player.inventory.getStackInSlot(i);

				if (isArrow(itemstack)) {
					return itemstack;
				}
			}

			return ItemStack.EMPTY;
		}
	}

	protected static boolean isArrow(ItemStack stack) {
		return stack.getItem() instanceof ItemArrow;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
