package com.windanesz.ancientspellcraft.item;

import electroblob.wizardry.Wizardry;
import electroblob.wizardry.client.DrawingUtils;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.item.IWorkbenchItem;
import electroblob.wizardry.registry.WizardryItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemManaArtefact extends ItemASArtefact implements IManaStoringItem, IWorkbenchItem {

	public ItemManaArtefact(EnumRarity rarity, Type type, int mana) {
		super(rarity, type);
		setMaxDamage(mana);
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {
		// Overridden to do nothing to stop repair things from 'repairing' the mana in a wand
	}

	@Override
	public void setMana(ItemStack stack, int mana) {
		// Using super (which can only be done from in here) bypasses the above override
		super.setDamage(stack, getManaCapacity(stack) - mana);
	}

	@Override
	public int getMana(ItemStack stack) {
		return getManaCapacity(stack) - getDamage(stack);
	}

	@Override
	public int getManaCapacity(ItemStack stack) {
		return this.getMaxDamage(stack);
	}

	@Override
	public int getSpellSlotCount(ItemStack stack) {
		return 0;
	}

	@Override
	public boolean showTooltip(ItemStack stack) {
		return false;
	}

	// Max damage is modifiable with upgrades.
	@Override
	public int getMaxDamage(ItemStack stack) {
		// + 0.5f corrects small float errors rounding down
		return super.getMaxDamage(stack);
	}

	public boolean onApplyButtonPressed(EntityPlayer player, Slot centre, Slot crystals, Slot upgrade, Slot[] spellBooks) {
		boolean changed = false; // Used for advancements
		// Charges wand by appropriate amount
		if (crystals.getStack() != ItemStack.EMPTY && !this.isManaFull(centre.getStack())) {

			int chargeDepleted = this.getManaCapacity(centre.getStack()) - this.getMana(centre.getStack());

			int manaPerItem = Constants.MANA_PER_CRYSTAL;
			if (crystals.getStack().getItem() == WizardryItems.crystal_shard) { manaPerItem = Constants.MANA_PER_SHARD; }
			if (crystals.getStack().getItem() == WizardryItems.grand_crystal) { manaPerItem = Constants.GRAND_CRYSTAL_MANA; }

			if (crystals.getStack().getCount() * manaPerItem < chargeDepleted) {
				// If there aren't enough crystals to fully charge the wand
				this.rechargeMana(centre.getStack(), crystals.getStack().getCount() * manaPerItem);
				crystals.decrStackSize(crystals.getStack().getCount());

			} else {
				// If there are excess crystals (or just enough)
				this.setMana(centre.getStack(), this.getManaCapacity(centre.getStack()));
				crystals.decrStackSize((int) Math.ceil(((double) chargeDepleted) / manaPerItem));
			}

			changed = true;
		}

		return changed;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> text, net.minecraft.client.util.ITooltipFlag advanced) {
		text.add(Wizardry.proxy.translate("item." + Wizardry.MODID + ":wand.mana", new Style().setColor(TextFormatting.BLUE),
				this.getMana(stack), this.getManaCapacity(stack)));
		super.addInformation(stack, world, text, advanced);
	}

	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack){
		return DrawingUtils.mix(0xff8bfe, 0x8e2ee4, (float)getDurabilityForDisplay(stack));
	}

}
