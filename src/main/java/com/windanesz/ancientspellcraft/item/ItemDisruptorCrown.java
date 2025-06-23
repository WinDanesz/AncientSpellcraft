package com.windanesz.ancientspellcraft.item;

import electroblob.wizardry.entity.living.ISummonedCreature;
import electroblob.wizardry.entity.construct.EntityMagicConstruct;
import electroblob.wizardry.util.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.List;

public class ItemDisruptorCrown extends ItemASArtefact {
    private static final double RADIUS = 16.0D;
    private static final int COOLDOWN_TICKS = 20 * 60 * 5; // 5 minutes

    public ItemDisruptorCrown(EnumRarity rarity, Type type) {
        super(rarity, type);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote && !player.getCooldownTracker().hasCooldown(this)) {
            List<Entity> entities = EntityUtils.getEntitiesWithinRadius(RADIUS, player.posX, player.posY, player.posZ, world, Entity.class);
            for (Entity entity : entities) {
                if (entity instanceof EntityLivingBase && entity != player) {
                    // Remove all potion effects
                    ((EntityLivingBase) entity).clearActivePotions();
                }
                // Banish summons
                if (entity instanceof ISummonedCreature) {
                    entity.setDead();
                }
                // Banish constructs
                if (entity instanceof EntityMagicConstruct) {
                    entity.setDead();
                }
            }
            player.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS);
            // Optionally, send a message to the player
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
} 