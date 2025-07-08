package com.windanesz.ancientspellcraft.item;

import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

public class ItemInfernalStone extends ItemASArtefact implements ITickableArtefact {

    // NBT Keys
    private static final String NBT_HEAT_PROGRESS = "heatProgress";
    
    // Configuration constants
    private static final int MAX_HEAT = 100;
    private static final int HEAT_LOSS_INTERVAL = 60; // Every 3 seconds
    private static final int FIRE_ABSORPTION_INTERVAL = 3; // Every 3 ticks
    private static final int FIRE_ABSORPTION_RANGE = 3; // 3 block radius
    private static final float HEAT_PER_FIRE_BLOCK = 5.0f; // 5% heat per fire block
    private static final float HEAT_LOSS_RATE = 1.0f; // 1% heat loss every 60 ticks
    private static final float MANA_COST_REDUCTION = 0.25f; // 25% less mana cost for fire spells
    private static final float HEAT_CONSUMPTION_PER_SPELL = 10.0f; // 10% heat consumed per spell cast

    public ItemInfernalStone(EnumRarity rarity, Type type) {
        super(rarity, type);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isHeld) {
        super.onUpdate(stack, world, entity, slot, isHeld);
        
        // Only run on server side and when held
        if (!world.isRemote && isHeld && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            
            // Fire absorption when held in hand
            if (player.ticksExisted % FIRE_ABSORPTION_INTERVAL == 0) {
                // Debug: Check if method is being called
                System.out.println("Infernal Stone onUpdate - held in hand, checking for fire...");
                absorbNearbyFire(player, stack);
            }
        }
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        if (!player.world.isRemote && player instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer) player;
            
            // Heat loss over time
            if (player.ticksExisted % HEAT_LOSS_INTERVAL == 0) {
                removeHeat(itemstack, HEAT_LOSS_RATE);
            }
            
            // Fire absorption when held in hand
            if (isHeldInHand(entityPlayer, itemstack) && player.ticksExisted % FIRE_ABSORPTION_INTERVAL == 0) {
                // Debug: Check if method is being called
                if (!player.world.isRemote) {
                    System.out.println("Infernal Stone tick - held in hand, checking for fire...");
                }
                absorbNearbyFire(entityPlayer, itemstack);
            }
        }
    }

    public boolean isHeldInHand(EntityPlayer player, ItemStack stone) {
        ItemStack mainHand = player.getHeldItemMainhand();
        ItemStack offHand = player.getHeldItemOffhand();
        
        // Check if the stone is in either hand by comparing the item type
        return (!mainHand.isEmpty() && mainHand.getItem() == stone.getItem()) ||
               (!offHand.isEmpty() && offHand.getItem() == stone.getItem());
    }

    public void absorbNearbyFire(EntityPlayer player, ItemStack stone) {
        BlockPos playerPos = player.getPosition();
        boolean foundFire = false;
        
        // Check in a 3x3x3 area around the player
        for (int x = -FIRE_ABSORPTION_RANGE; x <= FIRE_ABSORPTION_RANGE; x++) {
            for (int y = -FIRE_ABSORPTION_RANGE; y <= FIRE_ABSORPTION_RANGE; y++) {
                for (int z = -FIRE_ABSORPTION_RANGE; z <= FIRE_ABSORPTION_RANGE; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    
                    if (player.world.getBlockState(pos).getBlock() == Blocks.FIRE) {
                        // Remove the fire block
                        player.world.setBlockToAir(pos);
                        
                        // Add heat to the stone
                        addHeat(stone, HEAT_PER_FIRE_BLOCK);
                        foundFire = true;
                        
                        // Debug message
                        if (!player.world.isRemote) {
                            System.out.println("Infernal Stone absorbed fire at " + pos + ", heat: " + getHeatPercentage(stone) + "%");
                        }
                        
                        break; // Only absorb one fire block per tick
                    }
                }
                if (foundFire) break;
            }
            if (foundFire) break;
        }
    }

    /**
     * Adds heat to the stone
     * @param stack The item stack
     * @param amount The amount of heat to add
     */
    public static void addHeat(ItemStack stack, float amount) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        
        int currentHeat = stack.getTagCompound().getInteger(NBT_HEAT_PROGRESS);
        int newHeat = Math.min(MAX_HEAT, currentHeat + (int) amount);
        stack.getTagCompound().setInteger(NBT_HEAT_PROGRESS, newHeat);
    }

    /**
     * Removes heat from the stone
     * @param stack The item stack
     * @param amount The amount of heat to remove
     */
    public static void removeHeat(ItemStack stack, float amount) {
        if (!stack.hasTagCompound()) {
            return;
        }
        
        int currentHeat = stack.getTagCompound().getInteger(NBT_HEAT_PROGRESS);
        int newHeat = Math.max(0, currentHeat - (int) amount);
        stack.getTagCompound().setInteger(NBT_HEAT_PROGRESS, newHeat);
    }

    /**
     * Gets the current heat percentage of the stone
     * @param stack The item stack
     * @return Heat percentage (0-100)
     */
    public static int getHeatPercentage(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return 0;
        }
        return stack.getTagCompound().getInteger(NBT_HEAT_PROGRESS);
    }

    /**
     * Checks if the stone is hot enough to provide bonuses
     * @param stack The item stack
     * @return True if the stone has heat
     */
    public static boolean isHot(ItemStack stack) {
        return getHeatPercentage(stack) > 0;
    }

    /**
     * Consumes heat when casting a fire spell
     * @param stack The item stack
     */
    public static void consumeHeatForSpell(ItemStack stack) {
        removeHeat(stack, HEAT_CONSUMPTION_PER_SPELL);
    }

    /**
     * Applies fire spell bonuses based on heat
     * @param modifiers The spell modifiers
     * @param stack The item stack
     */
    public static void applyFireSpellBonuses(SpellModifiers modifiers, ItemStack stack) {
        if (isHot(stack)) {
            // Reduce mana cost by 25%
            float currentCost = modifiers.get(SpellModifiers.COST);
            modifiers.set(SpellModifiers.COST, currentCost * (1.0f - MANA_COST_REDUCTION), false);
        }
    }

    /**
     * Gets the burn time for furnace fuel (only when hot)
     * @param itemStack The item stack
     * @return Burn time in ticks, or 0 if not hot enough
     */
    @Override
    public int getItemBurnTime(ItemStack itemStack) {
        // Only provide fuel when the stone is hot (at least 50% heat)
        if (getHeatPercentage(itemStack) >= 50) {
            // Return a burn time equivalent to 10 coal (1600 ticks)
            return 8000;
        }
        return 0; // Not hot enough to use as fuel
    }

    /**
     * Checks if this item can be used as furnace fuel
     * @param itemStack The item stack
     * @return True if the stone is hot enough to use as fuel
     */
    public boolean isFurnaceFuel(ItemStack itemStack) {
        return getHeatPercentage(itemStack) >= 1;
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        // Only return true if the item is hot enough to be used as fuel
        return isFurnaceFuel(stack);
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        // Return a copy of the item with the same NBT, simulating infinite fuel
        if (!isFurnaceFuel(stack)) {
            return ItemStack.EMPTY;
        }
        ItemStack copy = stack.copy();
        // Optionally, you could reduce heat here if you want burning to consume heat
        // removeHeat(copy, HEAT_CONSUMPTION_PER_SPELL); // Uncomment if desired
        return copy;
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        
        int heatPercentage = getHeatPercentage(stack);
        
        // Simple percentage display
        String heatColor;
        if (heatPercentage >= 75) {
            heatColor = TextFormatting.RED.toString();
        } else if (heatPercentage >= 50) {
            heatColor = TextFormatting.GOLD.toString();
        } else if (heatPercentage >= 25) {
            heatColor = TextFormatting.YELLOW.toString();
        } else {
            heatColor = TextFormatting.GRAY.toString();
        }
        
        tooltip.add(TextFormatting.GRAY + "Heat: " + heatColor + heatPercentage + "%");
        
        // Show furnace fuel status
        if (heatPercentage >= 50) {
            tooltip.add(TextFormatting.GREEN + "Can be used as furnace fuel");
        }
    }
} 