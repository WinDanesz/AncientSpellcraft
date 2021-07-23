package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.tileentity.TileEntityRevertingBlock;
import electroblob.wizardry.util.AllyDesignationSystem;
import net.minecraft.block.BlockMagma;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockConjuredMagma extends BlockMagma implements ITileEntityProvider, ITemporaryBlock {

	public BlockConjuredMagma() {
		super();
		setTemporaryBlockProperties(this);
	}

	public void onEntityWalk(World worldIn, BlockPos pos, Entity walkingEntity) {

		EntityLivingBase caster = worldIn.getTileEntity(pos) instanceof TileEntityRevertingBlock ? ((TileEntityRevertingBlock) worldIn.getTileEntity(pos)).getCaster() : null;

		if (!walkingEntity.isImmuneToFire() && walkingEntity instanceof EntityLivingBase && !EnchantmentHelper.hasFrostWalkerEnchantment((EntityLivingBase) walkingEntity)) {
			if (AllyDesignationSystem.isValidTarget(caster, walkingEntity)) {

				if (caster != null && caster == walkingEntity)
					return;

				walkingEntity.attackEntityFrom(DamageSource.HOT_FLOOR, 1.0F);
			}
		}
	}

	//////////////// ITemporaryBlock Interface implementation ////////////////

	@Override
	public boolean isToolEffective(String type, IBlockState state) { return false; }

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) { return getItemDroppedDelegate(state, rand, fortune); }

	@Override
	protected boolean canSilkHarvest() {
		return false;
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
		harvestBlockDelegate(worldIn, player, pos, state, te, stack);
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(World world, int meta) { return createNewTileEntityDelegate(world, meta); }
}
