package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.registry.ASTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemArmourUpgradeMaterial extends Item {

	public ItemArmourUpgradeMaterial() {
		super();
		setMaxDamage(0);
		setCreativeTab(ASTabs.ANCIENTSPELLCRAFT);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack){
		return EnumRarity.EPIC;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack){
		return true;
	}
}
