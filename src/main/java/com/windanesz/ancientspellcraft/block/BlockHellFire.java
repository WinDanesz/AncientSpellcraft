package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import com.windanesz.ancientspellcraft.util.SpellTeleporter;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockHellFire extends Block {

	public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);
	public static final PropertyBool NORTH = PropertyBool.create("north");
	public static final PropertyBool EAST = PropertyBool.create("east");
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	public static final PropertyBool WEST = PropertyBool.create("west");
	public static final PropertyBool UPPER = PropertyBool.create("up");

	public BlockHellFire() {
		super(Material.FIRE);
		this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)).withProperty(NORTH, Boolean.valueOf(false)).withProperty(EAST, Boolean.valueOf(false)).withProperty(SOUTH, Boolean.valueOf(false)).withProperty(WEST, Boolean.valueOf(false)).withProperty(UPPER, Boolean.valueOf(false)));
		this.setTickRandomly(true);
		setLightLevel(1.5F);
	}

	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		if (!worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP) && !Blocks.FIRE.canCatchFire(worldIn, pos.down(), EnumFacing.UP)) {
			return state.withProperty(NORTH, this.canCatchFire(worldIn, pos.north(), EnumFacing.SOUTH))
					.withProperty(EAST, this.canCatchFire(worldIn, pos.east(), EnumFacing.WEST))
					.withProperty(SOUTH, this.canCatchFire(worldIn, pos.south(), EnumFacing.NORTH))
					.withProperty(WEST, this.canCatchFire(worldIn, pos.west(), EnumFacing.EAST))
					.withProperty(UPPER, this.canCatchFire(worldIn, pos.up(), EnumFacing.DOWN));
		}
		return this.getDefaultState();
	}

	public int tickRate(World worldIn) {
		return 20;
	}

	/**
	 * Returns the quantity of items to drop on block destruction.
	 */

	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public boolean isCollidable() {
		return true;
	}

	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return NULL_AABB;
	}

	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	// TODO: update

	/**
	 * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
	 */
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (!worldIn.isAreaLoaded(pos, 2))
			return; // Forge: prevent loading unloaded chunks

		int age = ((Integer) state.getValue(AGE)).intValue();
		if (age < 15) {
			state = state.withProperty(AGE, Integer.valueOf(age + rand.nextInt(3)));
			worldIn.setBlockState(pos, state, 4);
			worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn) + rand.nextInt(10));

		}
		if (age >= 12) {
			worldIn.setBlockToAir(pos);
			return;
		}
	}

	public boolean requiresUpdates() {
		return false;
	}

	public void teleportPlayer(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		if (entityIn instanceof EntityPlayer && !entityIn.isRiding() && !entityIn.isBeingRidden() && entityIn.isNonBoss()
				&& !((EntityPlayer) entityIn).isPotionActive(AncientSpellcraftPotions.dimensional_anchor)) {
			int currDim = entityIn.world.provider.getDimension();
			int targetDim;
			int x = pos.getX();
			int z = pos.getZ();

			if (currDim == -1) { // coming from nether to overworld
				targetDim = 0;
			} else {  // going to nether from overworld
				targetDim = -1;
			}
			worldIn.setBlockToAir(pos); // remove the nether fire block

			if (!worldIn.isRemote)
				SpellTeleporter.teleportEntity(targetDim, entityIn.posX, entityIn.posY, entityIn.posZ, true, (EntityPlayer) entityIn);
		}
	}

	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		teleportPlayer(worldIn, pos, state, entityIn);
	}

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {

		if (rand.nextInt(24) == 0) {
			ParticleBuilder.create(ParticleBuilder.Type.FLASH)
					.pos(pos.getX() + 0.5f, pos.getY() + 0.1, pos.getZ() + 0.5f)
					.face(EnumFacing.UP)
					.clr(145, 6, 55)
					//					.clr(89, 238, 155)
					.collide(false)
					.scale(4.3F)
					.time(50)
					.spawn(worldIn);
		}

		if (rand.nextInt(24) == 0) {
			worldIn.playSound((double) ((float) pos.getX() + 0.5F), (double) ((float) pos.getY() + 0.5F), (double) ((float) pos.getZ() + 0.5F), SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
		}
		if (rand.nextInt(50) == 0) {
			worldIn.playSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 0.5F, rand.nextFloat() * 0.4F + 0.8F, false);
		}
		for (int i = 0; i < 3; ++i) {
			double d0 = (double) pos.getX() + rand.nextDouble();
			double d1 = (double) pos.getY() + rand.nextDouble() * 0.5D + 0.5D;
			double d2 = (double) pos.getZ() + rand.nextDouble();
			worldIn.spawnParticle(EnumParticleTypes.PORTAL, d0, d1, d2, 0.0D, 0.0D, 0.0D);
		}
		for (int i = 0; i < 4; ++i) {
			double d0;
			double d1 = (double) ((float) pos.getY() + rand.nextFloat());
			double d2 = (double) ((float) pos.getZ() + rand.nextFloat());
			double d3;
			double d4 = ((double) rand.nextFloat() - 0.5D) * 0.5D;
			double d5 = ((double) rand.nextFloat() - 0.5D) * 0.5D;
			int j = rand.nextInt(2) * 2 - 1;

			d0 = (double) pos.getX() + 0.5D + 0.25D * (double) j;
			d3 = (double) (rand.nextFloat() * 2.0F * (float) j);
			worldIn.spawnParticle(EnumParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);

			d0 = (double) ((float) pos.getX() + rand.nextFloat());
			d3 = ((double) rand.nextFloat() - 0.5D) * 0.5D;
			d2 = (double) pos.getZ() + 0.5D + 0.25D * (double) j;
			d5 = (double) (rand.nextFloat() * 2.0F * (float) j);
			worldIn.spawnParticle(EnumParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);

		}
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn) + worldIn.rand.nextInt(10));
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return worldIn.getBlockState(pos.down()).isTopSolid();
	}

	@Override
	public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return MapColor.TNT;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(AGE, Integer.valueOf(meta));
	}

	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((Integer) state.getValue(AGE)).intValue();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {AGE, NORTH, EAST, SOUTH, WEST, UPPER});
	}

	public boolean canCatchFire(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return world.getBlockState(pos).getBlock().isFlammable(world, pos, face);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}
}

