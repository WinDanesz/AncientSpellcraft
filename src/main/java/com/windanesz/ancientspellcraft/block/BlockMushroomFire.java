package com.windanesz.ancientspellcraft.block;

import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockMushroomFire extends BlockMagicMushroom {

	public BlockMushroomFire() {
		super();
	}

	@Override
	public boolean applyHarmfulEffect(World world,
			@Nullable Block block, BlockPos pos, DamageSource source, float damage, IBlockState state,
			@Nullable EntityLivingBase caster, EntityLivingBase target, float potency) {

		if (!MagicDamage.isEntityImmune(MagicDamage.DamageType.FIRE, target)) {
			if (!world.isRemote) {
				EntityUtils.attackEntityWithoutKnockback(target, source, damage);
				// burns for 2x longer than regular potion effects would apply
				target.setFire(POTION_DURATION / 10);
			} else {
				ParticleBuilder.create(ParticleBuilder.Type.MAGIC_FIRE, world.rand, pos.getX() + 0.5f, pos.getY() + 1f, pos.getZ() + 0.5f, 1.2f, false)
						.time(10).scale(1.5f).spawn(world);
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).scale(2).time(15).pos(pos.getX() + 0.5f, pos.getY() + 0.3f, pos.getZ() + 0.5f).clr(1, 0.6f, 0)
						.spawn(world);
			}
		}

		return true;
	}
}
