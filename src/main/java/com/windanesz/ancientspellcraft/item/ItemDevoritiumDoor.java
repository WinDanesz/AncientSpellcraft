package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.material.IDevoritium;
import com.windanesz.ancientspellcraft.registry.ASTabs;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemDevoritiumDoor extends ItemDoor implements IDevoritium {

	public ItemDevoritiumDoor(Block block) {
		super(block);
		setCreativeTab(ASTabs.ANCIENTSPELLCRAFT);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		onUpdateDelegate(stack, worldIn, entityIn, itemSlot, isSelected);
	}


}
