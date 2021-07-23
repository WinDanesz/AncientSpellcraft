package com.windanesz.ancientspellcraft.enchantment;

import electroblob.wizardry.enchantment.Imbuement;
import net.minecraft.enchantment.EnchantmentDamage;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

// This one is for degraded swords. The only reason this is separate is that the way vanilla is written allows me to hook
// into the damage increase for melee weapons, meaning I don't have to use events - always handy!
// based on Electroblob's EnchantmentMagicSword

public class EnchantmentDegradeSword extends EnchantmentDamage implements Imbuement {

	public EnchantmentDegradeSword() {
		super(Rarity.COMMON, 0, EntityEquipmentSlot.MAINHAND);
		// Setting this to null stops the book appearing in the creative inventory
		this.type = null;
	}

	@Override
	public boolean canApply(ItemStack stack) {
		return false;
	}

	/**
	 * Returns the maximum level that the enchantment can have.
	 */
	@Override
	public int getMaxLevel() {
		return 4;
	}

	@Override
	public float calcDamageByCreature(int level, EnumCreatureAttribute creatureType) { return (float) (1.0 / (level + 1)); }

	@Override
	public String getName() { return "enchantment." + this.getRegistryName(); }

	@Override
	public boolean isAllowedOnBooks() { return false; }

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) { return false; }
}
