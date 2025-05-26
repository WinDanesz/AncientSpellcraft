package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.registry.ASTabs;
import com.windanesz.ancientspellcraft.tileentity.TileEntityArcaneFlame;
import com.windanesz.ancientspellcraft.tileentity.AbstractMagicFlameTileEntity;
import com.windanesz.ancientspellcraft.tileentity.TileEntityTeleportationFlame;
import com.windanesz.ancientspellcraft.tileentity.TileEntityWildfireFlame;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Block implementation for arcane flames.
 * Creates and manages the arcane flame tile entity.
 */
public class BlockArcaneFlame extends AbstractMagicFlameBlock {

    public BlockArcaneFlame() {
        super();
        this.setCreativeTab(ASTabs.ANCIENTSPELLCRAFT);
    }
    
    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityArcaneFlame();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, 
                                   EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tileEntity = world.getTileEntity(pos);
        
        if (player.isSneaking()) {
            // Cycle through flame types on sneak-right-click
            TileEntity newTileEntity = null;
            
            if (tileEntity instanceof TileEntityArcaneFlame) {
                // Change from Arcane to Teleportation
                newTileEntity = new TileEntityTeleportationFlame();
            } else if (tileEntity instanceof TileEntityTeleportationFlame) {
                // Change from Teleportation to Wildfire
                newTileEntity = new TileEntityWildfireFlame();
            } else if (tileEntity instanceof TileEntityWildfireFlame) {
                // Change from Wildfire back to Arcane
                newTileEntity = new TileEntityArcaneFlame();
            }
            
            if (newTileEntity != null && tileEntity instanceof AbstractMagicFlameTileEntity) {
                // Transfer common properties
                AbstractMagicFlameTileEntity oldFlame = (AbstractMagicFlameTileEntity) tileEntity;
                AbstractMagicFlameTileEntity newFlame = (AbstractMagicFlameTileEntity) newTileEntity;
                
                // Copy over common data
                if (oldFlame.getCaster() != null) {
                    newFlame.setCaster(oldFlame.getCaster());
                }
                
                // Copy over lifetime
                NBTTagCompound oldData = new NBTTagCompound();
                oldFlame.writeToNBT(oldData);
                
                int lifetime = oldData.getInteger("lifetime");
                int ticksExisted = oldData.getInteger("ticksExisted");
                
                // Replace the tile entity
                newFlame.setLifetime(lifetime);
                NBTTagCompound newData = new NBTTagCompound();
                newFlame.writeToNBT(newData);
                newData.setInteger("ticksExisted", ticksExisted);
                
                // Update the world with the new tile entity
                newTileEntity.readFromNBT(newData);
                world.setTileEntity(pos, newTileEntity);
                
                if (!world.isRemote) {
                    // Visual effect when toggling
                    world.playSound(null, pos, net.minecraft.init.SoundEvents.BLOCK_FIRE_EXTINGUISH,
                            SoundCategory.BLOCKS, 0.5f, 1.0f);
                }
                return true;
            }
        }
        
        return false;
    }
}
