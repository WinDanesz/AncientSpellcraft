package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.util.ASUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public class ItemEvergrowingCrystal extends ItemDailyArtefact {

	public ItemEvergrowingCrystal(EnumRarity rarity) {
		super(rarity);
	}

	@Override
	public void performAction(EntityPlayer player) {
		if (!player.world.isRemote) {
			ItemStack stack;
			float i = AncientSpellcraft.rand.nextFloat();
			if (i <= 0.1) {
				stack = new ItemStack(AncientSpellcraftItems.astral_diamond_shard, ASUtils.randIntBetween(1, 3));
			} else if (i <= 0.15) {
				stack = new ItemStack(Items.DIAMOND, ASUtils.randIntBetween(1, 1));
			} else {
				stack = new ItemStack(Items.EMERALD, ASUtils.randIntBetween(1, 2));
			}

			if (!player.inventory.addItemStackToInventory(stack)) {
				player.dropItem(stack, false);
			}

		}
	}

	//	// ########## rewards ##########
	//	static class Emerald {
	//		Item item = Items.EMERALD;
	//		int MIN_AMOUNT = 1;
	//		int MAX_AMOUNT = 3;
	//	}
	//
	//	static class Diamond {
	//		Item item = Items.DIAMOND;
	//		int MIN_AMOUNT = 1;
	//		int MAX_AMOUNT = 3;
	//	}
	//
	//	static class AstralDiamond {
	//		Item item = WizardryItems.astral_diamond;
	//		int MIN_AMOUNT = 1;
	//		int MAX_AMOUNT = 3;
	//	}
}
