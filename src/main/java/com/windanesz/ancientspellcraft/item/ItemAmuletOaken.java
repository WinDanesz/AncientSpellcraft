package com.windanesz.ancientspellcraft.item;

import electroblob.wizardry.registry.WizardryPotions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

public class ItemAmuletOaken extends ItemASArtefact implements ITickableArtefact {

	public ItemAmuletOaken(EnumRarity rarity, Type type) {
		super(rarity, type);
	}

	public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
		if (player.ticksExisted % 10 == 0 && player.isPotionActive(WizardryPotions.oakflesh)
				&& player.world.getBiome(player.getPosition()).getRegistryName().getPath().matches(".*forest.*|.*wood.*")) {
			player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 20, 0));
		}
	}


}
