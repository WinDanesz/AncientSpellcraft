package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftTabs;
import electroblob.wizardry.Wizardry;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemRelic extends Item {

	private final EnumRarity rarity;

	public ItemRelic(String name, EnumRarity rarity) {
		this.maxStackSize = 1;
		this.setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT);
		this.rarity = rarity;

	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return rarity;
	}

	@Override
	public IRarity getForgeRarity(ItemStack stack) {
		return rarity;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced) {
		Wizardry.proxy.addMultiLineDescription(tooltip, "item." + this.getRegistryName() + ".desc");

		if (advanced.isAdvanced()) {
			// Advanced tooltips for debugging
			// current mana
			tooltip.add(TextFormatting.BLUE + "Ancient Relic");
		}
		if (isResearched(stack)) {
			tooltip.add(TextFormatting.WHITE + (TextFormatting.ITALIC + I18n.format("item.ancientspellcraft:relic_researched")));
		}
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return rarity == EnumRarity.EPIC;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	public static void setResearched(ItemStack stack) {
		if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("researched") && stack.getTagCompound().getBoolean("researched"))
			return;
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("researched", true);
		stack.setTagCompound(nbt);
	}

	public static boolean isResearched(ItemStack stack) {
		if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("researched")) {
			return stack.getTagCompound().getBoolean("researched");
		}
		return false;
	}
}
