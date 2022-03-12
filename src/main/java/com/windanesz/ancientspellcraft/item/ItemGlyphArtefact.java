package com.windanesz.ancientspellcraft.item;

import electroblob.wizardry.block.BlockThorns;
import electroblob.wizardry.item.ItemArtefact;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

public class ItemGlyphArtefact extends ItemASArtefact {

	public ItemGlyphArtefact(EnumRarity rarity, Type type) {
		super(rarity, type);
	}

	public static boolean isArtefactActive(EntityPlayer player, Item artefact) {
		return ItemArtefact.isArtefactActive(player, artefact);
	}

	public static class ItemGlyphViality extends ItemGlyphArtefact implements ITickableArtefact {

		public ItemGlyphViality(EnumRarity rarity, Type type) {
			super(rarity, type);
		}

		@Override
		public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
			if (player.ticksExisted % 140 == 0 && player.getHealth() < player.getMaxHealth()) {
				player.heal(0.5f);
			}
		}
	}

	public static class ItemGlyphWarden extends ItemGlyphArtefact implements ITickableArtefact {

		public ItemGlyphWarden(EnumRarity rarity, Type type) {
			super(rarity, type);
		}

		@Override
		public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
			if (player.ticksExisted % 20 == 0) {
				if (player.world.getBlockState(player.getPosition().up()).getBlock() instanceof BlockThorns) {
					player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 30, 0));
				}
			}
		}
	}

}
