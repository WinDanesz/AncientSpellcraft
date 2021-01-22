package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.material.IDevoritium;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBlockDevoritiumMaterial extends ItemBlock implements IDevoritium {

	public ItemBlockDevoritiumMaterial(Block block) {
		super(block);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		onUpdateDelegate(stack, worldIn, entityIn, itemSlot, isSelected);
	}
}
