package com.windanesz.ancientspellcraft.item;

import baubles.api.IBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * Example artefact showing how to use the new generic IBauble implementation.
 * Simply implement IBauble and override the methods you need.
 */
public class ItemExampleArtefact extends ItemASArtefact implements IBauble {

    public ItemExampleArtefact(EnumRarity rarity, Type type) {
        super(rarity, type);
    }

    @Override
    public baubles.api.BaubleType getBaubleType(ItemStack itemstack) {
        return baubles.api.BaubleType.CHARM;
    }

    @Override
    public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
        if (player instanceof EntityPlayer && !player.world.isRemote) {
            EntityPlayer entityPlayer = (EntityPlayer) player;
            entityPlayer.sendMessage(new TextComponentTranslation("item.ancientspellcraft.example_artefact.equipped"));
        }
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        // This method is called every tick while the bauble is worn
        if (player instanceof EntityPlayer && player.ticksExisted % 100 == 0 && !player.world.isRemote) {
            // Do something every 5 seconds (100 ticks)
            EntityPlayer entityPlayer = (EntityPlayer) player;
            entityPlayer.sendMessage(new TextComponentTranslation("item.ancientspellcraft.example_artefact.tick"));
        }
    }

    @Override
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
        if (player instanceof EntityPlayer && !player.world.isRemote) {
            EntityPlayer entityPlayer = (EntityPlayer) player;
            entityPlayer.sendMessage(new TextComponentTranslation("item.ancientspellcraft.example_artefact.unequipped"));
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