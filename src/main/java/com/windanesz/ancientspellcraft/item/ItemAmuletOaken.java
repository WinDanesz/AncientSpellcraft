package com.windanesz.ancientspellcraft.item;

import electroblob.wizardry.registry.WizardryPotions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

public class ItemAmuletOaken extends ItemASArtefact implements ITickableArtefact {

	public ItemAmuletOaken(EnumRarity rarity, Type type) {
		super(rarity, type);
	}

	public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        if (player.world.isRemote) {
            return;
        }
//		if (player.ticksExisted % 10 == 0 && player.isPotionActive(WizardryPotions.oakflesh)
//				&& player.world.getBiome(player.getPosition()).getRegistryName().getPath().matches(".*forest.*|.*wood.*")) {
//			player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 20, 0));
//		}
        if (player.ticksExisted % 10 == 0 && player.isPotionActive(WizardryPotions.oakflesh)) {

            Biome biome = player.world.getBiome(player.getPosition());

            // Added safe null-check for biome registry names to prevent NullPointerExceptions
            ResourceLocation registryName = Biome.REGISTRY.getNameForObject(biome);

            if (registryName != null) {
                String biomePath = registryName.getPath().toLowerCase();

                if (biomePath.contains("forest") || biomePath.contains("wood")) {
                    player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 40, 0)); // Bumped to 40 ticks so it doesn't instantly expire between ticks
                }
            }
        }
	}
}
