package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASTabs;
import com.windanesz.ancientspellcraft.spell.IClassSpell;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.IWorkbenchItem;
import electroblob.wizardry.item.ItemBlankScroll;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellProperties;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class ItemBlankMysticScroll extends ItemBlankScroll implements IWorkbenchItem {

	public ItemBlankMysticScroll() {
		this.setCreativeTab(ASTabs.ANCIENTSPELLCRAFT);
	}

	public int getSpellSlotCount(ItemStack stack) {
		return 1;
	}

	public boolean showTooltip(ItemStack stack) {
		return false;
	}

	public boolean onApplyButtonPressed(EntityPlayer player, Slot centre, Slot crystals, Slot upgrade, Slot[] spellBooks) {
		if (!spellBooks[0].getStack().isEmpty() && !crystals.getStack().isEmpty()) {
			Spell spell = Spell.byMetadata(spellBooks[0].getStack().getItemDamage());
			WizardData data = WizardData.get(player);
			if (spell != Spells.none && spell instanceof IClassSpell && ((IClassSpell) spell).getArmourClass() == ItemWizardArmour.ArmourClass.SAGE
					&& player.isCreative() || data != null && data.hasSpellBeenDiscovered(spell) && spell.isEnabled(new SpellProperties.Context[]{
					SpellProperties.Context.SCROLL})) {
				int cost = spell.getCost() * centre.getStack().getCount();
				if (spell.isContinuous) {
					cost *= 6;
				}

				int manaPerItem = 100;
				if (crystals.getStack().getItem() == WizardryItems.crystal_shard) {
					manaPerItem = 10;
				}

				if (crystals.getStack().getItem() == WizardryItems.grand_crystal) {
					manaPerItem = 400;
				}

				if (crystals.getStack().getCount() * manaPerItem > cost) {
					crystals.decrStackSize(MathHelper.ceil((float)cost / (float)manaPerItem));
					centre.putStack(new ItemStack(ASItems.mystic_scroll, centre.getStack().getCount(), spell.metadata()));
					return true;
				}
			}
		}

		return false;
	}
}
