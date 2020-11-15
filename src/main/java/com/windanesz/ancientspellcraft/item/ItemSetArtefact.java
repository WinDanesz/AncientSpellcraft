package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import electroblob.wizardry.item.ItemArtefact;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ItemSetArtefact extends ItemASArtefact {

	public ArrayList<String> allArtefactSetItems;
	public int fullSetCount;
	public String setName;
	public String modid;

	public ItemSetArtefact(EnumRarity rarity, Type type, String setName, int fullSetCount, ArrayList<String> setItems) {
		super(rarity, type);
		allArtefactSetItems = setItems;
		this.fullSetCount = fullSetCount;
		this.setName = setName;
		this.modid = AncientSpellcraft.MODID;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag advanced) {
		super.addInformation(stack, worldIn, tooltip, advanced);
		EntityPlayer player = net.minecraft.client.Minecraft.getMinecraft().player; // Minecraft is client-side only
		if (player == null) { return; }

		List<ItemArtefact> artefacts = getActiveArtefacts(player);
		List<String> activeParts = getActiveSetPartList((ItemSetArtefact) stack.getItem(), artefacts);
		int activePartCount = activeParts.size();

		String localizedSetName = I18n.format("set." + modid + ":" + setName);

		// e.g. "Jewels of Power (2 of 3)"
		tooltip.add("\u00A76" + I18n.format("set." + modid + ":set_name_with_count", localizedSetName, activePartCount, fullSetCount));

		if (activePartCount > 1) { // only print out set bonus if at least 1 part is active

			// e.g. "Artefact Set Bonus: +5% Potency"
			tooltip.add("\u00A7d" + I18n.format("set." + modid + ":set_bonus", getSetBonusString(activePartCount)));

			// Advanced tooltips list all the active artefact item names
			if (advanced.isAdvanced()) {
				for (String artefact : activeParts) {
					// e.g. "Ring of Power"
					tooltip.add("\u00A71" + I18n.format("item." + artefact + ".name"));
				}
			}
		}
	}

	public String getSetBonusString(int activePartCount) {
		return I18n.format("set." + modid + ":" + setName + ".bonus", ((activePartCount - 1) * 5));

	}

	public static ArrayList<String> getAllArtefactSetItems(ItemSetArtefact setArtefact) {
		return setArtefact.allArtefactSetItems;
	}

	public static List<String> getActiveSetPartList(ItemSetArtefact setArtefact, List<ItemArtefact> activeArtefactList) {
		List<String> returnStringList = new ArrayList<>();
		int activecount = activeArtefactList.size();

		for (String setItem : setArtefact.allArtefactSetItems) {

			for (ItemArtefact activeArtefact : activeArtefactList) {
				String setItemRegName = setArtefact.modid + ":" + setItem;
				if (activeArtefact.getRegistryName().toString().equals(setItemRegName)) {
					returnStringList.add(activeArtefact.getRegistryName().toString());
				}
			}
		}

		return returnStringList;
	}

	public static int getFullSetCount(ItemSetArtefact setArtefact) {
		return setArtefact.fullSetCount;
	}

	public static boolean isSetFull(ItemSetArtefact setArtefact, int activeParts) {
		return getFullSetCount(setArtefact) == activeParts;
	}

	public static String getSetName(ItemSetArtefact setArtefact) {
		return setArtefact.setName;
	}
}
