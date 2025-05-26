package com.windanesz.ancientspellcraft.tileentity;

import electroblob.wizardry.tileentity.TileEntityPlayerSave;
import electroblob.wizardry.util.MagicDamage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;

/**
 * Abstract base class for magical flame tile entities.
 * Provides common functionality for different types of magical flames.
 */
public abstract class AbstractMagicFlameTileEntity extends TileEntityPlayerSave implements ITickable {

    protected int ticksExisted = 0;
    protected int lifetime = -1; // -1 means infinite lifetime

    /**
     * Called every tick to update the tile entity.
     * Handles lifetime management and calls abstract methods for specific behavior.
     */
    @Override
    public void update() {
        ticksExisted++;
        
        if (world.isRemote) {
            spawnParticles();
        }

        // Check if the flame should expire
        if (shouldExpire() && !this.world.isRemote) {
            this.world.setBlockToAir(pos);
        }
    }

    /**
     * Spawn particles for the flame effect.
     * Implemented by subclasses to create specific particle effects.
     */
    protected abstract void spawnParticles();
    
    /**
     * Returns the appropriate damage type for this flame.
     * @return The damage type for this flame
     */
    public abstract MagicDamage.DamageType getDamageType();

    /**
     * Sets the lifetime of the flame in ticks.
     * @param lifetime The lifetime in ticks, or -1 for infinite
     */
    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
        markDirty();
    }

    /**
     * Returns true if the flame should expire based on its lifetime.
     * @return Whether the flame should expire
     */
    public boolean shouldExpire() {
        return lifetime > 0 && ticksExisted > lifetime;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        
        ticksExisted = compound.getInteger("ticksExisted");
        lifetime = compound.getInteger("lifetime");
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.setInteger("ticksExisted", ticksExisted);
        compound.setInteger("lifetime", lifetime);
        return compound;
    }
}
