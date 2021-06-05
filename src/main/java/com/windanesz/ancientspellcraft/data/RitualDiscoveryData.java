package com.windanesz.ancientspellcraft.data;

import com.windanesz.ancientspellcraft.ritual.Ritual;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.util.Location;
import electroblob.wizardry.util.NBTExtras;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for handling all ritual knowledge related methods, using WizardData as a storage for dicovered rituals.
 */
public class RitualDiscoveryData {

	private static final String RITUAL_DISCOVERY_DATA_TAG = "ritual_discovery_data";
	private static final String RITUAL_TAG = "rituals";
	// For some reason 'the diamond' doesn't work if I chain methods onto this. Type inference is weird.

	public static final IStoredVariable<NBTTagCompound> RITUAL_DISCOVERY_DATA = IStoredVariable.StoredVariable.ofNBT(RITUAL_DISCOVERY_DATA_TAG, Persistence.ALWAYS).setSynced();

	//	public static final IStoredVariable<List<Ritual>> LOCATIONS_KEY = new IStoredVariable.StoredVariable<List<Ritual>, NBTTagList>("stoneCirclePos",
	//			), Persistence.ALWAYS).setSynced();

	//	public static final IStoredVariable<List<String>> COUNTDOWN_KEY = IStoredVariable.StoredVariable.ofString("tpCountdown", Persistence.ALWAYS).setSynced();

	public static final IStoredVariable<List<Location>> fds = new IStoredVariable.StoredVariable<List<Location>, NBTTagList>("stoneCirclePos",
			s -> NBTExtras.listToNBT(s, Location::toNBT), t -> new ArrayList<>(NBTExtras.NBTToList(t, Location::fromNBT)), Persistence.ALWAYS);

	public static void init() {
		WizardData.registerStoredVariables(RITUAL_DISCOVERY_DATA);
	}

	public static boolean hasRitualBeenDiscovered(EntityPlayer player, Ritual ritual) {
		List<String> knownRituals = getKnownRituals(player);

		return knownRituals.contains(Ritual.getRegistryNameString(ritual));
	}

	public static List<String> getKnownRituals(EntityPlayer player) {
		WizardData data = WizardData.get(player);
		return getKnownRituals(data, player);
	}

	public static List<String> getKnownRituals(WizardData data, EntityPlayer player) {
		List<String> stringList = new ArrayList<>();
		if (data != null) {
			NBTTagCompound compound = data.getVariable(RITUAL_DISCOVERY_DATA);

			if (compound != null) {
				if (compound.hasKey(RITUAL_TAG)) {
					NBTTagList tagList = (NBTTagList) compound.getTag(RITUAL_TAG);
//					if (!tagList.isEmpty()) {
						for (int i = 0; i < tagList.tagCount(); i++) {
							NBTTagCompound tag = tagList.getCompoundTagAt(i);
							String s = tag.getString("r" + i);
							stringList.add(s);
						}
//					}
				}
			}
		}
		return stringList;
	}

	public static void addKnownRitual(EntityPlayer player, Ritual ritual) {
		WizardData data = WizardData.get(player);
		List<String> stringList = getKnownRituals(data, player);
		String newRitual = Ritual.getRegistryNameString(ritual);
		if (!stringList.contains(newRitual)) {

			NBTTagList ritualList = new NBTTagList();
			stringList.add(newRitual);

			for (int i = 0; i < stringList.size(); i++) {
				String s = stringList.get(i);
				if (s != null) {
					NBTTagCompound tag = new NBTTagCompound();
					tag.setString("r" + i, s);
					ritualList.appendTag(tag);
				}
			}

			NBTTagCompound comp = new NBTTagCompound();
			comp.setTag(RITUAL_TAG, ritualList);
			data.setVariable(RITUAL_DISCOVERY_DATA, comp);
			data.sync();
		}
	}

}
