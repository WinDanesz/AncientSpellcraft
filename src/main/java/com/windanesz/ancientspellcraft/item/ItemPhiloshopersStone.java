package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public class ItemPhiloshopersStone extends ItemDailyArtefact {

	public ItemPhiloshopersStone(EnumRarity rarity) {
		super(rarity);
		addReadinessPropertyOverride();
	}

	@Override
	public void performAction(EntityPlayer player) {
		if (!player.world.isRemote) {

			ItemStack stack = new ItemStack(AncientSpellcraftItems.alchemical_essence, 1);
			if (!player.inventory.addItemStackToInventory(stack)) {
				player.dropItem(stack, false);
			}
		}
	}
}
