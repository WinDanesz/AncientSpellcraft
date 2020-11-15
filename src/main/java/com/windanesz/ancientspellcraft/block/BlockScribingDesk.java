package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.client.gui.GuiHandlerAS;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftTabs;
import com.windanesz.ancientspellcraft.tileentity.TileScribingDesk;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
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

public class BlockScribingDesk extends BlockContainer {
	public static final PropertyDirection FACING = BlockHorizontal.FACING;

	public BlockScribingDesk() {
		super(Material.WOOD, MapColor.WOOD);
		this.setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		setHardness(2.0F);
		setResistance(5.0F);
		setSoundType(SoundType.WOOD);
		lightOpacity = 0;
	}

	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {FACING});
	}

	protected static final AxisAlignedBB AABB_N_S = new AxisAlignedBB(-0.5, 0, 0, 1.5, 1.55, 1);
	protected static final AxisAlignedBB AABB_E_W = new AxisAlignedBB(0, 0, -0.5, 1, 1.55, 1.5);

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState block, EntityPlayer player, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {

		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity == null || player.isSneaking()) {
			return false;
		}

		player.openGui(AncientSpellcraft.instance, GuiHandlerAS.SCRIBING_DESK, world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	/**
	 * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
	 * IBlockstate
	 */
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.byIndex(meta);

		if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
			enumfacing = EnumFacing.NORTH;
		}

		return this.getDefaultState().withProperty(FACING, enumfacing);
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileScribingDesk();
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(IBlockState state) {
		return ((EnumFacing) state.getValue(FACING)).getIndex();
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		switch (state.getValue(FACING)) {
			case NORTH:
			case SOUTH:
				return AABB_N_S;
			case EAST:
			case WEST:
				return AABB_E_W;
			default:
				return AABB_N_S;
		}
	}

	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isTranslucent(IBlockState state) {
		return true;
	}

	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}

	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState block) {

		TileEntity tileentity = world.getTileEntity(pos);

		if (tileentity instanceof TileScribingDesk) {
			InventoryHelper.dropInventoryItems(world, pos, (TileScribingDesk) tileentity);
		}

		super.breakBlock(world, pos, block);
	}
}
