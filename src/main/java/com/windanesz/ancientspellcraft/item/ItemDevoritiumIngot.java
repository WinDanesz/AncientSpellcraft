package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.material.IDevoritium;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemDevoritiumIngot extends Item implements IDevoritium {

	public ItemDevoritiumIngot() {
		setMaxDamage(0);
		setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		onUpdateDelegate(stack, worldIn, entityIn, itemSlot, isSelected);
	}
}
