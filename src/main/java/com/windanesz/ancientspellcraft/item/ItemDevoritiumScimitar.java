package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.material.IDevoritium;
import com.windanesz.ancientspellcraft.registry.ASTabs;
import electroblob.wizardry.Wizardry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemDevoritiumScimitar extends ItemSword implements IDevoritium {

	@SuppressWarnings("ConstantConditions")
	public ItemDevoritiumScimitar() {
		super(AncientSpellcraft.DEVORITIUM_TOOL_MATERIAL);
		setCreativeTab(ASTabs.ANCIENTSPELLCRAFT_GEAR);
		setMaxDamage(0);
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		hitEntityDelegate(attacker, target, 3, 30);
		// no super call as that would damage the item
		return true;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) { return false; }

	@Override
	public int getItemEnchantability() { return 0; }

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		onUpdateDelegate(stack, worldIn, entityIn, itemSlot, isSelected);
	}

	@Override
	/**
	 * This is actually about damaging the item, not the damage the item deals to others!
	 */
	public void setDamage(ItemStack stack, int damage) {
	}

	@Override
	public boolean isRepairable() { return false; }

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced) {
		Wizardry.proxy.addMultiLineDescription(tooltip, "item." + this.getRegistryName() + ".desc");
	}
}





