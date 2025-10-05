
package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class SpellPlasmaCutter extends SpellRay {

	public SpellPlasmaCutter() {
		super(AncientSpellcraft.MODID, "plasma_cutter", SpellActions.POINT, true);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, @Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, @Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		if (!world.isRemote && caster instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) caster;
			IBlockState state = world.getBlockState(pos);
			   float hardness = state.getBlockHardness(world, pos);
			   if (hardness >= 0) {
				   // Determine the best tool for the block
				   String tool = state.getBlock().getHarvestTool(state);
				   float breakSpeed = 1.0f;
				   if (tool != null) {
					   net.minecraft.item.ItemStack toolStack;
					   switch (tool) {
						   case "pickaxe":
							   toolStack = new net.minecraft.item.ItemStack(net.minecraft.init.Items.DIAMOND_PICKAXE);
							   break;
						   case "shovel":
							   toolStack = new net.minecraft.item.ItemStack(net.minecraft.init.Items.DIAMOND_SHOVEL);
							   break;
						   case "axe":
							   toolStack = new net.minecraft.item.ItemStack(net.minecraft.init.Items.DIAMOND_AXE);
							   break;
						   default:
							   toolStack = net.minecraft.item.ItemStack.EMPTY;
					   }
					   if (!toolStack.isEmpty()) {
						   breakSpeed = toolStack.getDestroySpeed(state) * 5.0f;
					   } else {
						   breakSpeed = player.getDigSpeed(state, pos) * 5.0f;
					   }
				   } else {
					   breakSpeed = player.getDigSpeed(state, pos) * 5.0f;
				   }
				   // Scale breakSpeed with potency modifier
				   breakSpeed *= modifiers.get(SpellModifiers.POTENCY);
				   System.out.println("breakSpeed: " + breakSpeed );
				   // Improved scaling: linear with min/max clamp
				   final int minTicks = 5;
				   final int maxTicks = 20;
				   final double base = 20.0; // Adjust base for good spread
				   int ticksToBreak = (int) Math.ceil(Math.max(minTicks, Math.min(maxTicks, (hardness * base) / breakSpeed)));
				   System.out.println("ticksToBreak: " + ticksToBreak );
				   // Use a timer based on the player's ticksExisted and block position to avoid skipping
				   if ((player.ticksExisted + pos.hashCode()) % ticksToBreak == 0) {
					   world.destroyBlock(pos, true);
				   }
			   }
		}
		return true;
	}

	@Override
	protected boolean onMiss(World world, @Nullable EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		if (world.isRemote) spawnParticleRay(world, origin, direction, caster, 6);
		return false;
	}

	@Override
	protected void spawnParticleRay(World world, Vec3d origin, Vec3d direction, EntityLivingBase caster, double distance) {

		if (caster != null) {
			ParticleBuilder.create(ParticleBuilder.Type.BEAM).entity(caster).pos(origin.subtract(caster.getPositionVector()))
					.length(distance).clr(0x6a73b3)
					.scale(MathHelper.sin(caster.ticksExisted * 0.2f) * 0.1f + 1.4f).spawn(world);
		} else {
			ParticleBuilder.create(ParticleBuilder.Type.BEAM).pos(origin).target(origin.add(direction.scale(distance)))
					.clr(1, 0.6f + 0.3f * world.rand.nextFloat(), 0.2f)
					.scale(MathHelper.sin(Wizardry.proxy.getThePlayer().ticksExisted * 0.2f) * 0.1f + 1.4f).spawn(world);
		}
	}
}
