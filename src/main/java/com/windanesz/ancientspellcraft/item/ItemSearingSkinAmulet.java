package com.windanesz.ancientspellcraft.item;

import electroblob.wizardry.registry.WizardryPotions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

public class ItemSearingSkinAmulet extends ItemASArtefact implements ITickableArtefact {

	public ItemSearingSkinAmulet(EnumRarity rarity, Type type) {
		super(rarity, type);
	}

	@Override
	public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
		if (player.ticksExisted % 10 == 0 && player.isPotionActive(WizardryPotions.fireskin)) {
			player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 40));
		}
	}
}
