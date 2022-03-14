package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.client.gui.GuiHandlerAS;
import com.windanesz.ancientspellcraft.registry.ASTabs;
import com.windanesz.ancientspellcraft.tileentity.TileArcaneAnvil;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class BlockArcaneAnvil extends BlockContainer {

	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	protected static final AxisAlignedBB X_AXIS_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.125D, 1.0D, 1.0D, 0.875D);
	protected static final AxisAlignedBB Z_AXIS_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.0D, 0.875D, 1.0D, 1.0D);
	protected static final Logger LOGGER = LogManager.getLogger();

	public BlockArcaneAnvil() {
		super(Material.ANVIL);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		this.setLightOpacity(0);
		this.setCreativeTab(ASTabs.ANCIENTSPELLCRAFT);
		this.setSoundType(SoundType.ANVIL);
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		EnumFacing enumfacing = placer.getHorizontalFacing().rotateY();

		try {
			return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(FACING, enumfacing);
		}
		catch (IllegalArgumentException var11) {
			if (!worldIn.isRemote) {
				LOGGER.warn(String.format("Invalid damage property for arcane anvil at %s. Found %d, must be in [0, 1, 2]", pos, meta >> 2));

				if (placer instanceof EntityPlayer) {
					placer.sendMessage(new TextComponentTranslation("Invalid damage property. Please pick in [0, 1, 2]", new Object[0]));
				}
			}

			return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, 0, placer).withProperty(FACING, enumfacing);
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState block, EntityPlayer player, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {

		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity == null || player.isSneaking()) {
			return false;
		}

		player.openGui(AncientSpellcraft.instance, GuiHandlerAS.ARCANE_ANVIL, world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return state.getValue(FACING).getAxis() == EnumFacing.Axis.X ? X_AXIS_AABB : Z_AXIS_AABB;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex();
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.getBlock() != this ? state : state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {FACING});
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileArcaneAnvil();
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState block) {

		TileEntity tileentity = world.getTileEntity(pos);

		if (tileentity instanceof TileArcaneAnvil) {

			TileArcaneAnvil anvil = (TileArcaneAnvil) tileentity;

			// does not drop the result stack!
			for (int i = 0; i < 2; ++i) {
				ItemStack itemstack = anvil.getStackInSlot(i);

				if (!itemstack.isEmpty()) {
					InventoryHelper.spawnItemStack(world, anvil.getPos().getX(), anvil.getPos().getY(), anvil.getPos().getZ(), itemstack);
				}
			}
		}

		super.breakBlock(world, pos, block);
	}
}
