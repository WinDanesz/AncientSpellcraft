package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftTabs;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.registry.WizardryRecipes;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;

public abstract class ItemASWizardArmour extends ItemWizardArmour {

	public ItemASWizardArmour(ItemArmor.ArmorMaterial material, int renderIndex, EntityEquipmentSlot armourType, Element element) {
		super(material, renderIndex, armourType, element);
		this.element = element;
		setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT);
		WizardryRecipes.addToManaFlaskCharging(this);
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
