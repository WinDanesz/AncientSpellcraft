package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.client.gui.GuiHandlerAS;
import com.windanesz.ancientspellcraft.tileentity.TileSentinel;
import com.windanesz.ancientspellcraft.tileentity.TileSphereCognizance;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockSentinel extends BlockContainer {

	private static final AxisAlignedBB SENTINEL_AABB = new AxisAlignedBB(0.3125D, 0.0D, 0.3125D, 0.6875D, 0.5D, 0.6875D);
	private final float maxHealth;
	private boolean large;

	public BlockSentinel(float health, boolean large) {
		super(net.minecraft.block.material.Material.CLAY);
		this.setLightLevel(large ? 0.8f : 0.6f);
		this.maxHealth = health;
		setHardness(10.5f);
		this.large = large;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return SENTINEL_AABB;
	}

	/**
	 * Returns a new instance of a block's tile entity class. Called on placing the block.
	 */
	@Nullable
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		TileSentinel tile = new TileSentinel();
		tile.setSpellCasterHealth(maxHealth);
		tile.setLarge(large);
		return tile;
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
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
		public boolean onBlockActivated(World world, BlockPos pos, IBlockState block, EntityPlayer player, EnumHand hand,
				EnumFacing side, float hitX, float hitY, float hitZ) {

			TileEntity tileEntity = world.getTileEntity(pos);

			if (tileEntity == null || player.isSneaking()) {
				return false;
			}

			player.openGui(AncientSpellcraft.instance, GuiHandlerAS.SPHERE_COGNIZANCE, world, pos.getX(), pos.getY(), pos.getZ());
			return true;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState block) {

		TileEntity tileentity = world.getTileEntity(pos);

		if (tileentity instanceof TileSphereCognizance) {
			InventoryHelper.dropInventoryItems(world, pos, (TileSphereCognizance) tileentity);
		}

		super.breakBlock(world, pos, block);
	}
}
