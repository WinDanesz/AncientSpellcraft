package com.windanesz.ancientspellcraft.item;

import electroblob.wizardry.constants.Element;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;

public class ItemAncientHat extends ItemASWizardArmour{

	public ItemAncientHat(ArmorMaterial material, int renderIndex, EntityEquipmentSlot armourType, Element element) {
		super(material, renderIndex, armourType, element);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return "ancientspellcraft:textures/armour/wizard_armour_ancient.png";
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name").trim();
	}

}
