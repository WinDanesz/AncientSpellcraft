package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.tileentity.TileArcaneWall;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.util.AllyDesignationSystem;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockArcaneWall extends Block implements ITileEntityProvider, ITemporaryBlock {

	public BlockArcaneWall() {
		super(Material.ROCK);
		setTemporaryBlockProperties(this);
	}

	//////////////// ITemporaryBlock Interface implementation ////////////////

	@Override
	public boolean isToolEffective(String type, IBlockState state) { return false; }

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return FULL_BLOCK_AABB;
	}

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
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileArcaneWall();
	}

	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
	}

	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.SOLID;
	}

	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes,
			@Nullable Entity entityIn, boolean isActualState) {

		// allies can pass through
		if (entityIn instanceof EntityLivingBase && worldIn.getTileEntity(pos) instanceof TileArcaneWall
				&& (((TileArcaneWall) worldIn.getTileEntity(pos)).getCaster() == entityIn
				|| AllyDesignationSystem.isAllied(((TileArcaneWall) worldIn.getTileEntity(pos)).getCaster(), (EntityLivingBase) entityIn))) {

			return;
		}

		super.addCollisionBoxToList(state,worldIn, pos,entityBox, collidingBoxes, entityIn, isActualState);

	}

	public boolean isFullCube(IBlockState state) {
		return false;
	}

	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) { return BlockFaceShape.UNDEFINED; }

	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return FULL_BLOCK_AABB;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (playerIn != null) {
			ASUtils.sendMessage(playerIn, "interact.ancientspellcraft:arcane_barrier", true);
		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
		if (playerIn != null) {
			ASUtils.sendMessage(playerIn, "interact.ancientspellcraft:arcane_barrier", true);
		}
	}
}
