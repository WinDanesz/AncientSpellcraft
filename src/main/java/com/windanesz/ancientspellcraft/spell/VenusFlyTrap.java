package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.construct.EntityVenusFlyTrap;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class VenusFlyTrap extends SpellRay {

	// TODO: add an artefact which has a chance to trigger this?

	public VenusFlyTrap() {
		super(AncientSpellcraft.MODID, "venus_fly_trap", SpellActions.POINT_DOWN, false);
		this.addProperties(DAMAGE, EFFECT_DURATION);
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) {
		return true;
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {

		double d0 = Math.min(target.posY, caster.posY);
		double d1 = Math.max(target.posY, caster.posY) + 1.0D;
		float f = (float) MathHelper.atan2(target.posZ - caster.posZ, target.posX - caster.posX);
		spawnFangs(caster, f, d0, d1);

		return true;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		double d0 = Math.min(pos.getY(), caster.posY);
		double d1 = Math.max(pos.getY(), caster.posY) + 1.0D;
		float f = (float) MathHelper.atan2(pos.getZ() - caster.posZ, pos.getX() - caster.posX);

		spawnFangs(caster, f, d0, d1);
		return true;
	}

	@Override
	protected boolean onMiss(World world, @Nullable EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	/**
	 * adapted from {@link net.minecraft.entity.monster.EntityEvoker}
	 */
	private void spawnFangs(EntityLivingBase caster, float f, double d0, double d1) {
		for (int l = 0; l < 6; ++l) {
			if (l == 0) {
				continue;
			}
			double d2 = 1.25D * (double) (l + 1);
			int j = l;
			this.spawnFang(caster, caster.posX + (double) MathHelper.cos(f) * d2, caster.posZ + (double) MathHelper.sin(f) * d2, d0, d1, f, j);
		}

	}

	private void spawnFang(EntityLivingBase caster, double posX, double posZ, double p_190876_5_, double posY, float p_190876_9_, int p_190876_10_) {
		BlockPos blockpos = new BlockPos(posX, posY, posZ);
		boolean flag = false;
		double d0 = 0.0D;

		while (true) {
			if (!caster.world.isBlockNormalCube(blockpos, true) && caster.world.isBlockNormalCube(blockpos.down(), true)) {
				if (!caster.world.isAirBlock(blockpos)) {
					IBlockState iblockstate = caster.world.getBlockState(blockpos);
					AxisAlignedBB axisalignedbb = iblockstate.getCollisionBoundingBox(caster.world, blockpos);

					if (axisalignedbb != null) {
						d0 = axisalignedbb.maxY;
					}
				}

				flag = true;
				break;
			}

			blockpos = blockpos.down();

			if (blockpos.getY() < MathHelper.floor(p_190876_5_) - 1) {
				break;
			}
		}

		if (flag) {
			EntityVenusFlyTrap entityVenusFlyTrap = new EntityVenusFlyTrap(caster.world, posX, (double) blockpos.getY() + d0, posZ, p_190876_9_, p_190876_10_, caster);
			caster.world.spawnEntity(entityVenusFlyTrap);
		}
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
