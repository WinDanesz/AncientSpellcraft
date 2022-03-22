package com.windanesz.ancientspellcraft.spell;

import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.BlockVine;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class Beanstalk extends SpellRayAS {

	public static final String MAX_HEIGHT_IN_BLOCKS = "max_height_in_blocks";

	public Beanstalk() {
		super("beanstalk", SpellActions.POINT, true);
		addProperties(MAX_HEIGHT_IN_BLOCKS);
		soundValues(0.7f, 1f, 1f);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {

		if (side.getAxis().isHorizontal()) {

			if (ticksInUse % 10 == 0) {

				int currOffsetUp = ticksInUse / 10;
				int maxHeight = getProperty(MAX_HEIGHT_IN_BLOCKS).intValue();

				if (currOffsetUp < maxHeight) {
					boolean isVine = false;

					BlockPos position = pos.offset(EnumFacing.UP, currOffsetUp).offset(side);

					isVine = world.getBlockState(position).getBlock() == Blocks.VINE;

					if (!world.isRemote && !world.isAirBlock(pos.offset(EnumFacing.UP, currOffsetUp)) && !isVine && BlockUtils.canBlockBeReplaced(world, position) && BlockUtils.canPlaceBlock(caster, world, position)) {

						PropertyBool facing;
						switch (side) {
							case EAST:
								facing = BlockVine.WEST;
								break;
							case WEST:
								facing = BlockVine.EAST;
								break;
							case SOUTH:
								facing = BlockVine.NORTH;
								break;
							case NORTH:
								facing = BlockVine.SOUTH;
								break;
							default:
								facing = BlockVine.NORTH;
						}

						world.setBlockState(position, Blocks.VINE.getDefaultState().withProperty(facing, true));
					}
				}
			}

			return true;
		}
		return false;
	}

	@Override
	protected boolean onMiss(World world, @Nullable EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
		ParticleBuilder.create(ParticleBuilder.Type.LEAF).pos(x, y, z).vel(vx, vy, vz).collide(true).spawn(world);
	}
}
