package com.windanesz.ancientspellcraft.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.windanesz.ancientspellcraft.Settings;
import electroblob.wizardry.Wizardry;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemAmuletOfCelerity extends ItemASArtefact implements ITickableArtefact, IBauble {

	private static final UUID SPEED_MODIFIER_UUID = UUID.fromString("c8b6c8a8-4a4a-4b8b-8b8b-8b8b8b8b8b8b");
	private static final String SPEED_MODIFIER_NAME = "Amulet of Celerity Speed";

	public ItemAmuletOfCelerity(EnumRarity rarity, Type type) {
		super(rarity, type);
	}

	@Override
	public BaubleType getBaubleType(ItemStack itemstack) {
		return BaubleType.AMULET;
	}

	@Override
	public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
		if (!player.world.isRemote) {
			IAttributeInstance speedAttribute = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
			if (speedAttribute != null) {
				AttributeModifier modifier = new AttributeModifier(SPEED_MODIFIER_UUID, SPEED_MODIFIER_NAME,
					Settings.generalSettings.amulet_of_celerity_speed_bonus, 1);
				if (!speedAttribute.hasModifier(modifier)) {
					speedAttribute.applyModifier(modifier);
				}
			}
		}
	}

	@Override
	public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
		if (!player.world.isRemote) {
			IAttributeInstance speedAttribute = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
			if (speedAttribute != null) {
				speedAttribute.removeModifier(SPEED_MODIFIER_UUID);
			}
		}
	}

	@Override
	public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
		// Attribute modifiers are persistent, so we don't need to do anything here
		// The modifier is applied once in onEquipped and removed in onUnequipped
	}

	@Override
	public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
		return true;
	}

	@Override
	public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) {
		return true;
	}

	@Override
	public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced) {
		// Calculate and display the speed bonus percentage
		int percentage = (int) (Settings.generalSettings.amulet_of_celerity_speed_bonus * 100);
		tooltip.add(I18n.format("item.ancientspellcraft:amulet_celerity.tooltip", percentage));

		if (!Settings.isArtefactEnabled(this)) {
			tooltip.add(Wizardry.proxy.translate("item." + Wizardry.MODID + ":generic.disabled", new Style().setColor(TextFormatting.RED)));
		}
	}
}
