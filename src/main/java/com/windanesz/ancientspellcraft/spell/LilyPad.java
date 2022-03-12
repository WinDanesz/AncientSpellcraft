package com.windanesz.ancientspellcraft.spell;

import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class LilyPad extends SpellRayAS {

	public LilyPad() {
		super("lily_pad", SpellActions.POINT, false);
		this.hitLiquids(true);
		this.ignoreUncollidables(false);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {

		if (caster == null) { return false; }

		if (true) {

			EnumFacing direction = caster.getHorizontalFacing();

			boolean flag = false;



				// Gets the coordinates of the nearest block intersection to the player's feet.
				// Remember that a block always takes the coordinates of its northwestern corner.

				int startPoint = direction.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? -1 : 0;

				for (int i = 0; i < 10 * modifiers.get(WizardryItems.range_upgrade); i++) {
					// If either a block gets placed or one has already been placed, flag is set to true.
					flag = placeLilyBlockIfPossible(world, pos.offset(EnumFacing.UP).offset(direction, startPoint + i)) || flag;
//					flag = placeLilyBlockIfPossible(world, pos.offset(EnumFacing.UP).offset(direction, startPoint + i)
//							// Moves the BlockPos minus one block perpendicular to direction.
//							.offset(EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.NEGATIVE, direction.rotateY().getAxis()))) || flag;
					i++;
				}

		} else if (world.getBlockState(pos).getBlock() == Blocks.WATER && BlockUtils.canBlockBeReplaced(world, pos.offset(EnumFacing.UP))
				&& BlockUtils.canPlaceBlock(caster, world, pos.offset(EnumFacing.UP))) {

			if (!world.isRemote) {
				world.setBlockState(pos.offset(EnumFacing.UP), Blocks.WATERLILY.getDefaultState());
			}

			return true;
		}

		return false;
	}

	private boolean placeLilyBlockIfPossible(World world, BlockPos pos) {
		if (world.getBlockState(pos.down()).getBlock() == Blocks.WATER && BlockUtils.canBlockBeReplaced(world, pos, true)) {
			if (!world.isRemote) {
				world.setBlockState(pos, Blocks.WATERLILY.getDefaultState());
			} else {
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5).scale(3.5f)
						.clr(111, 173, 24).time(20).spawn(world);
			}

			return true;
		}
		return false;
	}

	@Override
	protected boolean onMiss(World world, @Nullable EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}
}
