package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASTabs;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class BlockUnsealButton extends Block {

	public BlockUnsealButton() {
		super(Material.ROCK, MapColor.STONE);
		this.setSoundType(SoundType.STONE);
		this.setCreativeTab(ASTabs.ANCIENTSPELLCRAFT);
		this.setBlockUnbreakable();
		this.setResistance(6000000.0F);
		this.setSoundType(SoundType.STONE);
	}

	public Item getItemDropped(IBlockState state, Random rand, int fortune) { return Items.AIR; }

	public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
		return new ItemStack(this);
	}

	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		boolean f = false;
		for (EnumFacing direction : EnumFacing.VALUES) {
			IBlockState blockState = worldIn.getBlockState(pos.offset(direction));

			if (blockState.getBlock() == ASBlocks.sealed_stone) {
				worldIn.setBlockState(pos.offset(direction), ASBlocks.sealed_stone.getDefaultState().withProperty(BlockSealedStone.UNSEALING, 1));
				f = true;
			}
		}

		if (!worldIn.isRemote) {
			worldIn.setBlockState(pos, ASBlocks.unsealed_stone.getDefaultState());
		}
		return f;
	}

}
