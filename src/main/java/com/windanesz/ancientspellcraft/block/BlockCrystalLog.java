package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftTabs;
import net.minecraft.block.BlockLog;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

// TODO: Auto-generated Javadoc
public class BlockCrystalLog extends BlockLog
{

	/**
	 * Instantiates a new block log cloud.
	 */
	public BlockCrystalLog()
	{
		super();
		setDefaultState(blockState.getBaseState().withProperty(LOG_AXIS, BlockLog.EnumAxis.Y));
		setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT);
	}

	/**
	 * Get the MapColor for this Block and the given BlockState.
	 *
	 * @param state the state
	 * @param worldIn the world in
	 * @param pos the pos
	 * @return the map color
	 */
	@Override
	public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		return Blocks.LOG.getDefaultState().getMapColor(worldIn, pos);
	}

	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks).
	 *
	 * @param itemIn the item in
	 * @param items the items
	 * @return the sub blocks
	 */
	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items)
	{
		items.add(new ItemStack(this));
	}

	/**
	 * Convert the given metadata into a BlockState for this Block.
	 *
	 * @param meta the meta
	 * @return the state from meta
	 */
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		IBlockState state = this.getDefaultState();

		switch (meta & 12)
		{
			case 0:
				state = state.withProperty(LOG_AXIS, BlockLog.EnumAxis.Y);
				break;
			case 4:
				state = state.withProperty(LOG_AXIS, BlockLog.EnumAxis.X);
				break;
			case 8:
				state = state.withProperty(LOG_AXIS, BlockLog.EnumAxis.Z);
				break;
			default:
				state = state.withProperty(LOG_AXIS, BlockLog.EnumAxis.NONE);
		}

		return state;
	}

	/**
	 * Convert the BlockState into the correct metadata value.
	 *
	 * @param state the state
	 * @return the meta from state
	 */
	@Override
	@SuppressWarnings("incomplete-switch")
	public int getMetaFromState(IBlockState state)
	{
		int meta = 0;

		switch (state.getValue(LOG_AXIS))
		{
			case X:
				meta |= 4;
				break;
			case Z:
				meta |= 8;
				break;
			case NONE:
				meta |= 12;
		}

		return meta;
	}

	/* (non-Javadoc)
	 * @see net.minecraft.block.BlockRotatedPillar#createBlockState()
	 */
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {LOG_AXIS});
	}

	/* (non-Javadoc)
	 * @see net.minecraft.block.BlockRotatedPillar#getSilkTouchDrop(net.minecraft.block.state.IBlockState)
	 */
	@Override
	protected ItemStack getSilkTouchDrop(IBlockState state)
	{
		return new ItemStack(Item.getItemFromBlock(this), 1);
	}

	/**
	 * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
	 * returns the metadata of the dropped item based on the old metadata of the block.
	 *
	 * @param state the state
	 * @return the int
	 */
	@Override
	public int damageDropped(IBlockState state)
	{
		return 0;
	}
}