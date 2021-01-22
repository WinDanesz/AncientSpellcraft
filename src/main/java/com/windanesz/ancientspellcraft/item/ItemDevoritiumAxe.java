package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.material.IDevoritium;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import static com.windanesz.ancientspellcraft.constants.AWConstants.DEVORITIUM_TOOL_ATTACK_SPEED;
import static com.windanesz.ancientspellcraft.constants.AWConstants.DEVORITIUM_TOOL_DAMAGE;

public class ItemDevoritiumAxe extends ItemAxe implements IDevoritium {

	@SuppressWarnings("ConstantConditions")
	public ItemDevoritiumAxe() {
		super(AncientSpellcraft.DEVORITIUM_TOOL_MATERIAL, DEVORITIUM_TOOL_DAMAGE, DEVORITIUM_TOOL_ATTACK_SPEED);
		setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT);
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	@Override
	public int getItemEnchantability() { return 0; }

	@Override
	public boolean hasEffect(ItemStack stack) {
		return false;
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		hitEntityDelegate(attacker, target);
		return super.hitEntity(stack, target, attacker);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		onUpdateDelegate(stack, worldIn, entityIn, itemSlot, isSelected);
	}
}

