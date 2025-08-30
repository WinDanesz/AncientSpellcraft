package com.windanesz.ancientspellcraft.tileentity;

import electroblob.wizardry.util.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.List;

/**
 * Tile entity for the wildfire flame block.
 * Wildfire flames have an orange-red color, deal fire damage, and move towards hostile entities.
 */
public class TileEntityWildfireFlame extends AbstractMagicFlameTileEntity {
      private float spreadRadius = 3.0f;
    private int movementCooldown = 0;
    private static final int MOVEMENT_COOLDOWN_MAX = 1; // Move every 1 second
    private boolean entityNearby = false;
    protected boolean canSpreadFire = false; // Fire spreading functionality is now replaced by movement
    
    @Override
    protected void spawnParticles() {
        // More dynamic particles when entity is nearby
        int particleCount = entityNearby ? 6 : 1;
        
        for (int i = 0; i < particleCount; i++) {  
            double x = pos.getX() + 0.2 + world.rand.nextDouble() * 0.6;
            double y = pos.getY() + 0.2 + world.rand.nextDouble() * 0.6;
            double z = pos.getZ() + 0.2 + world.rand.nextDouble() * 0.6;

            // Wildfire (orange-red) flame particles with more intensity
            ParticleBuilder.create(ParticleBuilder.Type.SPARKLE)
                    .pos(x, y, z)
                    .vel(0, world.rand.nextDouble() * 0.05, 0)
                    .time(10 + world.rand.nextInt(10))
                    .clr(0.9f + world.rand.nextFloat() * 0.1f, 0.2f + world.rand.nextFloat() * 0.2f, 0.0f)
                    .spawn(world);

            // Regular fire particles (more frequent than other flames)
            if (world.rand.nextInt(2) == 0) {
                world.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 
                        (world.rand.nextDouble() - 0.5) * 0.05, 
                        world.rand.nextDouble() * 0.05, 
                        (world.rand.nextDouble() - 0.5) * 0.05);
            }
            
            // Additional lava particles occasionally
            if (world.rand.nextInt(10) == 0) {
                world.spawnParticle(EnumParticleTypes.LAVA, x, y, z, 0, 0, 0);
            }
            
            // Add smoke when entities are nearby (makes it look more aggressive)
            if (entityNearby && world.rand.nextInt(5) == 0) {
                world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, 
                        x, y + 0.3, z, 
                        (world.rand.nextDouble() - 0.5) * 0.02, 
                        world.rand.nextDouble() * 0.08, 
                        (world.rand.nextDouble() - 0.5) * 0.02);
            }
        }
    }
    
    @Override
    public void update() {
        super.update();
        
        // Server-side logic for moving towards entities
        if (!world.isRemote) {
            // Check for entities and movement every tick but only move at specific intervals
            checkAndMoveTowardsTarget();
        }
    }
    
    /**
     * Checks for hostile entities and moves towards them if possible
     */
    private void checkAndMoveTowardsTarget() {
        // Decrement cooldown
        if (movementCooldown > 0) {
            movementCooldown--;
            return;
        }
        
        // Get nearby entities
        AxisAlignedBB searchArea = new AxisAlignedBB(
                pos.getX() - spreadRadius, 
                pos.getY() - 1, 
                pos.getZ() - spreadRadius, 
                pos.getX() + spreadRadius, 
                pos.getY() + 2, 
                pos.getZ() + spreadRadius);
        
        List<EntityLivingBase> nearbyEntities = world.getEntitiesWithinAABB(EntityLivingBase.class, searchArea);
        
        // Check for collision with entity
        boolean isCollidingWithEntity = false;
        for (EntityLivingBase entity : nearbyEntities) {
            AxisAlignedBB entityBB = entity.getEntityBoundingBox();
            if (entityBB.intersects(new AxisAlignedBB(pos))) {
                isCollidingWithEntity = true;
                // Don't move if colliding with an entity
                break;
            }
        }
          if (isCollidingWithEntity) {
            entityNearby = true;
            movementCooldown = MOVEMENT_COOLDOWN_MAX;
            return;
        }
        
        // Filter only hostile entities (not allies to the caster)
        EntityLivingBase caster = getCaster();
        List<EntityLivingBase> hostileEntities = EntityUtils.getEntitiesWithinRadius(
                spreadRadius, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, world, EntityLivingBase.class);
          hostileEntities.removeIf(entity -> {
            if (entity == caster) return true; // Remove the caster
            if (caster == null) return false; // If no caster, consider all entities hostile
            
            // Don't target entities immune to fire
            if (entity.isImmuneToFire()) return true;
            
            // Check entity dimensions - some mobs shouldn't be targeted (like Endermen that teleport away)
            float height = entity.height;
            float width = entity.width;
            if (height * width < 0.2f) return true; // Too small to chase
            if (height > 3.0f) return true; // Too big to chase
            
            return AllyDesignationSystem.isAllied(caster, entity); // Remove allies
        });
        
        // No movement if no hostile entities
        if (hostileEntities.isEmpty()) {
            entityNearby = false;
            movementCooldown = MOVEMENT_COOLDOWN_MAX;
            return;
        }
        
        entityNearby = true;
        
        // Find closest hostile entity
        EntityLivingBase target = hostileEntities.stream()
                .min(Comparator.comparingDouble(e -> e.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)))
                .orElse(null);
        
        if (target == null) {
            movementCooldown = MOVEMENT_COOLDOWN_MAX;
            return;
        }
        
        // Calculate direction to target
        Vec3d targetVec = new Vec3d(target.posX, target.posY, target.posZ);
        Vec3d currentVec = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        Vec3d direction = targetVec.subtract(currentVec).normalize();
          // Determine next position (only move in cardinal directions or diagonally)
        int dx = (direction.x > 0.2) ? 1 : (direction.x < -0.2) ? -1 : 0;
        int dz = (direction.z > 0.2) ? 1 : (direction.z < -0.2) ? -1 : 0;
          // Try to move to the next position
        BlockPos nextPos = pos.add(dx, 0, dz);
        
        // Try to climb one block if the target is higher
        if (target.posY > pos.getY() + 0.5) {
            BlockPos upPos = nextPos.up();
            // Check if we can climb up - needs air at the destination and a solid block to climb on
            if ((world.isAirBlock(upPos) || 
                BlockUtils.canBlockBeReplaced(world, upPos, true) ||
                world.getBlockState(upPos).getBlock() == Blocks.TALLGRASS ||
                world.getBlockState(upPos).getBlock() == Blocks.DOUBLE_PLANT) &&
                world.getBlockState(upPos.down()).isTopSolid()) {
                nextPos = upPos;
            }
        }
        // Try to go down one block if the target is lower
        else if (target.posY < pos.getY() - 0.5) {
            BlockPos downPos = nextPos.down();
            // Check if we can go down - needs air at the destination and a solid block beneath that
            if ((world.isAirBlock(downPos) || 
                BlockUtils.canBlockBeReplaced(world, downPos, true) ||
                world.getBlockState(downPos).getBlock() == Blocks.TALLGRASS ||
                world.getBlockState(downPos).getBlock() == Blocks.DOUBLE_PLANT) &&
                world.getBlockState(downPos.down()).isTopSolid()) {
                nextPos = downPos;
            }
        }
          // Move only if the target block is air or can be replaced and has a solid block beneath it
        // OR if the target is directly on top of a solid block
        boolean canPlaceAtNext = false;
        
        // Normal case - replaceable block with solid support beneath
        if ((world.isAirBlock(nextPos) || 
            BlockUtils.canBlockBeReplaced(world, nextPos, true) || 
            world.getBlockState(nextPos).getBlock() == Blocks.TALLGRASS || 
            world.getBlockState(nextPos).getBlock() == Blocks.DOUBLE_PLANT) && 
            world.getBlockState(nextPos.down()).isTopSolid()) {
            canPlaceAtNext = true;
        }
        // Special case for jumping up to a ledge - block itself is solid with air above it
        else if (world.getBlockState(nextPos).isTopSolid() && 
                 (world.isAirBlock(nextPos.up()) || BlockUtils.canBlockBeReplaced(world, nextPos.up(), true))) {
            nextPos = nextPos.up(); // Move to the block above the solid block
            canPlaceAtNext = true;
        }
        
        if (canPlaceAtNext) {
            // Store current state and tile entity data
            IBlockState currentState = world.getBlockState(pos);
            NBTTagCompound tileData = this.writeToNBT(new NBTTagCompound());// Additional safety check before moving
            if ((world.isAirBlock(nextPos) || 
                BlockUtils.canBlockBeReplaced(world, nextPos, true) || 
                world.getBlockState(nextPos).getBlock() == Blocks.TALLGRASS || 
                world.getBlockState(nextPos).getBlock() == Blocks.DOUBLE_PLANT) && 
                !world.isAirBlock(pos)) {
                
                // If the destination block isn't air, clear it before placing our flame
                if (!world.isAirBlock(nextPos)) {
                    world.setBlockToAir(nextPos);
                }
                
                // Remove this block and place at new position
                world.setBlockToAir(pos);
                world.setBlockState(nextPos, currentState);
            } else {
                // Something went wrong, don't move
                movementCooldown = MOVEMENT_COOLDOWN_MAX;
                return;
            }
            
            // Get the new tile entity and restore data
            TileEntity newTileEntity = world.getTileEntity(nextPos);
            if (newTileEntity instanceof TileEntityWildfireFlame) {
                ((TileEntityWildfireFlame) newTileEntity).readFromNBT(tileData);
                // Reset cooldown shorter when actively chasing
                ((TileEntityWildfireFlame) newTileEntity).movementCooldown = MOVEMENT_COOLDOWN_MAX / 2;            }
        }
        
        // Reset cooldown
        movementCooldown = MOVEMENT_COOLDOWN_MAX;
    }
    
    /**
     * Sets the spread/search radius for the wildfire flame.
     * @param radius The radius to search for hostile entities
     */
    public void setSpreadRadius(float radius) {
        this.spreadRadius = radius;
        markDirty();
    }
    
    /**
     * Gets the spread/search radius.
     * @return The search radius for hostile entities
     */
    public float getSpreadRadius() {
        return spreadRadius;
    }
    
    @Override
    public MagicDamage.DamageType getDamageType() {
        return MagicDamage.DamageType.FIRE;
    }
      @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        
        if (compound.hasKey("canSpreadFire")) {
            canSpreadFire = compound.getBoolean("canSpreadFire");
        }
        
        if (compound.hasKey("spreadRadius")) {
            spreadRadius = compound.getFloat("spreadRadius");
        }
        
        if (compound.hasKey("entityNearby")) {
            entityNearby = compound.getBoolean("entityNearby");
        }
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.setBoolean("canSpreadFire", canSpreadFire);
        compound.setFloat("spreadRadius", spreadRadius);
        compound.setBoolean("entityNearby", entityNearby);
        
        return compound;
    }
}
