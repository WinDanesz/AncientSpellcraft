package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.Settings;
import electroblob.wizardry.item.ItemScroll;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.registry.WizardrySounds;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWrittenBook;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.Arrays;

public class ItemDuplicationScroll extends ItemRareScroll {

	public ItemDuplicationScroll() {
		super();
	}

	/**
	 * Called when the equipped item is right clicked.
	 */
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		EnumHand otherHand = hand == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
		ItemStack otherStack = player.getHeldItem(otherHand);
		Item otherItem = otherStack.getItem();

		if (otherItem instanceof ItemRitualBook || otherItem instanceof ItemEnchantedBook || otherItem instanceof ItemSpellBook
				|| otherItem instanceof ItemScroll || otherItem instanceof ItemRareScroll || otherItem instanceof ItemWrittenBook
				|| Arrays.asList(Settings.generalSettings.duplication_scroll_additional_items).contains(otherItem.getRegistryName().toString())) {
			ItemStack newStack = otherStack.copy();
			newStack.setCount(1);
			if (!world.isRemote) {
				player.playSound(WizardrySounds.MISC_DISCOVER_SPELL, 1.25F, 1.0F);
				player.setHeldItem(hand, newStack);
			}
		}
		return new ActionResult<>(EnumActionResult.PASS, stack);
	}
}
