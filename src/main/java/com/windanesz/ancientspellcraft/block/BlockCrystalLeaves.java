package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.registry.ASTabs;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockCrystalLeaves extends BlockLeaves {

	public BlockCrystalLeaves() {
		setCreativeTab(ASTabs.ANCIENTSPELLCRAFT);
//		AncientSpellcraft.proxy.setGraphicsLevel(this, true);
		setDefaultState(blockState.getBaseState().withProperty(CHECK_DECAY, Boolean.valueOf(true)).withProperty(DECAYABLE, Boolean.valueOf(true)));
	}

	@Override
	protected void dropApple(World worldIn, BlockPos pos, IBlockState state, int chance) {
		if (worldIn.rand.nextInt(chance) == 0) {
			spawnAsEntity(worldIn, pos, new ItemStack(Items.APPLE));
		}
	}

	/**
	 * Get the Item that this Block should drop when harvested.
	 *
	 * @param state   the state
	 * @param rand    the rand
	 * @param fortune the fortune
	 * @return the item dropped
	 */
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return null;
	}

	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks).
	 *
	 * @param itemIn the item in
	 * @param items  the items
	 * @return the sub blocks
	 */
	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		items.add(new ItemStack(this));
	}

	/* (non-Javadoc)
	 * @see net.minecraft.block.Block#getSilkTouchDrop(net.minecraft.block.state.IBlockState)
	 */
	@Override
	protected ItemStack getSilkTouchDrop(IBlockState state) {
		return new ItemStack(Item.getItemFromBlock(this));
	}

	/**
	 * Convert the given metadata into a BlockState for this Block.
	 *
	 * @param meta the meta
	 * @return the state from meta
	 */
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(DECAYABLE, Boolean.valueOf((meta & 4) == 0)).withProperty(CHECK_DECAY, Boolean.valueOf((meta & 8) > 0));
	}

	/**
	 * Convert the BlockState into the correct metadata value.
	 *
	 * @param state the state
	 * @return the meta from state
	 */
	@Override
	public int getMetaFromState(IBlockState state) {
		int i = 0;

		if (!state.getValue(DECAYABLE).booleanValue()) {
			i |= 4;
		}

		if (state.getValue(CHECK_DECAY).booleanValue()) {
			i |= 8;
		}

		return i;
	}

	/* (non-Javadoc)
	 * @see net.minecraft.block.Block#createBlockState()
	 */
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {CHECK_DECAY, DECAYABLE});
	}

	/**
	 * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
	 * returns the metadata of the dropped item based on the old metadata of the block.
	 *
	 * @param state the state
	 * @return the int
	 */
	@Override
	public int damageDropped(IBlockState state) {
		return 0;
	}

	/**
	 * Spawns the block's drops in the world. By the time this is called the Block has possibly been set to air via
	 * Block.removedByPlayer
	 *
	 * @param worldIn the world in
	 * @param player  the player
	 * @param pos     the pos
	 * @param state   the state
	 * @param te      the te
	 * @param stack   the stack
	 */
	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
		if (!worldIn.isRemote && stack.getItem() == Items.SHEARS) {
			player.addStat(StatList.getBlockStats(this));
		} else {
			super.harvestBlock(worldIn, player, pos, state, te, stack);
		}
	}

	/**
	 * @see net.minecraftforge.common.IShearable#onSheared(net.minecraft.item.ItemStack, net.minecraft.world.IBlockAccess, net.minecraft.util.math.BlockPos, int)
	 */
	@Override
	public NonNullList<ItemStack> onSheared(ItemStack item, net.minecraft.world.IBlockAccess world, BlockPos pos, int fortune) {
		return NonNullList.withSize(1, new ItemStack(this));
	}

	/**
	 * @see net.minecraft.block.BlockLeaves#getWoodType(int)
	 */
	@Override
	public EnumType getWoodType(int meta) {
		// TODO Auto-generated method stub
		return null;
	}
}