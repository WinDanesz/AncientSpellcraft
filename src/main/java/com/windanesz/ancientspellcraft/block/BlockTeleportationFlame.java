package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.tileentity.TileEntityTeleportationFlame;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Block implementation for teleportation flames.
 * Creates and manages the teleportation flame tile entity.
 * Entity collision handling is now done in the tile entity.
 */
public class BlockTeleportationFlame extends AbstractMagicFlameBlock {

    public BlockTeleportationFlame() {
        super();
        this.setHardness(1.0F);
    }
    
    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityTeleportationFlame();
    }
}
