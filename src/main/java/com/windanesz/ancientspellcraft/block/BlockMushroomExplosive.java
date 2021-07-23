package com.windanesz.ancientspellcraft.block;

import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BlockMushroomExplosive extends BlockMagicMushroom {

	public BlockMushroomExplosive() {
		super();
	}

	@Override
	public boolean applyHarmfulEffect(World world,
			@Nullable Block block, BlockPos pos, DamageSource source, float damage, IBlockState state,
			@Nullable EntityLivingBase caster, EntityLivingBase target, float potency) {
		damage *= 2;
		// this is basically an altered EntityFirebomb.onImpact effect, Author: Electroblob
		if (target != null) {
			// affecting primary target
			if (!MagicDamage.isEntityImmune(MagicDamage.DamageType.FIRE, target))
				EntityUtils.attackEntityWithoutKnockback(target, source, damage);

			target.setFire(Spells.firebomb.getProperty(Spell.BURN_DURATION).intValue());
		}
		int blastMultiplier = 1;

		// Particle effect
		if (world.isRemote) {
			ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(pos.getX(), pos.getY(), pos.getZ()).scale(5 * blastMultiplier).clr(1, 0.6f, 0)
					.spawn(world);

			for (int i = 0; i < 60 * blastMultiplier; i++) {

				ParticleBuilder.create(ParticleBuilder.Type.MAGIC_FIRE, world.rand, pos.getX(), pos.getY(), pos.getZ(), 2 * blastMultiplier, false)
						.time(10 + world.rand.nextInt(4)).scale(2 + world.rand.nextFloat()).spawn(world);

				ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC, world.rand, pos.getX(), pos.getY(), pos.getZ(), 2 * blastMultiplier, false)
						.clr(1.0f, 0.2f + world.rand.nextFloat() * 0.4f, 0.0f).spawn(world);
			}

			world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0);
		}

		if (!world.isRemote) {
			// affecting nearby entities

			//			this.playSound(WizardrySounds.ENTITY_FIREBOMB_SMASH, 1.5F, target.world.rand.nextFloat() * 0.4F + 0.6F);
			//			this.playSound(WizardrySounds.ENTITY_FIREBOMB_FIRE, 1, 1);

			double range = Spells.firebomb.getProperty(Spell.BLAST_RADIUS).floatValue() * blastMultiplier;

			List<EntityLivingBase> targets = EntityUtils.getLivingWithinRadius(range, pos.getX(), pos.getY(), pos.getZ(), world);

			for (EntityLivingBase currTarget : targets) {
				if (currTarget != target && currTarget != caster && !MagicDamage.isEntityImmune(MagicDamage.DamageType.FIRE, currTarget)) {
					// Splash damage does not count as projectile damage
					EntityUtils.attackEntityWithoutKnockback(currTarget, source, damage);
					currTarget.setFire(4);
				}
			}
		}

		return true;
	}

	public static int getColorFromTile(Block block, int layer) {

		if (layer == 0) {
			return 0x10c7c1;
		}
		if (layer == 1) {
			return 0x10c7c1;
		}
		return 0;
	}

}
