package com.windanesz.ancientspellcraft.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import electroblob.wizardry.item.ItemArtefact;

public class ItemClockworkHeart extends ItemManaArtefact implements IBauble {

	private static final int MANA_CAPACITY = 1500;
	private static final int MANA_COST = 500;
	/** 12 seconds of Regeneration I */
	private static final int REGEN_DURATION = 240;
	/** 15 seconds cooldown after Heart Rush activates */
	private static final int COOLDOWN_TICKS =   300;

	public ItemClockworkHeart(EnumRarity rarity, ItemArtefact.Type type) {
		super(rarity, type, MANA_CAPACITY);
	}

	@Override
	public BaubleType getBaubleType(ItemStack itemstack) {
		return BaubleType.BODY;
	}

	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase player) {
		if (player instanceof EntityPlayer && !player.world.isRemote) {
			EntityPlayer entityPlayer = (EntityPlayer) player;
			if (!entityPlayer.getCooldownTracker().hasCooldown(this)
					&& player.getHealth() <= player.getMaxHealth() * 0.33f
					&& getMana(stack) >= MANA_COST) {
				player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, REGEN_DURATION, 0));
				setMana(stack, getMana(stack) - MANA_COST);
				entityPlayer.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS);
			}
		}
	}

	@Override
	public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
		return true;
	}

	@Override
	public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) {
		return true;
	}

	@Override
	public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player) {
		return false;
	}
}
