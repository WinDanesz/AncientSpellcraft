package com.windanesz.ancientspellcraft.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class ItemBeltEnchantedWhetstone extends ItemASArtefact implements ITickableArtefact {

    private static final int REPAIR_INTERVAL = 100; // Repair every 5 seconds (100 ticks)
    private static final int DURABILITY_REPAIR = 1; // Repair 1 durability point at a time

    public ItemBeltEnchantedWhetstone(EnumRarity rarity, Type type) {
        super(rarity, type);
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase entity) {
        // Only run on server side and at the specified interval
        if (!entity.world.isRemote && entity.ticksExisted % REPAIR_INTERVAL == 0) {
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;                // Check both hands for a sword or axe
                ItemStack mainHand = player.getHeldItemMainhand();
                ItemStack offHand = player.getHeldItemOffhand();
                
                // Try to repair the main hand first, then off hand
                if (!mainHand.isEmpty() && (mainHand.getItem() instanceof ItemSword || mainHand.getItem() instanceof ItemAxe)) {
                    repairTool(mainHand);
                } else if (!offHand.isEmpty() && (offHand.getItem() instanceof ItemSword || offHand.getItem() instanceof ItemAxe)) {
                    repairTool(offHand);
                }
            }
        }
    }    /**
     * Helper method to repair a single durability point on a tool (sword or axe) if it's damaged
     *
     * @param tool The tool ItemStack to repair
     */
    private void repairTool(ItemStack tool) {
        // If the tool is damaged and can be damaged (has durability)
        if (tool.isItemDamaged() && tool.getItem().isDamageable()) {
            // Repair the tool by one point
            tool.setItemDamage(tool.getItemDamage() - DURABILITY_REPAIR);

            // Ensure damage doesn't go below 0
            if (tool.getItemDamage() < 0) {
                tool.setItemDamage(0);
            }
        }
    }

}
