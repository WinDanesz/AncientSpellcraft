package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.item.ItemWandUpgrade;
import electroblob.wizardry.registry.WizardryItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class ItemBeltScrollHolder extends AbstractItemArtefactWithSlots implements ITickableArtefact {

	public ItemBeltScrollHolder(EnumRarity rarity, Type type) {
		super(rarity, type, 1, 1, true);
	}

	public static ItemStack getScroll(ItemStack stack) {
		return AbstractItemArtefactWithSlots.getItemForSlot(stack, 0);
	}

	@Override
	public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
		if (!player.world.isRemote && player.world.getTotalWorldTime() % 50L == 0L && getScroll(itemstack).getItem() == WizardryItems.condenser_upgrade) {
			if (player.getHeldItemMainhand().getItem() instanceof IManaStoringItem && !((IManaStoringItem) player.getHeldItemMainhand().getItem()).isManaFull(player.getHeldItemMainhand())) {
				((IManaStoringItem) player.getHeldItemMainhand().getItem()).rechargeMana(player.getHeldItemMainhand(), 1);
			}
			if (player.getHeldItemOffhand().getItem() instanceof IManaStoringItem && !((IManaStoringItem) player.getHeldItemOffhand().getItem()).isManaFull(player.getHeldItemMainhand())) {
				((IManaStoringItem) player.getHeldItemOffhand().getItem()).rechargeMana(player.getHeldItemMainhand(), 1);
			}
		}
	}

	@Override
	public boolean isItemValid(Item item) {
		List<Item> itemList = new ArrayList<>();
		for (String itemName : Settings.generalSettings.golden_scroll_holder_additional_items) {
			if (ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName))) {
				itemList.add(ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName)));
			}
		}

		return (item instanceof ItemWandUpgrade && item != ASItems.soulbound_upgrade && item != WizardryItems.melee_upgrade && item != WizardryItems.storage_upgrade && item != WizardryItems.siphon_upgrade && item != WizardryItems.attunement_upgrade) || itemList.contains(item.getRegistryName().toString());
	}
}
