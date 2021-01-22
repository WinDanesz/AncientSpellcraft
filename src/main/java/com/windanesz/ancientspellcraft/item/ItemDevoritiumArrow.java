package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.entity.projectile.EntityDevoritiumArrow;
import com.windanesz.ancientspellcraft.material.IDevoritium;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemDevoritiumArrow extends ItemArrow implements IDevoritium {

	@Override
	public EntityArrow createArrow(World worldIn, ItemStack stack, EntityLivingBase shooter) {
		EntityDevoritiumArrow entityDevoritiumArrow = new EntityDevoritiumArrow(worldIn, shooter);
		return entityDevoritiumArrow;
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		onUpdateDelegate(stack, worldIn, entityIn, itemSlot, isSelected);
	}
}
