package com.windanesz.ancientspellcraft.item;

import com.windanesz.wizardryutils.capability.SummonedCreatureData;
import electroblob.wizardry.entity.living.ISummonedCreature;
import electroblob.wizardry.item.ItemArtefact;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class ItemArdorCrown extends ItemASArtefact implements ITickableArtefact {
    
    // The radius around the player where minions will be affected
    private static final double EFFECT_RADIUS = 8.0D;
    
    // Effect duration in ticks (2 seconds = 40 ticks)
    private static final int EFFECT_DURATION = 40;

    public ItemArdorCrown(EnumRarity rarity, Type type) {
        super(rarity, type);
    }    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        // Only process every 10 ticks (0.5 seconds) to avoid unnecessary calculations
        if (player.world.isRemote || player.ticksExisted % 10 != 0) {
            return;
        }

        AxisAlignedBB areaOfEffect = player.getEntityBoundingBox().grow(EFFECT_RADIUS);
        List<EntityLivingBase> nearbyEntities = player.world.getEntitiesWithinAABB(
                EntityLivingBase.class, areaOfEffect);

        for (EntityLivingBase entity : nearbyEntities) {
            if (entity == player) continue;

            // Check if the entity is a summoned creature belonging to the player
            if (isValidMinion(entity, player)) {
                
                // Apply Speed II effect (amplifier 1)
                entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, EFFECT_DURATION, 1, false, false));
                
                // Apply Strength I effect (amplifier 0)
                entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, EFFECT_DURATION, 0, false, false));
            }
        }
    }

    /**
     * Checks if an entity is a valid minion belonging to the player
     */
    private boolean isValidMinion(EntityLivingBase entity, EntityLivingBase player) {
        // Check for ISummonedCreature interface
        if (entity instanceof ISummonedCreature) {
            ISummonedCreature summonedCreature = (ISummonedCreature) entity;
            return summonedCreature.getCaster() == player;
        }
        
        // Check using capability
        return SummonedCreatureData.isSummonedEntity(entity) && 
                SummonedCreatureData.get(entity).getCaster() == player;
    }    // No need for the applyAttackDamageBoost method as we're using potion effects now
}
