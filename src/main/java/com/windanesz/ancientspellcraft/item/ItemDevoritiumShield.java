package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.material.IDevoritium;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftTabs;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.registry.WizardryPotions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemDevoritiumShield extends ItemShield implements IDevoritium {

	public ItemDevoritiumShield() {
		super();
		setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT_GEAR);
		this.setMaxDamage(455);
	}

	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		return repair.getItem() == AncientSpellcraftItems.devoritium_ingot;
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		hitEntityDelegate(attacker, target);
		// no super call as that would damage the item
		return true;
	}

	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand handIn) {
		ItemStack itemstack = player.getHeldItem(handIn);
		player.setActiveHand(handIn);
		player.addPotionEffect(new PotionEffect(WizardryPotions.ward, 20, 0));
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
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
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
		super.onUsingTick(stack, player, count);
		player.addPotionEffect(new PotionEffect(WizardryPotions.ward, 20, 0));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced) {
		Wizardry.proxy.addMultiLineDescription(tooltip, "item." + this.getRegistryName() + ".desc");
	}

	public String getItemStackDisplayName(ItemStack stack) {
		return I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name").trim();
	}

	@Override
	public boolean isShield(ItemStack stack, @Nullable EntityLivingBase entity) {
		return true;
	}
}
