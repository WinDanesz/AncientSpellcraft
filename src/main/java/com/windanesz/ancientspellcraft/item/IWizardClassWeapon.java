package com.windanesz.ancientspellcraft.item;

import electroblob.wizardry.item.IManaStoringItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;

import java.util.Optional;

public interface IWizardClassWeapon {

	String CHARGE_PROGRESS = "charge_progress";

	default int getMana(ItemStack weapon, EnumHand otherHand, EntityLivingBase entity) {
		Optional<ItemStack> manaStoringItem = manaStoringItemStack(weapon, otherHand, entity);
		return manaStoringItem.map(stack -> ((IManaStoringItem) stack.getItem()).getMana(stack)).orElse(0);
	}

	default void setMana(ItemStack weapon, EnumHand otherHand, EntityLivingBase entity, int amount) {
		Optional<ItemStack> manaStoringItem = manaStoringItemStack(weapon, otherHand, entity);
		manaStoringItem.ifPresent(stack -> ((IManaStoringItem) stack.getItem()).setMana(stack, amount));
	}

	default void rechargeMana(ItemStack weapon, EnumHand otherHand, EntityLivingBase entity, int amount) {
		Optional<ItemStack> manaStoringItem = manaStoringItemStack(weapon, otherHand, entity);
		manaStoringItem.ifPresent(stack -> ((IManaStoringItem) stack.getItem()).rechargeMana(stack, amount));
	}

	default void consumeMana(ItemStack weapon, EnumHand otherHand, EntityLivingBase entity, int amount) {
		Optional<ItemStack> manaStoringItem = manaStoringItemStack(weapon, otherHand, entity);
		manaStoringItem.ifPresent(stack -> ((IManaStoringItem) stack.getItem()).consumeMana(stack, amount, entity));
	}

	default boolean isManaFull(ItemStack weapon, EnumHand otherHand, EntityLivingBase entity) {
		Optional<ItemStack> manaStoringItem = manaStoringItemStack(weapon, otherHand, entity);
		return manaStoringItem.map(stack -> ((IManaStoringItem) stack.getItem()).isManaFull(stack)).orElse(true);
	}

	default boolean isManaEmpty(ItemStack weapon, EnumHand otherHand, EntityLivingBase entity) {
		Optional<ItemStack> manaStoringItem = manaStoringItemStack(weapon, otherHand, entity);
		return manaStoringItem.map(stack -> ((IManaStoringItem) stack.getItem()).isManaEmpty(stack)).orElse(false);
	}

	default int getManaCapacity(ItemStack weapon, EnumHand otherHand, EntityLivingBase entity) {
		Optional<ItemStack> manaStoringItem = manaStoringItemStack(weapon, otherHand, entity);
		return manaStoringItem.map(stack -> ((IManaStoringItem) stack.getItem()).getManaCapacity(stack)).orElse(0);
	}

	default Optional<ItemStack> manaStoringItemStack(ItemStack weapon, EnumHand otherHand, EntityLivingBase entity) {

		if (weapon.getItem() instanceof IWizardClassWeapon) {

			ItemStack otherHandItem = entity.getHeldItem(otherHand);

			if (!otherHandItem.isEmpty() && otherHandItem.getItem() instanceof IManaStoringItem) {
				return Optional.of(otherHandItem);
			}
		}

		return Optional.empty();
	}

	static boolean isChargeFull(ItemStack stack) {
		if (stack.getItem() instanceof IWizardClassWeapon && stack.hasTagCompound()) {
			//noinspection ConstantConditions
			return stack.getTagCompound().hasKey(CHARGE_PROGRESS) && stack.getTagCompound().getInteger(CHARGE_PROGRESS) >= 100;
		}
		return false;
	}

	static int getChargeProgress(ItemStack stack) {
		if (stack.getItem() instanceof IWizardClassWeapon && stack.hasTagCompound()) {
			//noinspection ConstantConditions
			return stack.getTagCompound().hasKey(CHARGE_PROGRESS) ? stack.getTagCompound().getInteger(CHARGE_PROGRESS) : 0;
		}
		return 0;
	}

	static void addChargeProgress(ItemStack stack, int amount) {
		int progress = getChargeProgress(stack);

		if (stack.getItem() instanceof IWizardClassWeapon) {
			NBTTagCompound compound = stack.getTagCompound();
			if (compound == null) { compound = new NBTTagCompound(); }

			compound.setInteger(CHARGE_PROGRESS, Math.min(progress + amount, 100));
			stack.setTagCompound(compound);
		}
	}

	static  void resetChargeProgress(ItemStack stack) {
		if (stack.getItem() instanceof IWizardClassWeapon && stack.hasTagCompound()) {
			NBTTagCompound compound = stack.getTagCompound();
			if (compound == null) { compound = new NBTTagCompound(); }

			compound.setInteger(CHARGE_PROGRESS, 0);
			stack.setTagCompound(compound);
		}
	}

}
