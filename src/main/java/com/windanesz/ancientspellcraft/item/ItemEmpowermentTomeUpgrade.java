package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.Settings;
import electroblob.wizardry.Wizardry;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemEmpowermentTomeUpgrade extends ItemWandUpgradeAS {

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag flag) {
		Wizardry.proxy.addMultiLineDescription(tooltip, "item." + this.getRegistryName() + ".desc", (float) Settings.generalSettings.empowerment_upgrade_potency_gain);
	}
}
