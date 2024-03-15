package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.item.ItemRune;
import com.windanesz.ancientspellcraft.registry.Rituals;
import com.windanesz.ancientspellcraft.ritual.IHasRightClickEffect;
import com.windanesz.ancientspellcraft.tileentity.TileRune;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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

import javax.annotation.Nullable;
import java.util.Random;

public class BlockPlacedRune extends Block implements ITileEntityProvider {

	AxisAlignedBB AABB = new AxisAlignedBB(3 / 16F, 0, 3 / 16F, 13 / 16F, 1 / 16F, 13 / 16F);

	public BlockPlacedRune() {
		super(Material.ROCK, MapColor.OBSIDIAN);
		this.setSoundType(SoundType.STONE);
		setHardness(50.0F);
		setResistance(2000.0F);
		setLightLevel(0.3F);
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		TileRune tile = new TileRune();
		return tile;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(Blocks.STONE);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) { return EnumBlockRenderType.INVISIBLE; }

	@Override
	public BlockRenderLayer getRenderLayer() { return BlockRenderLayer.CUTOUT; }

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileRune) {
			TileRune tileRune = (TileRune) tile;

			if (tileRune.getRitual() != Rituals.none && !player.isSneaking()) {
				if (tileRune.getRitual() instanceof IHasRightClickEffect) {
					if (((IHasRightClickEffect) (tileRune).getRitual()).onRightClick(tileRune, player)) {
						return true;
					}
				}
			}

			ItemStack stack = new ItemStack(((TileRune) tile).getRune());

			if (tileRune.getRitual() != Rituals.none && !player.isSneaking()) {
				if (tileRune.getRitual() instanceof IHasRightClickEffect) {
					((IHasRightClickEffect) (tileRune).getRitual()).onBreak(tileRune, player);
				}
			}

			if (!player.inventory.addItemStackToInventory(stack)) {
				player.dropItem(stack, false);
			}
			world.setBlockToAir(pos);
			return true;
		}
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) { return false; }

	@Override
	public boolean isFullCube(IBlockState state) { return false;}

	@Override
	public boolean isOpaqueCube(IBlockState state) { return false; }

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) { return AABB; }

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		EnumFacing enumfacing = placer.getHorizontalFacing().getOpposite();
		if (placer.isSneaking()) {
			enumfacing = enumfacing.rotateY();
		}
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileRune && stack.getItem() instanceof ItemRune) {
			((TileRune) tile).setRune(stack.getItem());
			if (placer instanceof EntityPlayer) {

				((TileRune) tile).setPlacer((EntityPlayer) placer);
				((TileRune) tile).setDirection(enumfacing);
			}
		}
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return side == EnumFacing.DOWN;
	}

}
