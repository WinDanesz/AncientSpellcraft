package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASTabs;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

public class BlockSealedStone extends Block {

	public static final PropertyInteger UNSEALING = PropertyInteger.create("unsealing", 0, 3);

	public BlockSealedStone() {
		super(Material.ROCK, MapColor.STONE);
		this.setBlockUnbreakable();
		this.setResistance(6000000.0F);
		this.setSoundType(SoundType.STONE);
		this.setCreativeTab(ASTabs.ANCIENTSPELLCRAFT);
		this.setDefaultState(this.blockState.getBaseState().withProperty(UNSEALING, 0));
		this.setTickRandomly(true);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(ASBlocks.unsealed_stone);
	}

	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(UNSEALING, meta);
	}

	public int getMetaFromState(IBlockState state) {
		return state.getValue(UNSEALING);
	}

	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, UNSEALING);
	}

	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {

		Block block = world.getBlockState(fromPos).getBlock();
		IBlockState test = world.getBlockState(fromPos);
		if (block == ASBlocks.sealed_stone && test.getValue(UNSEALING) > 0) {
			world.setBlockState(pos, state.withProperty(UNSEALING, 1));
		}

		super.neighborChanged(state, world, pos, blockIn, fromPos);
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(world, pos, state, rand);

		if (state.getValue(UNSEALING) > 0 && state.getValue(UNSEALING) < 3) {
			state = state.withProperty(UNSEALING, state.getValue(UNSEALING) + 1);
			world.setBlockState(pos, state);
		}

		if (state.getValue(UNSEALING) == 3) {
			world.setBlockState(pos, ASBlocks.unsealed_stone.getDefaultState());
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced){
		super.addInformation(itemstack,world,tooltip, advanced);

		if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak)) {
			AncientSpellcraft.proxy.addMultiLineDescription(tooltip, I18n.format("tooltip.ancientspellcraft:sealed_stone.more_info"));
		} else {
			tooltip.add(I18n.format("tooltip.ancientspellcraft:more_info"));
		}
	}
}
