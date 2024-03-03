package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.tileentity.TileArtefactPensive;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockArtefactPensive extends BlockContainer {

	protected static final AxisAlignedBB PENSIVE_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.1875D, 0.875D);
	private static final PropertyBool EMPTY = PropertyBool.create("empty");
	private static int MAX_XP = 1395;  // 30 lvls

	public BlockArtefactPensive() {
		super(Material.CLAY);
		this.setLightLevel(0.5F);
		this.setHardness(0.5F);
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileArtefactPensive();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {

		if (!(world.getTileEntity(pos) instanceof TileArtefactPensive)) {
			return false;
		}

		TileArtefactPensive tilePensive = (TileArtefactPensive) world.getTileEntity(pos);

		// retrieve XP
		if (player.isSneaking()) {
			player.addExperience(tilePensive.getStoredXP());
			tilePensive.setStoredXP(0);
			world.setBlockState(pos, state.withProperty(EMPTY, true), 2);
		} else {
			// add xp
			int currentPlayerXP = player.experienceTotal;

			if (tilePensive.getStoredXP() < MAX_XP) {
				int maxAmountToAdd = MAX_XP - tilePensive.getStoredXP();
				if (maxAmountToAdd >= currentPlayerXP) {
					tilePensive.setStoredXP(tilePensive.getStoredXP() + currentPlayerXP);
					removePlayerXP(player, tilePensive.getStoredXP() + currentPlayerXP);
				} else {
					tilePensive.setStoredXP(tilePensive.getStoredXP() + maxAmountToAdd);
					removePlayerXP(player, maxAmountToAdd);
				}
				if (currentPlayerXP > 0) {
					world.setBlockState(pos, state.withProperty(EMPTY, false), 2);
				}
				tilePensive.markDirty();
				world.notifyBlockUpdate(pos, state, state, 3);
			}
		}

		// store xp
		return true;
	}

	public void removePlayerXP(EntityPlayer player, int amount) {
		player.experienceLevel -= amount;

		if (player.experienceLevel < 0) {
			player.experienceLevel = 0;
			player.experience = 0.0F;
			player.experienceTotal = 0;
		}
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer.Builder(this).add(EMPTY).build();
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return (state.getValue(EMPTY) ? 0 : 1);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(EMPTY, meta == 0);
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return super.getActualState(state, world, pos);
		//return super.getActualState(state, world, pos).withProperty(EMPTY, ASUtils.getTile(world, pos, TileArtefactPensive.class)
		//		.map(TileArtefactPensive::isEmpty).orElse(true));
	}

	/**
	 * @deprecated call via {@link IBlockState#getBoundingBox(IBlockAccess, BlockPos)} whenever possible.
	 * Implementing/overriding is fine.
	 */
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return PENSIVE_AABB;
	}

	/**
	 * Used to determine ambient occlusion and culling when rebuilding chunks for render
	 *
	 * @deprecated call via {@link IBlockState#isOpaqueCube()} whenever possible. Implementing/overriding is fine.
	 */
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	/**
	 * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
	 * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
	 *
	 * @deprecated call via {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
	 */
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	/**
	 * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
	 */
	public boolean isFullCube(IBlockState state) {
		return false;
	}

}
