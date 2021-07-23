package com.windanesz.ancientspellcraft.block;

import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockMushroomIce extends BlockMagicMushroom {

	public BlockMushroomIce() {
		super();
	}

	@Override
	public boolean applyHarmfulEffect(World world,
			@Nullable Block block, BlockPos pos, DamageSource source, float damage, IBlockState state,
			@Nullable EntityLivingBase caster, EntityLivingBase target, float potency) {

		if (!MagicDamage.isEntityImmune(MagicDamage.DamageType.FROST, target)) {

			target.addPotionEffect(new PotionEffect(WizardryPotions.frost, POTION_DURATION, 1));
			EntityUtils.attackEntityWithoutKnockback(target, source, damage);
			return true;
		}
		return false;
	}
}
