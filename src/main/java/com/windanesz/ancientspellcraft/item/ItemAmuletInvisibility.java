package com.windanesz.ancientspellcraft.item;

import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.item.IWorkbenchItem;
import electroblob.wizardry.registry.WizardryItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemAmuletInvisibility extends ItemASArtefact implements IManaStoringItem, IWorkbenchItem, ITickableArtefact {

	private static final int MANA_CAPACITY = 1500;
	private static final int MANA_COST_PER_TRIGGER = 4;
	private static final int TRIGGER_INTERVAL_TICKS = 40; // 2 seconds
	private static final int EFFECT_DURATION_TICKS = 60; // 3 seconds

	public ItemAmuletInvisibility(EnumRarity rarity, Type type) {
		super(rarity, type);
		setMaxDamage(MANA_CAPACITY);
		setMaxStackSize(1);
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {
		// Prevent regular repair mechanics from restoring mana.
	}

	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase wearer) {
		if (wearer.world.isRemote || wearer.ticksExisted % TRIGGER_INTERVAL_TICKS != 0) return;
		if (getMana(stack) < MANA_COST_PER_TRIGGER) return;

		consumeMana(stack, MANA_COST_PER_TRIGGER, wearer);
		wearer.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, EFFECT_DURATION_TICKS, 0, false, false));
	}

	@Override
	public int getMana(ItemStack stack) {
		return getManaCapacity(stack) - getDamage(stack);
	}

	@Override
	public void setMana(ItemStack stack, int mana) {
		super.setDamage(stack, getManaCapacity(stack) - mana);
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
		return true;
	}

	@Override
	public boolean onApplyButtonPressed(EntityPlayer player, Slot centre, Slot crystals, Slot upgrade, Slot[] spellBooks) {
		boolean changed = false;

		if (crystals.getStack() != ItemStack.EMPTY && !this.isManaFull(centre.getStack())) {
			int chargeDepleted = this.getManaCapacity(centre.getStack()) - this.getMana(centre.getStack());

			int manaPerItem = Constants.MANA_PER_CRYSTAL;
			if (crystals.getStack().getItem() == WizardryItems.crystal_shard) { manaPerItem = Constants.MANA_PER_SHARD; }
			if (crystals.getStack().getItem() == WizardryItems.grand_crystal) { manaPerItem = Constants.GRAND_CRYSTAL_MANA; }

			if (crystals.getStack().getCount() * manaPerItem < chargeDepleted) {
				this.rechargeMana(centre.getStack(), crystals.getStack().getCount() * manaPerItem);
				crystals.decrStackSize(crystals.getStack().getCount());
			} else {
				this.setMana(centre.getStack(), this.getManaCapacity(centre.getStack()));
				crystals.decrStackSize((int) Math.ceil(((double) chargeDepleted) / manaPerItem));
			}

			changed = true;
		}

		return changed;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced) {
		tooltip.add(Wizardry.proxy.translate("item." + Wizardry.MODID + ":wand.mana", new Style().setColor(TextFormatting.BLUE),
				this.getMana(stack), this.getManaCapacity(stack)));
		super.addInformation(stack, world, tooltip, advanced);
	}
}
