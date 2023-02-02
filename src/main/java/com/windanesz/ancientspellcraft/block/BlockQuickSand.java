package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.tileentity.TileEntityPlayerSave;
import electroblob.wizardry.util.MagicDamage;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockQuickSand extends Block implements ITileEntityProvider, ITemporaryBlock {
	public BlockQuickSand() {
		super(Material.SAND);
		this.setSoundType(SoundType.SAND);
		setTemporaryBlockProperties(this);
	}

	//////////////// Quicksand behaviour ////////////////

	/**
	 * Called When an Entity Collided with the Block
	 */
	public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
		entity.setInWeb();

		if (shouldDrown(world, entity)) {

			if (world.getTileEntity(pos) instanceof TileEntityPlayerSave) {
				TileEntityPlayerSave tileentity = (TileEntityPlayerSave) world.getTileEntity(pos);

				DamageSource source = tileentity.getCaster() == null ? DamageSource.FLY_INTO_WALL
						: MagicDamage.causeDirectMagicDamage(tileentity.getCaster(), MagicDamage.DamageType.MAGIC);

				entity.attackEntityFrom(source, 0.5f);
			}
		}
	}

	public static boolean shouldDrown(World world, Entity entity) {
		double y = entity.posY + entity.getEyeHeight();
		return world.getBlockState(new BlockPos(entity.posX, y, entity.posZ)).getBlock() == ASBlocks.QUICKSAND;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes,
			@Nullable Entity entityIn, boolean isActualState) {

		if (entityIn instanceof EntityPlayer && ItemArtefact.isArtefactActive((EntityPlayer) entityIn, ASItems.charm_quicksand_walker) ) {
			super.addCollisionBoxToList(state,worldIn, pos,entityBox, collidingBoxes, entityIn, isActualState);
		}

		// no collision otherwise = entities will sink into this block
	}

	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return FULL_BLOCK_AABB;
	}

	public boolean isFullCube(IBlockState state) {
		return false;
	}

	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) { return BlockFaceShape.UNDEFINED; }

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