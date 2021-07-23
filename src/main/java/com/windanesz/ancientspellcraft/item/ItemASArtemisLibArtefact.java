package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.integration.artemislib.ASArtemisLibIntegration;
import electroblob.wizardry.Wizardry;
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

public class ItemASArtemisLibArtefact extends ItemASArtefact {
	public ItemASArtemisLibArtefact(EnumRarity rarity, Type type) {
		super(rarity, type);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced) {
		Wizardry.proxy.addMultiLineDescription(tooltip, "item." + this.getRegistryName() + ".desc", Settings.generalSettings.orb_artefact_potency_bonus);

		if (!Settings.isArtefactEnabled(this)) {
			tooltip.add(Wizardry.proxy.translate("item." + Wizardry.MODID + ":generic.disabled", new Style().setColor(TextFormatting.RED)));
		} else if (!ASArtemisLibIntegration.enabled()) {
			tooltip.add(Wizardry.proxy.translate("tooltip.ancientspellcraft:missing_artemislib.disabled_item", new Style().setColor(TextFormatting.RED)));
		}

	}
}
