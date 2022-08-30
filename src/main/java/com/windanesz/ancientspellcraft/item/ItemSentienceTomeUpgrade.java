package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASTabs;
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

public class ItemSentienceTomeUpgrade extends ItemWandUpgrade {

	public ItemSentienceTomeUpgrade() {
		super();
		this.setCreativeTab(ASTabs.ANCIENTSPELLCRAFT);
	}

	public static void init() {
		WandHelper.registerSpecialUpgrade(ASItems.sentience_upgrade, "sentience_upgrade");
	}

	private static boolean isNumeric(String string) {
		return string.matches("\\d+");
	}

}
