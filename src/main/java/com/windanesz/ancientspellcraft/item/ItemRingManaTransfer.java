package com.windanesz.ancientspellcraft.item;

import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.registry.WizardryItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class ItemRingManaTransfer extends ItemASArtefact implements ITickableArtefact {

	private static final int MANA_THRESHOLD = 200;
	private static final int CHECK_INTERVAL = 100; // 5 seconds (20 ticks per second * 5)
	private static final float FLASK_CONSUMPTION_CHANCE = 0.2f; // 20% chance

	public ItemRingManaTransfer(EnumRarity rarity, Type type) {
		super(rarity, type);
	}

	@Override
	public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
		if (!(player instanceof EntityPlayer) || player.world.isRemote) {
			return;
		}

		EntityPlayer entityPlayer = (EntityPlayer) player;

		// Check every 5 seconds
		if (entityPlayer.ticksExisted % CHECK_INTERVAL != 0) {
			return;
		}

		// Check if player has a wand in hand with low mana
		ItemStack mainHand = entityPlayer.getHeldItemMainhand();
		ItemStack offHand = entityPlayer.getHeldItemOffhand();

		// Check main hand first, then off hand
		if (shouldCheckWand(mainHand)) {
			checkAndConsumeFlask(entityPlayer, mainHand, EnumHand.MAIN_HAND);
		} else if (shouldCheckWand(offHand)) {
			checkAndConsumeFlask(entityPlayer, offHand, EnumHand.OFF_HAND);
		}
	}

	/**
	 * Checks if the given item stack is a wand that should be checked for low mana
	 */
	private boolean shouldCheckWand(ItemStack stack) {
		return !stack.isEmpty() && 
			   stack.getItem() instanceof IManaStoringItem && 
			   ((IManaStoringItem) stack.getItem()).getMana(stack) < MANA_THRESHOLD;
	}

	/**
	 * Checks if player has mana flasks and consumes one to restore mana if conditions are met
	 */
	private void checkAndConsumeFlask(EntityPlayer player, ItemStack wandStack, EnumHand hand) {
		// 20% chance to trigger the effect
		if (player.world.rand.nextFloat() >= FLASK_CONSUMPTION_CHANCE) {
			return;
		}

		// Check if player has mana flasks in inventory
		ItemStack flaskToUse = findBestManaFlask(player);
		if (flaskToUse != null) {
			// Consume one flask
			flaskToUse.shrink(1);
			
			// Restore mana to the wand based on flask size
			IManaStoringItem manaItem = (IManaStoringItem) wandStack.getItem();
			int currentMana = manaItem.getMana(wandStack);
			int manaToRestore = getFlaskManaCapacity(flaskToUse);
			int newMana = Math.min(manaItem.getManaCapacity(wandStack), currentMana + manaToRestore);
			manaItem.setMana(wandStack, newMana);
			
			// Update the item in the player's hand
			if (hand == EnumHand.MAIN_HAND) {
				player.setHeldItem(EnumHand.MAIN_HAND, wandStack);
			} else {
				player.setHeldItem(EnumHand.OFF_HAND, wandStack);
			}
		}
	}

	/**
	 * Finds the best mana flask to use based on priority (LARGE > MEDIUM > SMALL)
	 */
	private ItemStack findBestManaFlask(EntityPlayer player) {
		// Check for large mana flasks first
		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack stack = player.inventory.getStackInSlot(i);
			if (!stack.isEmpty() && stack.getItem() == WizardryItems.large_mana_flask) {
				return stack;
			}
		}
		
		// Check for medium mana flasks
		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack stack = player.inventory.getStackInSlot(i);
			if (!stack.isEmpty() && stack.getItem() == WizardryItems.medium_mana_flask) {
				return stack;
			}
		}
		
		// Check for small mana flasks
		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack stack = player.inventory.getStackInSlot(i);
			if (!stack.isEmpty() && stack.getItem() == WizardryItems.small_mana_flask) {
				return stack;
			}
		}
		
		return null;
	}

	/**
	 * Gets the mana capacity of the given mana flask
	 */
	private int getFlaskManaCapacity(ItemStack flaskStack) {
		if (flaskStack.getItem() == WizardryItems.large_mana_flask) {
			return 1400;
		} else if (flaskStack.getItem() == WizardryItems.medium_mana_flask) {
			return 350;
		} else if (flaskStack.getItem() == WizardryItems.small_mana_flask) {
			return 75;
		}
		return 0;
	}
}
