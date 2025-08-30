package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.tileentity.AbstractMagicFlameTileEntity;
import electroblob.wizardry.util.MagicDamage;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Abstract base class for magical flame blocks.
 * Provides common functionality for different types of magical flames.
 */
public abstract class AbstractMagicFlameBlock extends Block implements ITileEntityProvider {
    
    public AbstractMagicFlameBlock() {
        super(Material.FIRE);
        this.setTickRandomly(true);
        this.setLightLevel(1.0F);
    }
    
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state,
                               EntityLivingBase placer, net.minecraft.item.ItemStack stack) {
        if (placer instanceof EntityPlayer && !world.isRemote) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof AbstractMagicFlameTileEntity) {
                ((AbstractMagicFlameTileEntity) tileEntity).setCaster((EntityPlayer) placer);
                
            }
        }
    }
    
    /**
     * Gets the appropriate damage source for this flame.
     * @param caster The entity that created the flame
     * @param tileEntity The flame tile entity
     * @return A damage source appropriate for the flame type
     */
    protected DamageSource getDamageSource(EntityLivingBase caster, AbstractMagicFlameTileEntity tileEntity) {
        return MagicDamage.causeDirectMagicDamage(caster, tileEntity.getDamageType());
    }
    
    @Override
    public int quantityDropped(Random random) {
        return 0;
    }
    
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }
    
    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (!entity.isImmuneToFire() && entity instanceof EntityLivingBase) {
            // Get the caster from the tile entity
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof AbstractMagicFlameTileEntity) {
                AbstractMagicFlameTileEntity flame = (AbstractMagicFlameTileEntity) tileEntity;
                EntityLivingBase caster = flame.getCaster();
                
                // Check if the entity is a valid target (not the caster or an ally)
                if (entity != caster && entity instanceof EntityLivingBase) {
                    if (caster != null) {
                        // Use magic damage type based on the flame type
                        DamageSource damageSource = getDamageSource(caster, flame);
                        entity.attackEntityFrom(damageSource, 1.0F);
                    } else {
                        // No caster, use regular fire damage
                        entity.attackEntityFrom(DamageSource.IN_FIRE, 1.0F);
                    }
                    
                    // Set entity on fire for a short duration
                    entity.setFire(2);
                }
            }
        }
    }
    
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.down()).isTopSolid();
    }
    
    @Override
    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return MapColor.EMERALD;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        // Just check if the flame should expire
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof AbstractMagicFlameTileEntity) {
            AbstractMagicFlameTileEntity flame = (AbstractMagicFlameTileEntity) tileEntity;
            
            // Check if flame should expire
            if (flame.shouldExpire()) {
                world.setBlockToAir(pos);
            }
        }
    }
    
    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
}
