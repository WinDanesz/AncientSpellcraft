package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.util.ASUtils;
import com.windanesz.ancientspellcraft.util.SpellTeleporter;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.client.DrawingUtils;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.item.IWorkbenchItem;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.util.Location;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.ItemArtefact;

import javax.annotation.Nullable;
import java.util.List;

public class ItemTeleportationStone extends ItemASArtefact implements IManaStoringItem, IWorkbenchItem {

    // NBT Keys
    private static final String NBT_BOUND_LOCATION = "boundLocation";
    private static final String NBT_IS_BOUND = "isBound";
    
    // Configuration constants
    private static final int MANA_CAPACITY = 500;
    private static final int TELEPORTATION_COST = 250;
    private static final int BINDING_TIME_TICKS = 60; // 3 seconds (20 ticks per second)
    private static final int TELEPORTATION_TIME_TICKS = 80; // 2 seconds (20 ticks per second)
    
    public ItemTeleportationStone(EnumRarity rarity, Type type) {
        super(rarity, type);
        setMaxDamage(MANA_CAPACITY);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        
        if (!Settings.isArtefactEnabled(this)) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        
        // Check if player is sneaking (binding mode)
        if (player.isSneaking()) {
            return handleBindingMode(world, player, stack, hand);
        } else {
            return handleTeleportationMode(world, player, stack, hand);
        }
    }

