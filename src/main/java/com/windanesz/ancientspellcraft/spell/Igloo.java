package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Igloo extends SpellRay {

	private static Random rand = new Random();

	public Igloo() {
		super(AncientSpellcraft.MODID, "create_igloo", SpellActions.SUMMON, false);
		this.soundValues(0.5f, 1.1f, 0.2f);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		this.playSound(world, caster, ticksInUse, -1, modifiers);

		List<BlockPos> blockPosList = new ArrayList<>();
		BlockPos layer1Center = pos.offset(EnumFacing.UP);
		//		blockPosList.add(layer1Center.offset(EnumFacing.NORTH, 2));
		//		blockPosList.add(layer1Center.offset(EnumFacing.NORTH, 2).offset(EnumFacing.EAST));
		blockPosList.add(layer1Center.offset(EnumFacing.NORTH, 2).offset(EnumFacing.EAST, 2));
		//		blockPosList.add(layer1Center.offset(EnumFacing.NORTH).offset(EnumFacing.EAST, 2));
		//		blockPosList.add(layer1Center.offset(EnumFacing.EAST, 2));
		//		blockPosList.add(layer1Center.offset(EnumFacing.SOUTH).offset(EnumFacing.EAST, 2));
		blockPosList.add(layer1Center.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST, 2));
		//		blockPosList.add(layer1Center.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST));
		//		blockPosList.add(layer1Center.offset(EnumFacing.SOUTH, 2));
		//		blockPosList.add(layer1Center.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.WEST));
		blockPosList.add(layer1Center.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.WEST, 2));
		//		blockPosList.add(layer1Center.offset(EnumFacing.SOUTH).offset(EnumFacing.WEST, 2));
		//		blockPosList.add(layer1Center.offset(EnumFacing.WEST, 2));
		//		blockPosList.add(layer1Center.offset(EnumFacing.NORTH).offset(EnumFacing.WEST, 2));
		blockPosList.add(layer1Center.offset(EnumFacing.NORTH, 2).offset(EnumFacing.WEST, 2));
		//		blockPosList.add(layer1Center.offset(EnumFacing.NORTH, 2).offset(EnumFacing.WEST, 1));
		///

		blockPosList.add(layer1Center.offset(EnumFacing.NORTH, 3));
		blockPosList.add(layer1Center.offset(EnumFacing.NORTH, 3).offset(EnumFacing.EAST));
		blockPosList.add(layer1Center.offset(EnumFacing.NORTH, 3).offset(EnumFacing.EAST, 2));
		blockPosList.add(layer1Center.offset(EnumFacing.NORTH, 2).offset(EnumFacing.EAST, 3));
		blockPosList.add(layer1Center.offset(EnumFacing.NORTH, 1).offset(EnumFacing.EAST, 3));
		blockPosList.add(layer1Center.offset(EnumFacing.EAST, 3));
		blockPosList.add(layer1Center.offset(EnumFacing.SOUTH).offset(EnumFacing.EAST, 3));
		blockPosList.add(layer1Center.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST, 3));
		blockPosList.add(layer1Center.offset(EnumFacing.SOUTH, 3).offset(EnumFacing.EAST, 2));
		blockPosList.add(layer1Center.offset(EnumFacing.SOUTH, 3).offset(EnumFacing.EAST));
		//		blockPosList.add(layer1Center.offset(EnumFacing.SOUTH,3));
		blockPosList.add(layer1Center.offset(EnumFacing.SOUTH, 3).offset(EnumFacing.WEST));
		blockPosList.add(layer1Center.offset(EnumFacing.SOUTH, 3).offset(EnumFacing.WEST, 2));
		blockPosList.add(layer1Center.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.WEST, 3));
		blockPosList.add(layer1Center.offset(EnumFacing.SOUTH, 1).offset(EnumFacing.WEST, 3));
		blockPosList.add(layer1Center.offset(EnumFacing.WEST, 3));
		blockPosList.add(layer1Center.offset(EnumFacing.NORTH, 1).offset(EnumFacing.WEST, 3));
		blockPosList.add(layer1Center.offset(EnumFacing.NORTH, 2).offset(EnumFacing.WEST, 3));
		blockPosList.add(layer1Center.offset(EnumFacing.NORTH, 3).offset(EnumFacing.WEST, 2));
		blockPosList.add(layer1Center.offset(EnumFacing.NORTH, 3).offset(EnumFacing.WEST, 1));

		BlockPos layer2Center = layer1Center.offset(EnumFacing.UP);

		blockPosList.add(layer2Center.offset(EnumFacing.NORTH, 2));
		blockPosList.add(layer2Center.offset(EnumFacing.NORTH, 2).offset(EnumFacing.EAST));
		blockPosList.add(layer2Center.offset(EnumFacing.NORTH, 1).offset(EnumFacing.EAST, 2));
		blockPosList.add(layer2Center.offset(EnumFacing.EAST, 2));
		blockPosList.add(layer2Center.offset(EnumFacing.SOUTH, 1).offset(EnumFacing.EAST, 2));
		blockPosList.add(layer2Center.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST, 1));
		//		blockPosList.add(layer2Center.offset(EnumFacing.SOUTH, 2));
		blockPosList.add(layer2Center.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.WEST));
		blockPosList.add(layer2Center.offset(EnumFacing.SOUTH, 1).offset(EnumFacing.WEST, 2));
		blockPosList.add(layer2Center.offset(EnumFacing.SOUTH, 3).offset(EnumFacing.EAST, 1));
		blockPosList.add(layer2Center.offset(EnumFacing.SOUTH, 3).offset(EnumFacing.WEST, 1));

		blockPosList.add(layer2Center.offset(EnumFacing.WEST, 2));
		blockPosList.add(layer2Center.offset(EnumFacing.NORTH, 1).offset(EnumFacing.WEST, 2));
		blockPosList.add(layer2Center.offset(EnumFacing.NORTH, 2).offset(EnumFacing.WEST, 1));

		blockPosList.add(layer2Center.offset(EnumFacing.NORTH, 3));
		blockPosList.add(layer2Center.offset(EnumFacing.EAST, 3));
		//		blockPosList.add(layer2Center.offset(EnumFacing.SOUTH,3));
		blockPosList.add(layer2Center.offset(EnumFacing.WEST, 3));

		BlockPos layer3Center = layer2Center.offset(EnumFacing.UP);

		blockPosList.add(layer3Center.offset(EnumFacing.NORTH, 1));
		blockPosList.add(layer3Center.offset(EnumFacing.NORTH, 1).offset(EnumFacing.EAST, 1));
		blockPosList.add(layer3Center.offset(EnumFacing.EAST, 1));
		blockPosList.add(layer3Center.offset(EnumFacing.SOUTH, 1).offset(EnumFacing.EAST, 1));
		blockPosList.add(layer3Center.offset(EnumFacing.SOUTH, 1));
		blockPosList.add(layer3Center.offset(EnumFacing.SOUTH, 3));
		blockPosList.add(layer3Center.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST, 1));
		blockPosList.add(layer3Center.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.WEST, 1));
		blockPosList.add(layer3Center.offset(EnumFacing.SOUTH, 1).offset(EnumFacing.WEST, 1));
		blockPosList.add(layer3Center.offset(EnumFacing.WEST, 1));
		blockPosList.add(layer3Center.offset(EnumFacing.NORTH, 1).offset(EnumFacing.WEST, 1));

		blockPosList.add(layer3Center.offset(EnumFacing.NORTH, 2));
		blockPosList.add(layer3Center.offset(EnumFacing.EAST, 2));
		blockPosList.add(layer3Center.offset(EnumFacing.SOUTH, 2));
		blockPosList.add(layer3Center.offset(EnumFacing.WEST, 2));

		BlockPos layer4Center = layer3Center.offset(EnumFacing.UP);
		blockPosList.add(layer4Center);
		blockPosList.add(layer4Center.offset(EnumFacing.NORTH));
		blockPosList.add(layer4Center.offset(EnumFacing.EAST));
		blockPosList.add(layer4Center.offset(EnumFacing.SOUTH));
		blockPosList.add(layer4Center.offset(EnumFacing.WEST));

		for (BlockPos currPos : blockPosList) {
			world.setBlockState(currPos, Blocks.SNOW.getDefaultState());
			world.spawnParticle(EnumParticleTypes.SNOW_SHOVEL, (double) currPos.getX() + world.rand.nextDouble(), (double) currPos.getY() + world.rand.nextDouble() * 2.5D, (double) currPos.getZ() + world.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
		}
		// "snow carpet"
		List<BlockPos> blockPosFloor = new ArrayList<>();
		blockPosFloor.add(layer1Center);
		blockPosFloor.add(layer1Center.offset(EnumFacing.NORTH));
		blockPosFloor.add(layer1Center.offset(EnumFacing.NORTH, 2));
		blockPosFloor.add(layer1Center.offset(EnumFacing.EAST));
		blockPosFloor.add(layer1Center.offset(EnumFacing.EAST, 2));
		blockPosFloor.add(layer1Center.offset(EnumFacing.SOUTH));
		blockPosFloor.add(layer1Center.offset(EnumFacing.SOUTH, 2));
		blockPosFloor.add(layer1Center.offset(EnumFacing.SOUTH, 3));
		blockPosFloor.add(layer1Center.offset(EnumFacing.SOUTH, 4));
		blockPosFloor.add(layer1Center.offset(EnumFacing.SOUTH, 4).offset(EnumFacing.EAST));
		blockPosFloor.add(layer1Center.offset(EnumFacing.SOUTH, 4).offset(EnumFacing.WEST));
		blockPosFloor.add(layer1Center.offset(EnumFacing.WEST));
		blockPosFloor.add(layer1Center.offset(EnumFacing.WEST, 2));
		blockPosFloor.add(layer1Center.offset(EnumFacing.NORTH).offset(EnumFacing.EAST));
		blockPosFloor.add(layer1Center.offset(EnumFacing.NORTH, 2).offset(EnumFacing.EAST));
		blockPosFloor.add(layer1Center.offset(EnumFacing.NORTH).offset(EnumFacing.EAST, 2));
		blockPosFloor.add(layer1Center.offset(EnumFacing.SOUTH).offset(EnumFacing.EAST));
		blockPosFloor.add(layer1Center.offset(EnumFacing.SOUTH).offset(EnumFacing.EAST, 2));
		blockPosFloor.add(layer1Center.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST));
		blockPosFloor.add(layer1Center.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.WEST));
		blockPosFloor.add(layer1Center.offset(EnumFacing.SOUTH).offset(EnumFacing.WEST));
		blockPosFloor.add(layer1Center.offset(EnumFacing.SOUTH).offset(EnumFacing.WEST, 2));
		blockPosFloor.add(layer1Center.offset(EnumFacing.NORTH).offset(EnumFacing.WEST));
		blockPosFloor.add(layer1Center.offset(EnumFacing.NORTH).offset(EnumFacing.WEST, 2));
		blockPosFloor.add(layer1Center.offset(EnumFacing.NORTH, 2).offset(EnumFacing.WEST));

		for (BlockPos currPos : blockPosFloor) {
			world.setBlockState(currPos, Blocks.SNOW_LAYER.getDefaultState());
		}

		return true;
	}

	@Override
	protected boolean onMiss(World world, @Nullable EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
