package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.util.NBTExtras;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemWhisperingVeil extends ItemASArtefact {
	
	// Store the UUIDs of wizards that have been used today
	public static final IStoredVariable<List<UUID>> USED_WIZARDS_TODAY = new IStoredVariable.StoredVariable<>("whispering_veil_used_wizards",
			s -> NBTExtras.listToNBT(s, NBTUtil::createUUIDTag),
			(NBTTagList t) -> new ArrayList<>(NBTExtras.NBTToList(t, NBTUtil::getUUIDFromTag)),
			Persistence.ALWAYS);
	
	// Store the UUIDs of villagers that have been used today
	public static final IStoredVariable<List<UUID>> USED_VILLAGERS_TODAY = new IStoredVariable.StoredVariable<>("whispering_veil_used_villagers",
			s -> NBTExtras.listToNBT(s, NBTUtil::createUUIDTag),
			(NBTTagList t) -> new ArrayList<>(NBTExtras.NBTToList(t, NBTUtil::getUUIDFromTag)),
			Persistence.ALWAYS);
	
	static {
		WizardData.registerStoredVariables(USED_WIZARDS_TODAY, USED_VILLAGERS_TODAY);
	}
	
	public ItemWhisperingVeil(EnumRarity rarity, Type type) {
		super(rarity, type);
	}
	
	/**
	 * Checks if a wizard has been used today by this player
	 */
	public static boolean hasUsedWizardToday(EntityPlayer player, UUID wizardUUID) {
		WizardData data = WizardData.get(player);
		if (data == null) return false;
		
		List<UUID> usedWizards = data.getVariable(USED_WIZARDS_TODAY);
		return usedWizards != null && usedWizards.contains(wizardUUID);
	}
	
	/**
	 * Marks a wizard as used today by this player
	 */
	public static void markWizardAsUsedToday(EntityPlayer player, UUID wizardUUID) {
		WizardData data = WizardData.get(player);
		if (data == null) return;
		
		List<UUID> usedWizards = data.getVariable(USED_WIZARDS_TODAY);
		if (usedWizards == null) {
			usedWizards = new ArrayList<>();
		}
		
		if (!usedWizards.contains(wizardUUID)) {
			usedWizards.add(wizardUUID);
			data.setVariable(USED_WIZARDS_TODAY, usedWizards);
		}
	}
	
	/**
	 * Checks if a villager has been used today by this player
	 */
	public static boolean hasUsedVillagerToday(EntityPlayer player, UUID villagerUUID) {
		WizardData data = WizardData.get(player);
		if (data == null) return false;
		
		List<UUID> usedVillagers = data.getVariable(USED_VILLAGERS_TODAY);
		return usedVillagers != null && usedVillagers.contains(villagerUUID);
	}
	
	/**
	 * Marks a villager as used today by this player
	 */
	public static void markVillagerAsUsedToday(EntityPlayer player, UUID villagerUUID) {
		WizardData data = WizardData.get(player);
		if (data == null) return;
		
		List<UUID> usedVillagers = data.getVariable(USED_VILLAGERS_TODAY);
		if (usedVillagers == null) {
			usedVillagers = new ArrayList<>();
		}
		
		if (!usedVillagers.contains(villagerUUID)) {
			usedVillagers.add(villagerUUID);
			data.setVariable(USED_VILLAGERS_TODAY, usedVillagers);
		}
	}
	
	/**
	 * Clears the used wizards list (called daily)
	 */
	public static void clearUsedWizardsToday(EntityPlayer player) {
		WizardData data = WizardData.get(player);
		if (data != null) {
			data.setVariable(USED_WIZARDS_TODAY, null);
		}
	}
	
	/**
	 * Clears the used villagers list (called daily)
	 */
	public static void clearUsedVillagersToday(EntityPlayer player) {
		WizardData data = WizardData.get(player);
		if (data != null) {
			data.setVariable(USED_VILLAGERS_TODAY, null);
		}
	}
} 