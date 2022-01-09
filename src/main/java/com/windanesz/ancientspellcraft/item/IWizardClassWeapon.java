package com.windanesz.ancientspellcraft.item;

import electroblob.wizardry.item.IManaStoringItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

import java.util.Optional;

public interface IWizardClassWeapon {

	//	default ItemStack getExternalManaSource(ItemStack manaForItemStack, EntityLivingBase entity) {
	//
	//		if (entity != null) {
	//			entity.getHeldItemOffhand()
	//		}
	//
	//		Optional<ItemStack> manaStoringItem = manaStoringItemStack(manaForItemStack, otherHand, entity);
	//		return manaStoringItem.map(stack -> ((IManaStoringItem) stack.getItem()).getMana(stack)).orElse(0);
	//	}

	//	default int getMana(ItemStack weapon, EntityLivingBase entity) {
	//		Optional<ItemStack> manaStoringItem = manaStoringItemStack(weapon, otherHand, entity);
	//		return manaStoringItem.map(stack -> ((IManaStoringItem) stack.getItem()).getMana(stack)).orElse(0);
	//	}

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

}
