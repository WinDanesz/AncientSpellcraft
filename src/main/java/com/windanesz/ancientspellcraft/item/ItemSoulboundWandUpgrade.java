package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftTabs;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ItemWand;
import electroblob.wizardry.item.ItemWandUpgrade;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemSoulboundWandUpgrade extends ItemWandUpgrade {

	private static final IStoredVariable<NBTTagCompound> STORED_WANDS = IStoredVariable.StoredVariable.ofNBT("soulbound_wands", Persistence.ALWAYS);

	public ItemSoulboundWandUpgrade() {
		super();
		this.setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT);
		WizardData.registerStoredVariables(STORED_WANDS);
	}

	public static void init() {
		WandHelper.registerSpecialUpgrade(AncientSpellcraftItems.soulbound_upgrade, "soulbound_upgrade");
	}

	/**
	 * Gets all the stored wands from WizardData and gives it back to the player, then purges these from WizardData
	 * @param player
	 */
	public static void restoreStoredWandsToInventory(EntityPlayer player) {
		HashMap<Integer, ItemStack> wandMap = getStoredWands(player);

		if (!wandMap.isEmpty()) {
			for (ItemStack wand : wandMap.values()) {
				if (!player.addItemStackToInventory(wand)) {
					player.dropItem(wand, false);
				}
			}
		}

		WizardData data = WizardData.get(player);
		if (data != null) {
			data.setVariable(STORED_WANDS, new NBTTagCompound());
		}
	}

	private static HashMap<Integer, ItemStack> getStoredWands(EntityPlayer player) {
		HashMap<Integer, ItemStack> wandMap = new HashMap<>();

		WizardData data = WizardData.get(player);
		if (data != null) {
			NBTTagCompound compound = data.getVariable(STORED_WANDS);

			if (compound != null) {

				// filling the wandMap with entries
				for (String key : compound.getKeySet()) {

					if (isNumeric(key) && compound.hasKey(key)) {
						ItemStack currWand = new ItemStack(compound.getCompoundTag(key));
						wandMap.put(Integer.valueOf(key), currWand.copy());
					}
				}
			}
		}

		return wandMap;
	}

	/**
	 * Should be called when players die to scan their inventory for soulbound wand and save these to WizardData.
	 * Serializes all wands which has the soulbound upgrade in the player's inventory into nbt and saves it to WizardData.
	 * This also removes the stacks from the inventory, otherwise they would be dropped when the player dies (leading to item duplication).
	 *
	 * @param player the player
	 */
	public static void storeSoulboundWands(EntityPlayer player) {
		List<ItemStack> itemList = new ArrayList<>();

		WizardData data = WizardData.get(player);
		if (data != null) {

			for (ItemStack stack : player.inventory.mainInventory) {
				if (stack.getItem() instanceof ItemWand || stack.getItem() instanceof IWizardClassWeapon) {
					boolean hasUpgrade = WandHelper.getUpgradeLevel(stack, AncientSpellcraftItems.soulbound_upgrade) != 0;
					if (hasUpgrade) {
						itemList.add(stack.copy());
						stack.setCount(0);
					}
				}
			}

			for (ItemStack stack : player.inventory.offHandInventory) {
				if (stack.getItem() instanceof ItemWand || stack.getItem() instanceof IWizardClassWeapon) {
					boolean hasUpgrade = WandHelper.getUpgradeLevel(stack, AncientSpellcraftItems.soulbound_upgrade) != 0;
					if (hasUpgrade) {
						itemList.add(stack.copy());
						stack.setCount(0);
					}
				}
			}
		}

		if (!itemList.isEmpty()) {
			// We can just create a new object here because there is no way a player can die twice without getting back the soulbound items first.
			NBTTagCompound newStoredWandData = new NBTTagCompound();

			for (int i = 0; i < itemList.size(); i++) {

				ItemStack currentWand = itemList.get(i);
				NBTTagCompound currentWandCompound = new NBTTagCompound();
				currentWand.writeToNBT(currentWandCompound);
				newStoredWandData.setTag(String.valueOf(i), currentWandCompound);
			}

			data.setVariable(STORED_WANDS, newStoredWandData);
			data.sync();
		}
	}

	private static boolean isNumeric(String string) {
		return string.matches("\\d+");
	}

}
