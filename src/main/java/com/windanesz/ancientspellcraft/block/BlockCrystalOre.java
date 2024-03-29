package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASTabs;
import electroblob.wizardry.constants.Element;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockCrystalOre extends Block {

	private Element element;

	public BlockCrystalOre(Element element) {
		super(Material.ROCK);
		this.setSoundType(SoundType.STONE);
		setHardness(3.0F);
		setCreativeTab(ASTabs.ANCIENTSPELLCRAFT);
		setResistance(5.0F);
		setHarvestLevel("pickaxe", 2);
		this.element = element;
	}

	@Override
	public int quantityDropped(IBlockState state, int fortune, Random random) {
		// This now works the same way as vanilla ores
		if (fortune > 0) {

			int i = random.nextInt(fortune);

			return (random.nextInt(1) + 1 + i);

		} else {
			return random.nextInt(1) + 1;
		}
	}

	@Override
	public Item getItemDropped(IBlockState state, Random random, int fortune) {
		switch (element) {
			case FIRE:
				return ASItems.crystal_shard_fire;
			case ICE:
				return ASItems.crystal_shard_ice;
			case NECROMANCY:
				return ASItems.crystal_shard_necromancy;
			case SORCERY:
				return ASItems.crystal_shard_sorcery;
			case HEALING:
				return ASItems.crystal_shard_healing;
			case LIGHTNING:
				return ASItems.crystal_shard_lightning;
			case EARTH:
				return ASItems.crystal_shard_earth;
			default:
				return ASItems.crystal_shard_earth;
		}
	}

	@Override
	public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {

		Random rand = world instanceof World ? ((World) world).rand : RANDOM;

		if (this.getItemDropped(state, rand, fortune) != Item.getItemFromBlock(this)) {
			return MathHelper.getInt(rand, 1, 5);
		}

		return 0;
	}


}
