package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.Settings;
import electroblob.wizardry.Wizardry;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemAurelianCup extends ItemASArtefact {

	public ItemAurelianCup(EnumRarity rarity, Type type) {
		super(rarity, type);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced) {
		// Display the absorption duration from config
		int durationSeconds = Settings.generalSettings.aurelian_cup_absorption_duration;
		tooltip.add(I18n.format("item.ancientspellcraft:charm_aurelian_cup.tooltip", durationSeconds));

		if (!Settings.isArtefactEnabled(this)) {
			tooltip.add(Wizardry.proxy.translate("item." + Wizardry.MODID + ":generic.disabled", new Style().setColor(TextFormatting.RED)));
		}
	}
}

