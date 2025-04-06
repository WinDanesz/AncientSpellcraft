package com.windanesz.ancientspellcraft.item;

import electroblob.wizardry.entity.living.ISummonedCreature;
import electroblob.wizardry.util.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ItemCircletOfCircling extends ItemASArtefact implements ITickableArtefact {
   private static final double MOVE_THRESHOLD = 0.5D;
   private static final double RADIUS = 3.0D;

   public ItemCircletOfCircling(EnumRarity rarity, Type type) {
      super(rarity, type);
   }

   @Override
   public void onWornTick(ItemStack stack, EntityLivingBase entity) {
      if (entity instanceof EntityPlayer && entity.ticksExisted % 5 == 0) {
         EntityPlayer player = (EntityPlayer) entity;
         List<EntityLiving> minions = this.getMinions(player);
         this.arrangeMinionsInCircle(player, minions);
      }
   }

   private List<EntityLiving> getMinions(EntityPlayer player) {
      List<Entity> entities = EntityUtils.getEntitiesWithinRadius(20.0D, player.posX, player.posY, player.posZ, player.world, Entity.class);
      return entities.stream()
         .filter(entity -> entity instanceof ISummonedCreature && entity instanceof EntityLivingBase && (((EntityLivingBase) entity)).getRevengeTarget() == null)
         .map(entity -> (EntityLiving) entity)
         .sorted(Comparator.comparingInt(Entity::getEntityId))
         .collect(Collectors.toList());
   }

   private void removeAIWander(EntityLiving minion) {
      minion.tasks.taskEntries.removeIf(task -> task.action instanceof EntityAIWander);
   }

   private void arrangeMinionsInCircle(EntityPlayer player, List<EntityLiving> minions) {
      int totalMinions = minions.size();
      double angleIncrement = 2 * Math.PI / totalMinions;

      for (int i = 0; i < totalMinions; ++i) {
         EntityLiving minion = minions.get(i);
         this.removeAIWander(minion);
         double targetX = player.posX + RADIUS * Math.cos(i * angleIncrement);
         double targetZ = player.posZ + RADIUS * Math.sin(i * angleIncrement);
         if (minion.getDistance(targetX, minion.posY, targetZ) > MOVE_THRESHOLD) {
            minion.getNavigator().tryMoveToXYZ(targetX, minion.posY, targetZ, 1.2D);
         }
      }
   }
}
