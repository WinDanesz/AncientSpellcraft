package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.block.BlockIceDoor;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftBlocks;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.minecraft.block.BlockSlab.HALF;

public class IceTower extends Spell {
	private static Random rand = new Random();

	public IceTower() {
		super(AncientSpellcraft.MODID, "ice_tower", SpellActions.SUMMON, false);
		this.soundValues(0.5f, 1.1f, 0.2f);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		if (!caster.onGround) {
			return false;
		}

		BlockPos pos = caster.getPosition().down();

		for (BlockPos currTestPos : BlockPos.getAllInBox(caster.getPosition().offset(EnumFacing.SOUTH, 5).offset(EnumFacing.WEST, 5),
				caster.getPosition().offset(EnumFacing.UP).offset(EnumFacing.NORTH, 5).offset(EnumFacing.EAST, 5))) {
			if (!world.canSeeSky(currTestPos)) {
				if (!world.isRemote && caster instanceof EntityPlayer) {
					caster.sendStatusMessage(new TextComponentTranslation("spell.ancientspellcraft:ice_tower.no_room"), true);
				}
				return false;
			}
		}

		List<BlockPos> blockPosList = new ArrayList<>();
		List<BlockPos> blockPosList2 = new ArrayList<>();
		List<BlockPos> blockPosList3 = new ArrayList<>();
		BlockPos layer1 = pos.offset(EnumFacing.UP);

		for (int i = 0; i < 10; i++) {
			blockPosList.add(layer1.offset(EnumFacing.UP, i).offset(EnumFacing.NORTH, 2));
			blockPosList.add(layer1.offset(EnumFacing.UP, i).offset(EnumFacing.SOUTH, 2));
			blockPosList.add(layer1.offset(EnumFacing.UP, i).offset(EnumFacing.EAST, 2));
			blockPosList.add(layer1.offset(EnumFacing.UP, i).offset(EnumFacing.WEST, 2));
			blockPosList.add(layer1.offset(EnumFacing.UP, i).offset(EnumFacing.NORTH, 2).offset(EnumFacing.EAST, 1));
			blockPosList.add(layer1.offset(EnumFacing.UP, i).offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST, 1));
			blockPosList.add(layer1.offset(EnumFacing.UP, i).offset(EnumFacing.SOUTH, 2).offset(EnumFacing.WEST, 1));
			blockPosList.add(layer1.offset(EnumFacing.UP, i).offset(EnumFacing.NORTH, 2).offset(EnumFacing.WEST, 1));
			blockPosList.add(layer1.offset(EnumFacing.UP, i).offset(EnumFacing.NORTH, 1).offset(EnumFacing.EAST, 2));
			blockPosList.add(layer1.offset(EnumFacing.UP, i).offset(EnumFacing.SOUTH, 1).offset(EnumFacing.EAST, 2));
			blockPosList.add(layer1.offset(EnumFacing.UP, i).offset(EnumFacing.NORTH, 1).offset(EnumFacing.WEST, 2));
			blockPosList.add(layer1.offset(EnumFacing.UP, i).offset(EnumFacing.SOUTH, 1).offset(EnumFacing.WEST, 2));

			// floor
			for (BlockPos currpos : BlockPos.getAllInBox(pos.offset(EnumFacing.DOWN).offset(EnumFacing.NORTH, 2).offset(EnumFacing.WEST, 2).add(0, 1, 0), pos.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.WEST, 2).add(4, 0, 1))) {
				blockPosList.add(currpos);
			}
			//			blockPosList.add(layer1.offset(EnumFacing.NORTH, 2).offset());
		}

		for (BlockPos currPos : blockPosList) {
			world.setBlockState(currPos, Blocks.SNOW.getDefaultState());
		}

		BlockPos layer2 = layer1.offset(EnumFacing.UP, 10);

		for (int i = 0; i < 3; i++) {
			blockPosList2.add(layer2.offset(EnumFacing.UP, i).offset(EnumFacing.NORTH, 3));
			blockPosList2.add(layer2.offset(EnumFacing.UP, i).offset(EnumFacing.SOUTH, 3));
			blockPosList2.add(layer2.offset(EnumFacing.UP, i).offset(EnumFacing.EAST, 3));
			blockPosList2.add(layer2.offset(EnumFacing.UP, i).offset(EnumFacing.WEST, 3));

			blockPosList2.add(layer2.offset(EnumFacing.UP, i).offset(EnumFacing.NORTH, 1).offset(EnumFacing.EAST, 3));
			blockPosList2.add(layer2.offset(EnumFacing.UP, i).offset(EnumFacing.NORTH, 1).offset(EnumFacing.WEST, 3));
			blockPosList2.add(layer2.offset(EnumFacing.UP, i).offset(EnumFacing.SOUTH, 1).offset(EnumFacing.EAST, 3));
			blockPosList2.add(layer2.offset(EnumFacing.UP, i).offset(EnumFacing.SOUTH, 1).offset(EnumFacing.WEST, 3));

			blockPosList2.add(layer2.offset(EnumFacing.UP, i).offset(EnumFacing.NORTH, 3).offset(EnumFacing.EAST));
			blockPosList2.add(layer2.offset(EnumFacing.UP, i).offset(EnumFacing.NORTH, 3).offset(EnumFacing.WEST));
			blockPosList2.add(layer2.offset(EnumFacing.UP, i).offset(EnumFacing.SOUTH, 3).offset(EnumFacing.EAST));
			blockPosList2.add(layer2.offset(EnumFacing.UP, i).offset(EnumFacing.SOUTH, 3).offset(EnumFacing.WEST));

			blockPosList3.add(layer2.offset(EnumFacing.UP, i).offset(EnumFacing.NORTH, 2).offset(EnumFacing.EAST, 2));
			blockPosList3.add(layer2.offset(EnumFacing.UP, i).offset(EnumFacing.NORTH, 2).offset(EnumFacing.WEST, 2));
			blockPosList3.add(layer2.offset(EnumFacing.UP, i).offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST, 2));
			blockPosList3.add(layer2.offset(EnumFacing.UP, i).offset(EnumFacing.SOUTH, 2).offset(EnumFacing.WEST, 2));
		}

		for (BlockPos currPos : blockPosList2) {
			world.setBlockState(currPos, Blocks.SNOW.getDefaultState());
		}

		for (BlockPos currPos : blockPosList3) {
			world.setBlockState(currPos, Blocks.PACKED_ICE.getDefaultState());
		}

		List<BlockPos> blockposListUpper = new ArrayList<>();
		List<BlockPos> blockposListLower = new ArrayList<>();

		blockposListLower.add(layer1.offset(EnumFacing.NORTH, 1).offset(EnumFacing.EAST));
		blockposListUpper.add(layer1.offset(EnumFacing.NORTH, 1));
		blockposListLower.add(layer1.offset(EnumFacing.UP, 1).offset(EnumFacing.NORTH, 1).offset(EnumFacing.WEST));
		blockposListUpper.add(layer1.offset(EnumFacing.UP, 1).offset(EnumFacing.WEST, 1));
		blockposListLower.add(layer1.offset(EnumFacing.UP, 2).offset(EnumFacing.SOUTH, 1).offset(EnumFacing.WEST));
		blockposListUpper.add(layer1.offset(EnumFacing.UP, 2).offset(EnumFacing.SOUTH, 1));
		blockposListLower.add(layer1.offset(EnumFacing.UP, 3).offset(EnumFacing.SOUTH, 1).offset(EnumFacing.EAST));
		blockposListUpper.add(layer1.offset(EnumFacing.UP, 3).offset(EnumFacing.EAST));

		List<BlockPos> blockposListLowerToAdd = new ArrayList<>();
		List<BlockPos> blockposListUpperToAdd = new ArrayList<>();

		for (BlockPos currPos : blockposListLower) {
			blockposListLowerToAdd.add(currPos.offset(EnumFacing.UP, 4));
		}

		for (BlockPos currPos : blockposListUpper) {
			blockposListUpperToAdd.add(currPos.offset(EnumFacing.UP, 4));
		}

		blockposListLower.addAll(blockposListLowerToAdd);
		blockposListUpper.addAll(blockposListUpperToAdd);

		blockposListLower.add(layer1.offset(EnumFacing.UP, 8).offset(EnumFacing.NORTH, 1).offset(EnumFacing.EAST));
		blockposListUpper.add(layer1.offset(EnumFacing.UP, 8).offset(EnumFacing.NORTH, 1));
		blockposListLower.add(layer1.offset(EnumFacing.UP, 9).offset(EnumFacing.NORTH, 1).offset(EnumFacing.WEST));
		blockposListUpper.add(layer1.offset(EnumFacing.UP, 9).offset(EnumFacing.SOUTH, 1).offset(EnumFacing.WEST));
		blockposListUpper.add(layer1.offset(EnumFacing.UP, 9).offset(EnumFacing.WEST));
		blockposListUpper.add(layer1.offset(EnumFacing.UP, 9));
		blockposListUpper.add(layer1.offset(EnumFacing.UP, 9).offset(EnumFacing.SOUTH).offset(EnumFacing.WEST));
		blockposListUpper.add(layer1.offset(EnumFacing.UP, 9).offset(EnumFacing.SOUTH));

		/// roof

		BlockPos topcenter = layer1.offset(EnumFacing.UP, 12).offset(EnumFacing.NORTH, 2).offset(EnumFacing.WEST, 2);

		List<BlockPos> roof = new ArrayList<>();

		for (BlockPos currpos : BlockPos.getAllInBox(topcenter.add(0, 1, 0), topcenter.add(4, 1, 4))) {
			roof.add(currpos);
		}

		roof.add(topcenter.offset(EnumFacing.NORTH));
		roof.add(topcenter.offset(EnumFacing.NORTH).offset(EnumFacing.EAST));
		roof.add(topcenter.offset(EnumFacing.NORTH).offset(EnumFacing.EAST, 2));
		roof.add(topcenter.offset(EnumFacing.NORTH).offset(EnumFacing.EAST, 3));
		roof.add(topcenter.offset(EnumFacing.NORTH).offset(EnumFacing.EAST, 4));
		roof.add(topcenter.offset(EnumFacing.EAST, 4));
		roof.add(topcenter.offset(EnumFacing.WEST, 1));
		roof.add(topcenter.offset(EnumFacing.SOUTH, 1).offset(EnumFacing.WEST));
		roof.add(topcenter.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.WEST));
		roof.add(topcenter.offset(EnumFacing.SOUTH, 3).offset(EnumFacing.WEST));
		roof.add(topcenter.offset(EnumFacing.SOUTH, 4).offset(EnumFacing.WEST));
		roof.add(topcenter.offset(EnumFacing.SOUTH, 5));
		roof.add(topcenter.offset(EnumFacing.SOUTH, 5).offset(EnumFacing.EAST));
		roof.add(topcenter.offset(EnumFacing.SOUTH, 5).offset(EnumFacing.EAST, 2));
		roof.add(topcenter.offset(EnumFacing.SOUTH, 5).offset(EnumFacing.EAST, 3));
		roof.add(topcenter.offset(EnumFacing.SOUTH, 5).offset(EnumFacing.EAST, 4));
		roof.add(topcenter.offset(EnumFacing.EAST, 5));
		//exp
		roof.add(topcenter.offset(EnumFacing.SOUTH, 1).offset(EnumFacing.EAST, 5));
		roof.add(topcenter.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST, 5));
		roof.add(topcenter.offset(EnumFacing.SOUTH, 3).offset(EnumFacing.EAST, 5));
		roof.add(topcenter.offset(EnumFacing.SOUTH, 4).offset(EnumFacing.EAST, 5));

		for (BlockPos currpos : BlockPos.getAllInBox(topcenter.offset(EnumFacing.UP).offset(EnumFacing.SOUTH).offset(EnumFacing.EAST).add(0, 1, 0), topcenter.add(3, 3, 3))) {
			roof.add(currpos);
		}

		roof.add(topcenter.offset(EnumFacing.UP, 2).offset(EnumFacing.SOUTH, 2));
		roof.add(topcenter.offset(EnumFacing.UP, 2).offset(EnumFacing.EAST, 2));
		roof.add(topcenter.offset(EnumFacing.UP, 2).offset(EnumFacing.EAST, 4).offset(EnumFacing.SOUTH, 2));
		roof.add(topcenter.offset(EnumFacing.UP, 2).offset(EnumFacing.EAST, 2).offset(EnumFacing.SOUTH, 4));
		roof.add(topcenter.offset(EnumFacing.UP, 4).offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST, 2));
		roof.add(topcenter.offset(EnumFacing.UP, 5).offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST, 2));

		for (BlockPos currPos : roof) {
			world.setBlockState(currPos, Blocks.PACKED_ICE.getDefaultState());
		}

		for (BlockPos currPos : blockposListLower) {
			world.setBlockState(currPos, AncientSpellcraftBlocks.SNOW_SLAB.getDefaultState().withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM));
		}
		for (BlockPos currPos : blockposListUpper) {
			world.setBlockState(currPos, AncientSpellcraftBlocks.SNOW_SLAB.getDefaultState().withProperty(HALF, BlockSlab.EnumBlockHalf.TOP));
		}

		//door
		//		world.setBlockState(pos.offset(EnumFacing.UP).offset(EnumFacing.SOUTH, 2), Blocks.AIR.getDefaultState());
		world.setBlockState(pos.offset(EnumFacing.UP).offset(EnumFacing.SOUTH, 2), AncientSpellcraftBlocks.ICE_DOOR.getDefaultState().withProperty(BlockIceDoor.HALF, BlockIceDoor.EnumDoorHalf.LOWER));
		//		world.setBlockState(pos.offset(EnumFacing.UP,2).offset(EnumFacing.SOUTH, 2), Blocks.AIR.getDefaultState());
		world.setBlockState(pos.offset(EnumFacing.UP, 2).offset(EnumFacing.SOUTH, 2), AncientSpellcraftBlocks.ICE_DOOR.getDefaultState().withProperty(BlockIceDoor.HALF, BlockIceDoor.EnumDoorHalf.UPPER));

		// windows
		List<BlockPos> windows = new ArrayList<>();
		windows.add(pos.offset(EnumFacing.UP, 3).offset(EnumFacing.NORTH, 2));
		windows.add(pos.offset(EnumFacing.UP, 3).offset(EnumFacing.NORTH, 2));
		windows.add(pos.offset(EnumFacing.UP, 7).offset(EnumFacing.NORTH, 2));
		windows.add(pos.offset(EnumFacing.UP, 12).offset(EnumFacing.NORTH, 3));

		windows.add(pos.offset(EnumFacing.UP, 12).offset(EnumFacing.SOUTH, 3));
		windows.add(pos.offset(EnumFacing.UP, 12).offset(EnumFacing.EAST, 3));
		windows.add(pos.offset(EnumFacing.UP, 12).offset(EnumFacing.WEST, 3));
		for (BlockPos window : windows) {
			world.setBlockState(window, Blocks.STAINED_GLASS_PANE.getDefaultState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.LIGHT_BLUE));
		}
		return true;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}

}
