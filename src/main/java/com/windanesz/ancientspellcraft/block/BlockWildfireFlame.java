package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.registry.ASTabs;
import com.windanesz.ancientspellcraft.tileentity.TileEntityWildfireFlame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Block implementation for wildfire flames.
 * Creates and manages the wildfire flame tile entity.
 * Has additional functionality for spreading fire.
 */
public class BlockWildfireFlame extends AbstractMagicFlameBlock {

    public BlockWildfireFlame() {
        super();
        this.setCreativeTab(ASTabs.ANCIENTSPELLCRAFT);
    }
      @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityWildfireFlame();
    }
    
    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        // Make sure to check for expiration first by calling super
        super.updateTick(world, pos, state, rand);
    }
      @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (!entity.isImmuneToFire() && entity instanceof EntityLivingBase) {
            // Get the caster from the tile entity
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof TileEntityWildfireFlame) {
                TileEntityWildfireFlame flame = (TileEntityWildfireFlame) tileEntity;
                EntityLivingBase caster = flame.getCaster();
                
                // Check if the entity is a valid target (not the caster or an ally)
                if (entity != caster && entity instanceof EntityLivingBase) {
                    if (caster != null) {
                        // Use magic damage type based on the flame type - wildfire deals more damage
                        DamageSource damageSource = getDamageSource(caster, flame);
                        entity.attackEntityFrom(damageSource, 2.0F); // Increased damage from 1.0F to 2.0F
                    } else {
                        // No caster, use regular fire damage with increased amount
                        entity.attackEntityFrom(DamageSource.IN_FIRE, 2.0F);
                    }
                    
                    // Set entity on fire for a longer duration
                    entity.setFire(4); // Increased from 2 to 4 seconds
                }
            } else {
                // Fallback to parent behavior if the tile entity isn't present or is wrong type
                super.onEntityCollision(world, pos, state, entity);
            }
        }
    }
}
