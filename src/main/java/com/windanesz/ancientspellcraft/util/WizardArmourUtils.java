package com.windanesz.ancientspellcraft.util;

import electroblob.wizardry.constants.Element;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.util.InventoryUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Arrays;

public final class WizardArmourUtils {

	private WizardArmourUtils() {}

	;

	/**
	 * Same as electroblob.wizardry.item.ItemWizardArmour#isWearingFullSet(), but that has private access
	 * Returns whether the given entity is wearing a full set of wizard armour of the given class and element.
	 *
	 * @param entity      The entity to query.
	 * @param element     The element to check, or null to accept any element as long as they are all the same.
	 * @param armourClass The class to check, or null to accept any class as long as they are all the same.
	 * @return True if the entity is wearing a full set of the given element and class, false otherwise.
	 * @author: Electroblob
	 */
	public static boolean isWearingFullSet(EntityLivingBase entity, @Nullable Element element, @Nullable ItemWizardArmour.ArmourClass armourClass) {
		ItemStack helmet = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		if (!(helmet.getItem() instanceof ItemWizardArmour)) { return false; }
		Element e = element == null ? ((ItemWizardArmour) helmet.getItem()).element : element;
		ItemWizardArmour.ArmourClass ac = armourClass == null ? ((ItemWizardArmour) helmet.getItem()).armourClass : armourClass;
		return Arrays.stream(InventoryUtils.ARMOUR_SLOTS)
				.allMatch(s -> entity.getItemStackFromSlot(s).getItem() instanceof ItemWizardArmour
						&& ((ItemWizardArmour) entity.getItemStackFromSlot(s).getItem()).element == e
						&& ((ItemWizardArmour) entity.getItemStackFromSlot(s).getItem()).armourClass == ac);
	}

	public static Element getFullSetElement(EntityLivingBase entity) {
		if (isWearingFullSet(entity, null, null)) {
			// if it's a full set, we can just check any of the armour pieces
			ItemStack helmet = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
			return ((ItemWizardArmour) helmet.getItem()).element;
		}
		return Element.MAGIC;
	}

	public static ItemWizardArmour.ArmourClass getArmourClass(EntityLivingBase entity) {
		if (isWearingFullSet(entity, null, null)) {
			// if it's a full set, we can just check any of the armour pieces
			ItemStack helmet = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
			return ((ItemWizardArmour) helmet.getItem()).armourClass;
		}
		return ItemWizardArmour.ArmourClass.WIZARD;
	}
}
