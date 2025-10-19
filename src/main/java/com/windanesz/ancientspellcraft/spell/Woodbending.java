package com.windanesz.ancientspellcraft.spell;

import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nullable;

public class Woodbending extends SpellRayAS {

	public Woodbending() {
		super("woodbending", SpellActions.POINT, true);
		this.particleSpacing = 0.3;
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, @Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return true;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, @Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		if (!world.isRemote && ticksInUse % 10 == 0) {
			BlockPos placePos = pos.offset(side);
			IBlockState existingState = world.getBlockState(placePos);
			if (existingState.getBlock().isReplaceable(world, placePos) && BlockUtils.canPlaceBlock(caster, world, placePos)) {
				// Determine the axis based on the side (opposite of targeted block face)
				BlockLog.EnumAxis axis;
				if (side == EnumFacing.UP || side == EnumFacing.DOWN) {
					axis = BlockLog.EnumAxis.Y;
				} else if (side == EnumFacing.EAST || side == EnumFacing.WEST) {
					axis = BlockLog.EnumAxis.X;
				} else {
					axis = BlockLog.EnumAxis.Z;
				}
				
				// Get biome-specific wood type
				IBlockState logState = getBiomeWoodType(world, placePos, axis);
				world.setBlockState(placePos, logState);
			}
		}
		return true;
	}

	/**
	 * Returns the appropriate wood type based on the biome
	 */
	private IBlockState getBiomeWoodType(World world, BlockPos pos, BlockLog.EnumAxis axis) {
		Biome biome = world.getBiome(pos);
		
		// Birch biomes
		if (biome == Biomes.BIRCH_FOREST || biome == Biomes.BIRCH_FOREST_HILLS || biome == Biomes.MUTATED_BIRCH_FOREST || biome == Biomes.MUTATED_BIRCH_FOREST_HILLS) {
			return Blocks.LOG.getDefaultState()
					.withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.BIRCH)
					.withProperty(BlockLog.LOG_AXIS, axis);
		}
		
		// Acacia biomes
		if (biome == Biomes.SAVANNA || biome == Biomes.SAVANNA_PLATEAU || biome == Biomes.MUTATED_SAVANNA || biome == Biomes.MUTATED_SAVANNA_ROCK) {
			return Blocks.LOG2.getDefaultState()
					.withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.ACACIA)
					.withProperty(BlockLog.LOG_AXIS, axis);
		}
		
		// Dark Oak biomes
		if (biome == Biomes.ROOFED_FOREST || biome == Biomes.MUTATED_ROOFED_FOREST) {
			return Blocks.LOG2.getDefaultState()
					.withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.DARK_OAK)
					.withProperty(BlockLog.LOG_AXIS, axis);
		}
		
		// Jungle biomes
		if (biome == Biomes.JUNGLE || biome == Biomes.JUNGLE_HILLS || biome == Biomes.JUNGLE_EDGE || 
			biome == Biomes.MUTATED_JUNGLE || biome == Biomes.MUTATED_JUNGLE_EDGE) {
			return Blocks.LOG.getDefaultState()
					.withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE)
					.withProperty(BlockLog.LOG_AXIS, axis);
		}
		
		// Spruce/Taiga biomes
		if (biome == Biomes.TAIGA || biome == Biomes.TAIGA_HILLS || biome == Biomes.COLD_TAIGA || biome == Biomes.COLD_TAIGA_HILLS ||
			biome == Biomes.REDWOOD_TAIGA || biome == Biomes.REDWOOD_TAIGA_HILLS || biome == Biomes.MUTATED_TAIGA ||
			biome == Biomes.MUTATED_TAIGA_COLD || biome == Biomes.MUTATED_REDWOOD_TAIGA || biome == Biomes.MUTATED_REDWOOD_TAIGA_HILLS) {
			return Blocks.LOG.getDefaultState()
					.withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.SPRUCE)
					.withProperty(BlockLog.LOG_AXIS, axis);
		}
		
		// Default to Oak for all other biomes
		return Blocks.LOG.getDefaultState()
				.withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.OAK)
				.withProperty(BlockLog.LOG_AXIS, axis);
	}

	@Override
	protected boolean onMiss(World world, @Nullable EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return true;
	}

	@Override
	protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
		if (world.rand.nextInt(10) == 0) {
			ParticleBuilder.create(ParticleBuilder.Type.LEAF).pos(x, y, z).vel(vx, vy, vz).clr(0, 0.8f, 0).spawn(world);
		}
	}
}
