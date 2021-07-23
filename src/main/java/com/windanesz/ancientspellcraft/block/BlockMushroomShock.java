package com.windanesz.ancientspellcraft.block;

import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BlockMushroomShock extends BlockMagicMushroom {

	public BlockMushroomShock() {
		super();
	}

	@Override
	public boolean applyHarmfulEffect(World world,
			@Nullable Block block, BlockPos pos, DamageSource source, float damage, IBlockState state,
			@Nullable EntityLivingBase caster, EntityLivingBase target, float potency) {

		if (!MagicDamage.isEntityImmune(MagicDamage.DamageType.SHOCK, target)) {
			// Secondary chaining effect
			Vec3d origin = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
			//			electrocute(world, caster, origin, target, damage);

			if (!world.isRemote && caster instanceof EntityPlayer) {

			} else {
				target.attackEntityFrom(MagicDamage.causeDirectMagicDamage(caster, MagicDamage.DamageType.SHOCK), damage);
			}

			if (world.isRemote) {

				ParticleBuilder.create(ParticleBuilder.Type.LIGHTNING).entity(caster)
						.pos(caster != null ? origin.subtract(caster.getPositionVector()) : origin).target(target).spawn(world);

				ParticleBuilder.spawnShockParticles(world, pos.getX() + 0.5f, pos.getY() + 0.2f, pos.getZ() + 0.5f);
			}

			if (true)
				return true;

			int secondaryRange = 5;
			int secondaryMaxTargets = 2;
			List<EntityLivingBase> secondaryTargets = EntityUtils.getLivingWithinRadius(
					secondaryRange, target.posX, target.posY + target.height / 2, target.posZ, world);

			secondaryTargets.remove(target);
			secondaryTargets.removeIf(e -> !EntityUtils.isLiving(e));
			secondaryTargets.removeIf(e -> e == caster);
			if (secondaryTargets.size() > secondaryMaxTargets)
				secondaryTargets = secondaryTargets.subList(0, secondaryMaxTargets);

			int index = 0;
			for (EntityLivingBase secondaryTarget : secondaryTargets) {
				if (!MagicDamage.isEntityImmune(MagicDamage.DamageType.SHOCK, secondaryTarget)) {
					if (!world.isRemote) {
						EntityUtils.attackEntityWithoutKnockback(secondaryTarget, source, damage * 0.5f);
					} else {
						ParticleBuilder.create(ParticleBuilder.Type.LIGHTNING).entity(caster)
								.pos(index == 0 ? target.getPositionVector().add(0, target.height / 2, 0) :
										secondaryTarget.getPositionVector().add(0, target.height / 2, 0)).target(secondaryTarget).spawn(world);

						ParticleBuilder.spawnShockParticles(world, target.posX + 0.5, target.posY + target.height / 2, target.posZ + 0.5);
					}

					index++;
				}
				// Tertiary chaining effect
			}

		}
		return true;
	}
}
