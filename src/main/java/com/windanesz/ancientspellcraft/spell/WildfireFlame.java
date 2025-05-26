package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.tileentity.TileEntityWildfireFlame;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.Spell;
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
 * A spell that places a wildfire flame block where the player targets.
 * <p>
 * This spell uses the {@link com.windanesz.ancientspellcraft.block.BlockWildfireFlame} and its tile entity to create
 * magical flames that can damage entities and spread like fire to nearby blocks.
 */
public class WildfireFlame extends SpellRayAS {

    public WildfireFlame() {
        super("wildfire_flame", SpellActions.SUMMON, false);
        this.soundValues(1.0f, 1.2f, 0.4f);
    }

    @Override
    protected boolean onEntityHit(World world, Entity target, Vec3d hit,
                                @Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
        // We only want to place blocks when hitting a block, not an entity
        return false;
    }    @Override
    protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit,
                               @Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
        
        // Don't do anything if we're on the client side
        if (world.isRemote) return false;
        
        // Position where we'll place the wildfire flame (adjacent to the hit block)
        BlockPos placePos = pos.offset(side);
        
        // Check if we can place a block at the position
        if (BlockUtils.canBlockBeReplaced(world, placePos)) {
            // Place the wildfire flame block
            if (world.setBlockState(placePos, ASBlocks.WILDFIRE_FLAME.getDefaultState())) {
                
                // Get the tile entity and configure it
                TileEntity tileEntity = world.getTileEntity(placePos);
                if (tileEntity instanceof TileEntityWildfireFlame) {
                    TileEntityWildfireFlame wildfireFlame = (TileEntityWildfireFlame) tileEntity;
                    
                    // Configure the flame properties
                    // Set lifetime based on potency modifier, default 30 seconds
                    int lifetime = (int)(30 * 20 * modifiers.get(SpellModifiers.POTENCY));
                    wildfireFlame.setLifetime(lifetime);
                    
                    // Set the spread radius based on the effect radius modifier (default 2)
                    float spreadRadius = (float)(2 * modifiers.get(Spell.EFFECT_RADIUS));
                    wildfireFlame.setSpreadRadius(spreadRadius);
                    
                    // If caster exists, save it to the tile entity
                    if (caster != null) {
                        wildfireFlame.setCaster(caster);
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
