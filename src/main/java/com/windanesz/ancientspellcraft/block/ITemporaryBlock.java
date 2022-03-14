package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.registry.ASTabs;
import com.windanesz.ancientspellcraft.tileentity.TileEntityRevertingBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Interface class for the temporary magical blocks. These blocks revert to their original state if they have replaced a block, after their lifetime expires.
 */
public interface ITemporaryBlock {

	default void setTemporaryBlockProperties(Block block) {
		block.setCreativeTab(ASTabs.ANCIENTSPELLCRAFT);
		block.setBlockUnbreakable();
		block.setResistance(6000000.0F);
	}

	default TileEntity createNewTileEntityDelegate(World world, int meta) {
		return new TileEntityRevertingBlock();
	}

	default Item getItemDroppedDelegate(IBlockState state, Random rand, int fortune) {
		return Items.AIR;
	}

	default void harvestBlockDelegate(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {}

	static boolean placeTemporaryBlock(EntityLivingBase placer, World world, Block temporaryBlock, BlockPos pos, int lifetime) {
		return placeTemporaryBlock(placer, world, temporaryBlock, pos, lifetime, true);
	}

	static boolean placeTemporaryBlock(EntityLivingBase placer, World world, Block temporaryBlock, BlockPos pos, int lifetime, boolean replaceAir) {
		if (!(temporaryBlock instanceof ITemporaryBlock))
			return false;

		if (!replaceAir && world.isAirBlock(pos)) {
			return false;
		}

		if ((world.getTileEntity(pos) instanceof TileEntityRevertingBlock) || world.getTileEntity(pos) != null) {
			return false;
		}

		NBTTagCompound oldStateCompound = NBTUtil.writeBlockState(new NBTTagCompound(), world.getBlockState(pos));

		world.setBlockState(pos, temporaryBlock.getDefaultState());

		TileEntity tileentity = world.getTileEntity(pos);

		if (tileentity instanceof TileEntityRevertingBlock) {
			TileEntityRevertingBlock tileEntityRevertingBlock = (TileEntityRevertingBlock) tileentity;
			tileEntityRevertingBlock.setLifetime(lifetime);

			if (placer != null)
				tileEntityRevertingBlock.setCaster(placer);

			tileEntityRevertingBlock.setOldState(NBTUtil.readBlockState(oldStateCompound));
			tileEntityRevertingBlock.sync();
		} else {
			world.setBlockState(pos, NBTUtil.readBlockState(oldStateCompound));
		}

		return true;
	}
}