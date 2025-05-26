package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.tileentity.TileEntityArcaneFlame;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * A spell that places an arcane flame block where the player targets.
 * <p>
 * This spell uses the {@link com.windanesz.ancientspellcraft.block.BlockArcaneFlame} and its tile entity to create
 * magical flames that can damage entities and spread to nearby blocks.
 */
public class ArcaneFlame extends SpellRayAS {

    public ArcaneFlame() {
        super("arcane_flame", SpellActions.SUMMON, false);
        this.soundValues(1.0f, 1.0f, 0.4f);
    }

    @Override
    protected boolean onEntityHit(World world, Entity target, Vec3d hit,
                                @Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
        // We only want to place blocks when hitting a block, not an entity
        return false;
    }

    @Override
    protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit,
                               @Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
        
        // Don't do anything if we're on the client side
        if (world.isRemote) return false;
        
        // Position where we'll place the arcane flame (adjacent to the hit block)
        BlockPos placePos = pos.offset(side);
        
        // Check if we can place a block at the position
        if (BlockUtils.canBlockBeReplaced(world, placePos)) {
            // Place the arcane flame block
            if (world.setBlockState(placePos, ASBlocks.ARCANE_FLAME.getDefaultState())) {
                
                // Get the tile entity and configure it
                TileEntity tileEntity = world.getTileEntity(placePos);
                if (tileEntity instanceof TileEntityArcaneFlame) {
                    TileEntityArcaneFlame arcaneFlame = (TileEntityArcaneFlame) tileEntity;
                      // Configure the flame properties
                    // Set lifetime based on potency modifier, default 30 seconds
                    int lifetime = (int)(30 * 20 * modifiers.get(SpellModifiers.POTENCY));
                    arcaneFlame.setLifetime(lifetime);
                    
                    // If caster exists, save it to the tile entity
                    if (caster != null) {
                        arcaneFlame.setCaster(caster);
                    }
                }
                
                return true;
            }
        }
        
        return false;
    }

    @Override
    protected boolean onMiss(World world, @Nullable EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
        return false;
    }
}
