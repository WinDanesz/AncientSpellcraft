package com.windanesz.ancientspellcraft.tileentity;

import com.windanesz.ancientspellcraft.util.ASUtils;
import com.windanesz.ancientspellcraft.util.SpellTeleporter;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.Location;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

/**
 * Tile entity for the teleportation flame block.
 * Teleportation flames have a blue color, deal frost damage, and can store a target location.
 */
public class TileEntityTeleportationFlame extends AbstractMagicFlameTileEntity {
    
    private Location targetLocation = null;
    private static final int TELEPORT_TICKS_REQUIRED = 20; // Entity must stay in flame for 20 ticks before teleporting
    private boolean hasValidFireplace = true;
    private int teleportCounter = 0;
    private static final double COLLISION_RADIUS = 0.5; // Radius in blocks to check for entity collision
    
    @Override
    protected void spawnParticles() {
        boolean hasCollidingEntities = teleportCounter > 0;
        
        // Spawn more particles when entities are charging up for teleportation
          int baseParticles = hasCollidingEntities ? 5 : 3;

          for (int i = 0; i < baseParticles; i++) {
            double x = pos.getX() + 0.2 + world.rand.nextDouble() * 0.6;
            double y = pos.getY() + 0.2 + world.rand.nextDouble() * 0.6;
            double z = pos.getZ() + 0.2 + world.rand.nextDouble() * 0.6;

            // Teleportation (green) flame particles
            ParticleBuilder.create(ParticleBuilder.Type.SPARKLE)
                    .pos(x, y, z)
                    .vel(0, world.rand.nextDouble() * 0.05, 0)
                    .time(10 + world.rand.nextInt(10))
                    .clr(0.0f, 0.7f + world.rand.nextFloat() * 0.3f, 0.2f)
                    .spawn(world);

            // Special charging particles when entities are standing in the flame
            if (hasCollidingEntities && world.rand.nextInt(2) == 0) {
                ParticleBuilder.create(ParticleBuilder.Type.SPARKLE)
                        .pos(x, y, z)
                        .vel(0, world.rand.nextDouble() * 0.55, 0)
                        .time(10 + world.rand.nextInt(10))
                        .clr(0.0f, 0.7f + world.rand.nextFloat() * 0.3f, 0.2f)
                        .spawn(world);
            }
        }
    }
    
    /**
     * Gets the target location for teleportation.
     * @return The target location, or null if not set
     */
    public Location getTargetLocation() {
        return targetLocation;
    }
    
    /**
     * Sets the target location for teleportation.
     * @param location The target location
     */
    public void setTargetLocation(Location location) {
        this.targetLocation = location;
        markDirty();
    }
    
    @Override
    public MagicDamage.DamageType getDamageType() {
        return MagicDamage.DamageType.FIRE;
    }
    
    @Override
    public void update() {
        super.update();
        
        if (!world.isRemote) {
            // Check for entities within radius
            double posX = pos.getX() + 0.5;
            double posY = pos.getY() + 0.5;
            double posZ = pos.getZ() + 0.5;
            
            // Check for all living entities in the flame
            List<EntityLivingBase> entities = EntityUtils.getEntitiesWithinRadius(
                    COLLISION_RADIUS + 0.5, posX, posY, posZ, world, EntityLivingBase.class);
            
            if (!entities.isEmpty()) {
                // Use the first entity for counter purposes
                EntityLivingBase entity = entities.get(0);
                processEntityCollision(entity);
            } else {
                resetTeleportCounter();
            }
        }
    }
    
    /**
     * Process entity collision with the teleportation flame
     * @param entity The entity that collided with the flame
     * @return True if the entity was teleported
     */
    private boolean processEntityCollision(EntityLivingBase entity) {
        if (entity == null) return false;
        entity.setFire(2);
        // Check if teleportation is possible
        if (targetLocation == null || !hasValidFireplace) {
            // Apply fire effect but don't teleport
            if (!world.isRemote && this.ticksExisted % 40 == 0) {
                ASUtils.sendMessage(entity, "spell.ancientspellcraft:teleportation_flame.no_target", true);
            }

            return false;
        }
        
        // Reset counter if the entity has blindness effect
        if (entity.isPotionActive(MobEffects.BLINDNESS)) {
            teleportCounter = 0;
            return false;
        }
        
        // Increment the counter
        teleportCounter++;
        
        // When counter reaches threshold, reset counter and teleport
        if (teleportCounter >= TELEPORT_TICKS_REQUIRED) {
            resetTeleportCounter();

            if (!hasValidFireplace()) {
                ASUtils.sendMessage(entity, "spell.ancientspellcraft:teleportation_flame.no_fireplace", true);
            }

            // Teleport the entity
            SpellTeleporter.teleportPlayerOrMob(entity, targetLocation);
            return true;
        }
        
        // Apply fire effect while waiting to teleport
        entity.setFire(2);
        return false;
    }

    /**
     * Resets the teleportation counter
     */
    public void resetTeleportCounter() {
        teleportCounter = 0;
    }
    
    /**
     * Returns whether the teleportation flame has a valid fireplace structure
     * @return true if the fireplace structure is valid
     */
    public boolean hasValidFireplace() {
        return hasValidFireplace;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        if (targetLocation != null) {
            compound.setTag("targetLocation", targetLocation.toNBT());
        }
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
    if (compound.hasKey("targetLocation")) {
            setTargetLocation(Location.fromNBT(compound.getCompoundTag("targetLocation")));
        } else {
            targetLocation = null;
        }
        super.readFromNBT(compound);
    }
}
