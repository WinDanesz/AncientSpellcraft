package com.windanesz.ancientspellcraft.block;

import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockMushroomWither extends BlockMagicMushroom {

	public BlockMushroomWither() {
		super();
	}

	@Override
	public boolean applyHarmfulEffect(World world,
			@Nullable Block block, BlockPos pos, DamageSource source, float damage, IBlockState state,
			@Nullable EntityLivingBase caster, EntityLivingBase target, float potency) {

		if (!MagicDamage.isEntityImmune(MagicDamage.DamageType.WITHER, target)) {

			target.addPotionEffect(new PotionEffect(MobEffects.WITHER, POTION_DURATION, 1));

			if (!world.isRemote) {
				EntityUtils.attackEntityWithoutKnockback(target, source, damage);
			} else {
				ParticleBuilder.create(ParticleBuilder.Type.CLOUD).pos(pos.getX() + 0.5f, pos.getY() + 0.3f, pos.getZ() + 0.5f)
						.clr(44, 19, 61).scale(1.1f).face(EnumFacing.UP).shaded(true).spawn(world);
				ParticleBuilder.create(ParticleBuilder.Type.CLOUD).pos(pos.getX() + 0.5f, pos.getY() + 0.3f, pos.getZ() + 0.5f)
						.clr(50, 50, 50).scale(0.5f).fade(0x000000).shaded(true).spawn(world);
			}
		}

		return true;
	}
}