    private ActionResult<ItemStack> handleBindingMode(World world, EntityPlayer player, ItemStack stack, EnumHand hand) {
        // Check if already bound
        if (isBound(stack)) {
            ASUtils.sendMessage(player, "item.ancientspellcraft:charm_teleportation_stone.already_bound", true);
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        
        // Start using the item for binding
        player.setActiveHand(hand);
        ASUtils.sendMessage(player, "item.ancientspellcraft:charm_teleportation_stone.binding_started", true);
        
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    private ActionResult<ItemStack> handleTeleportationMode(World world, EntityPlayer player, ItemStack stack, EnumHand hand) {
        if (!isBound(stack)) {
            ASUtils.sendMessage(player, "item.ancientspellcraft:charm_teleportation_stone.not_bound", true);
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        
        // Check mana
        if (getMana(stack) < TELEPORTATION_COST) {
            ASUtils.sendMessage(player, "item.ancientspellcraft:charm_teleportation_stone.insufficient_mana", true);
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        
        // Start using the item for teleportation
        player.setActiveHand(hand);
        ASUtils.sendMessage(player, "item.ancientspellcraft:charm_teleportation_stone.teleportation_started", true);
        
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        // Return the appropriate duration based on whether player is sneaking
        EntityPlayer player = null;
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("temp_sneaking")) {
            return stack.getTagCompound().getBoolean("temp_sneaking") ? BINDING_TIME_TICKS : TELEPORTATION_TIME_TICKS;
        }
        return TELEPORTATION_TIME_TICKS; // Default
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return SpellActions.SUMMON; // Shows the pointing animation (for testing)
    }

//    @Override
//    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
//        // Spawn particles during charging
//        if (player.world.isRemote && player instanceof EntityPlayer) {
//            spawnChargingParticles(player.world, player.getPosition());
//        }
//    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, world, entity, itemSlot, isSelected);

        if (world.isRemote && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            
            // Only spawn particles if the item is actively being used (charging)
            if (player.getActiveItemStack() == stack && player.getItemInUseCount() > 0) {
                // Random blue/cyan colors for variety
                int[] colors = {0x0066ff, 0x00ccff, 0x0099ff, 0x00ffff, 0x3366ff};
                int dustColor = colors[world.rand.nextInt(colors.length)];
                int flashColor = colors[world.rand.nextInt(colors.length)];
                
                ParticleBuilder.create(ParticleBuilder.Type.DUST).clr(dustColor).vel(0, 0.03f, 0).spin(0.8f, 0.03f).time(60).entity(entity).pos(0, 0.1f, 0).scale(1.2f).spawn(world);
                ParticleBuilder.create(ParticleBuilder.Type.DUST).clr(dustColor).vel(0, 0.03f, 0).spin(0.8f, 0.04f).time(60).entity(entity).pos(0, 0.1f, 0).scale(1.2f).spawn(world);
                ParticleBuilder.create(ParticleBuilder.Type.FLASH).face(EnumFacing.DOWN).clr(flashColor).pos(0,0.1f,0).time(20).entity(entity).scale(1.2f).spawn(world);            }
        }
    }


    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity) {
        if (!(entity instanceof EntityPlayer)) return stack;
        
        EntityPlayer player = (EntityPlayer) entity;
        
        // Determine if this was a binding or teleportation based on sneaking state
        boolean wasBinding = player.isSneaking();
        
        if (wasBinding) {
            completeBinding(world, player, stack);
        } else {
            completeTeleportation(world, player, stack);
        }
        
        return stack;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity, int timeLeft) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            ASUtils.sendMessage(player, "item.ancientspellcraft:charm_teleportation_stone.charging_cancelled", true);
        }
    }

    private void completeBinding(World world, EntityPlayer player, ItemStack stack) {
        // Check if player is still sneaking
        if (!player.isSneaking()) {
            ASUtils.sendMessage(player, "item.ancientspellcraft:charm_teleportation_stone.binding_cancelled", true);
            return;
        }
        
        // Store the current location
        Location boundLocation = new Location(player.getPosition(), player.dimension);
        setBoundLocation(stack, boundLocation);
        setBound(stack, true);
        
        // Set vanilla cooldown
        player.getCooldownTracker().setCooldown(this, 60); // 3 seconds
        
        ASUtils.sendMessage(player, "item.ancientspellcraft:charm_teleportation_stone.bound", true);
        
        // Spawn binding particles
        if (world.isRemote) {
            spawnBindingParticles(world, player.getPosition());
        }
    }
    
    private void completeTeleportation(World world, EntityPlayer player, ItemStack stack) {
        // Check if player is still not sneaking
        if (player.isSneaking()) {
            ASUtils.sendMessage(player, "item.ancientspellcraft:charm_teleportation_stone.teleportation_cancelled", true);
            return;
        }
        
        // Check mana again
        if (getMana(stack) < TELEPORTATION_COST) {
            ASUtils.sendMessage(player, "item.ancientspellcraft:charm_teleportation_stone.insufficient_mana", true);
            return;
        }
        
        Location boundLocation = getBoundLocation(stack);
        if (boundLocation == null) {
            ASUtils.sendMessage(player, "item.ancientspellcraft:charm_teleportation_stone.invalid_location", true);
            return;
        }
        
        // Check if interdimensional teleportation is allowed
        if (boundLocation.dimension != player.dimension && !ItemArtefact.isArtefactActive(player, ASItems.charm_rift_bottle)) {
            ASUtils.sendMessage(player, "item.ancientspellcraft:charm_teleportation_stone.interdimensional_not_allowed", false);
            return;
        }
        
        // Consume mana and teleport
        consumeMana(stack, TELEPORTATION_COST, player);
        SpellTeleporter.teleportEntity(boundLocation.dimension, boundLocation.pos.getX(), boundLocation.pos.getY(), boundLocation.pos.getZ(), true, player);
        
        // Set vanilla cooldown
        player.getCooldownTracker().setCooldown(this, 40); // 2 seconds
        
    }

    @SideOnly(Side.CLIENT)
    private void spawnBindingParticles(World world, BlockPos pos) {
        for (int i = 0; i < 20; i++) {
            ParticleBuilder.create(ParticleBuilder.Type.SPARKLE)
                    .pos(pos.getX() + world.rand.nextFloat(), pos.getY() + 1 + world.rand.nextFloat(), pos.getZ() + world.rand.nextFloat())
                    .clr(0x00FFFF)
                    .vel(0, 0.1f, 0)
                    .time(40)
                    .spawn(world);
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnChargingParticles(World world, BlockPos pos) {
        if (world.rand.nextInt(5) == 0) {
            ParticleBuilder.create(ParticleBuilder.Type.SPARKLE)
                    .pos(pos.getX() + world.rand.nextFloat(), pos.getY() + 1 + world.rand.nextFloat(), pos.getZ() + world.rand.nextFloat())
                    .clr(0x00FFFF)
                    .vel(0, 0.1f, 0)
                    .time(30)
                    .spawn(world);
        }
    }

    // NBT Helper methods
    private boolean isBound(ItemStack stack) {
        return getNBTBoolean(stack, NBT_IS_BOUND, false);
    }

    private void setBound(ItemStack stack, boolean bound) {
        setNBTBoolean(stack, NBT_IS_BOUND, bound);
    }

    private Location getBoundLocation(ItemStack stack) {
        if (!stack.hasTagCompound()) return null;
        
        NBTTagCompound locationTag = stack.getTagCompound().getCompoundTag(NBT_BOUND_LOCATION);
        if (locationTag.isEmpty()) return null;
        
        return Location.fromNBT(locationTag);
    }

    private void setBoundLocation(ItemStack stack, Location location) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setTag(NBT_BOUND_LOCATION, location.toNBT());
    }

    private boolean getNBTBoolean(ItemStack stack, String key, boolean defaultValue) {
        if (!stack.hasTagCompound()) return defaultValue;
        return stack.getTagCompound().getBoolean(key);
    }

    private void setNBTBoolean(ItemStack stack, String key, boolean value) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setBoolean(key, value);
    }

    private long getNBTLong(ItemStack stack, String key, long defaultValue) {
        if (!stack.hasTagCompound()) return defaultValue;
        return stack.getTagCompound().getLong(key);
    }

    private void setNBTLong(ItemStack stack, String key, long value) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setLong(key, value);
    }

    // IManaStoringItem implementation
    @Override
    public int getMana(ItemStack stack) {
        return getManaCapacity(stack) - getDamage(stack);
    }

    @Override
    public void setMana(ItemStack stack, int mana) {
        super.setDamage(stack, getManaCapacity(stack) - mana);
    }

    @Override
    public int getManaCapacity(ItemStack stack) {
        return MANA_CAPACITY;
    }

    @Override
    public boolean showManaInWorkbench(EntityPlayer player, ItemStack stack) {
        return true;
    }

    @Override
    public void consumeMana(ItemStack stack, int mana, @Nullable EntityLivingBase wielder) {
        IManaStoringItem.super.consumeMana(stack, mana, wielder);
    }

    @Override
    public void rechargeMana(ItemStack stack, int mana) {
        IManaStoringItem.super.rechargeMana(stack, mana);
    }

    @Override
    public boolean isManaFull(ItemStack stack) {
        return IManaStoringItem.super.isManaFull(stack);
    }

    @Override
    public boolean isManaEmpty(ItemStack stack) {
        return IManaStoringItem.super.isManaEmpty(stack);
    }

    @Override
    public float getFullness(ItemStack stack) {
        return IManaStoringItem.super.getFullness(stack);
    }

    // IWorkbenchItem implementation
    @Override
    public int getSpellSlotCount(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean showTooltip(ItemStack stack) {
        return true;
    }

    @Override
    public boolean onApplyButtonPressed(EntityPlayer player, Slot centre, Slot crystals, Slot upgrade, Slot[] spellBooks) {
        boolean changed = false;
        
        // Handle mana charging with crystals
        if (crystals.getStack() != ItemStack.EMPTY && !this.isManaFull(centre.getStack())) {
            int chargeDepleted = this.getManaCapacity(centre.getStack()) - this.getMana(centre.getStack());
            
            int manaPerItem = Constants.MANA_PER_CRYSTAL;
            if (crystals.getStack().getItem() == WizardryItems.crystal_shard) {
                manaPerItem = Constants.MANA_PER_SHARD;
            }
            if (crystals.getStack().getItem() == WizardryItems.grand_crystal) {
                manaPerItem = Constants.GRAND_CRYSTAL_MANA;
            }
            
            if (crystals.getStack().getCount() * manaPerItem < chargeDepleted) {
                // If there aren't enough crystals to fully charge
                this.rechargeMana(centre.getStack(), crystals.getStack().getCount() * manaPerItem);
                crystals.decrStackSize(crystals.getStack().getCount());
            } else {
                // If there are excess crystals (or just enough)
                this.setMana(centre.getStack(), this.getManaCapacity(centre.getStack()));
                crystals.decrStackSize((int) Math.ceil(((double) chargeDepleted) / manaPerItem));
            }
            
            changed = true;
        }
        
        return changed;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, net.minecraft.client.util.ITooltipFlag flagIn) {
        // Add mana information
        tooltip.add(Wizardry.proxy.translate("item." + Wizardry.MODID + ":wand.mana", 
                new Style().setColor(TextFormatting.BLUE),
                this.getMana(stack), this.getManaCapacity(stack)));
        
        // Add binding information
        if (isBound(stack)) {
            Location boundLocation = getBoundLocation(stack);
            if (boundLocation != null) {
                String dimName = "Unknown";
                DimensionType dimType = DimensionType.getById(boundLocation.dimension);
                if (dimType != null) {
                    dimName = I18n.format(dimType.getName());
                    if (!dimName.isEmpty()) {
                        dimName = dimName.substring(0, 1).toUpperCase() + dimName.substring(1);
                        dimName = dimName.replace("_", " ");
                    }
                }
                tooltip.add(TextFormatting.GREEN + "Bound to: " + 
                        boundLocation.pos.getX() + ", " + 
                        boundLocation.pos.getY() + ", " + 
                        boundLocation.pos.getZ() + 
                        " (" + dimName + ")");
            }
            tooltip.add(TextFormatting.YELLOW + "Right-click to teleport (Cost: " + TELEPORTATION_COST + ")");
        } else {
            tooltip.add(TextFormatting.YELLOW + "Sneak + Right-click to bind tw2o current location (3s charge)");
        }
        
        if (!Settings.isArtefactEnabled(this)) {
            tooltip.add(Wizardry.proxy.translate("item." + Wizardry.MODID + ":generic.disabled", 
                    new Style().setColor(TextFormatting.RED)));
        }
        
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return DrawingUtils.mix(0xff8bfe, 0x8e2ee4, (float) getDurabilityForDisplay(stack));
    }
} 