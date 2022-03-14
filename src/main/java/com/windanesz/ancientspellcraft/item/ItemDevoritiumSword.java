package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.material.IDevoritium;
import com.windanesz.ancientspellcraft.registry.ASTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.world.World;

public class ItemDevoritiumSword extends ItemSword implements IDevoritium {

	@SuppressWarnings("ConstantConditions")
	public ItemDevoritiumSword() {
		super(AncientSpellcraft.DEVORITIUM_TOOL_MATERIAL);
		setCreativeTab(ASTabs.ANCIENTSPELLCRAFT_GEAR);
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		hitEntityDelegate(attacker, target);
		return super.hitEntity(stack, target, attacker);
	}

	@Override
	public boolean isEnchantable(ItemStack stack) { return false; }

	@Override
	public int getItemEnchantability() { return 0; }

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		onUpdateDelegate(stack, worldIn, entityIn, itemSlot, isSelected);
	}
}



