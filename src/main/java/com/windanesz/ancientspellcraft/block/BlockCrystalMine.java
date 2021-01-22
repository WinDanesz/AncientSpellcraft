package com.windanesz.ancientspellcraft.block;

import electroblob.wizardry.tileentity.TileEntityPlayerSave;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockCrystalMine extends Block implements ITileEntityProvider {

	protected static final AxisAlignedBB MINE_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.1875D, 0.875D);

	public BlockCrystalMine() {
		super(Material.IRON);
		this.setLightLevel(0.5F);
	}

	/**
	 * @deprecated call via {@link IBlockState#getBoundingBox(IBlockAccess, BlockPos)} whenever possible.
	 * Implementing/overriding is fine.
	 */
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return MINE_AABB;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		return NULL_AABB;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
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
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return null;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEntityPlayerSave();
	}

	public void explode(World world, BlockPos pos, Entity entity) {
		boolean buried = world.getBlockState(pos.up()).isTopSolid();
		if (!world.isRemote) {
			world.createExplosion(entity, pos.getX(), buried ? pos.up().up().getY() : pos.getY(), pos.getZ(),  buried ? 2.5f : 2, buried);
			world.setBlockToAir(pos);
		}
	}

	@Override
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random){
		if(world.isRemote && random.nextBoolean()){
			ParticleBuilder.create(ParticleBuilder.Type.SPARKLE)
					.pos(pos.getX() + random.nextDouble() / 2 + 0.3, pos.getY() + random.nextDouble() / 4, pos.getZ() + random.nextDouble()  / 2 + 0.3).vel(0, 0.01, 0)
					.time(20 + random.nextInt(10)).clr(0.5f + (random.nextFloat() / 2), 0.5f + (random.nextFloat() / 2),
					0.5f + (random.nextFloat() / 2)).spawn(world);
		}
	}
}
