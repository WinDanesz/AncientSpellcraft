package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftSounds;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockMushroomHeal extends BlockMagicMushroom {

	public BlockMushroomHeal() {
		super();
	}

	@Override
	public boolean applyBeneficialEffect(World world,
			@Nullable Block block, BlockPos pos, DamageSource source, float damage, IBlockState state,
			@Nullable EntityLivingBase caster, EntityLivingBase target, float potency) {
		if (target.getHealth() < target.getMaxHealth()) {
			if (!world.isRemote) {
				target.heal(1.5f * potency);
			} else {
				spawnRedHealingParticles(world, target);
			}
			target.playSound(AncientSpellcraftSounds.ENTITY_HEALING_HEATH_HEALS, 0.9F, 1.2F / (world.rand.nextFloat() * 0.2F + 0.9F));
			return true;
		}
		return false;
	}

	@Override
	public boolean applyHarmfulEffect(World world,
			@Nullable Block block, BlockPos pos, DamageSource source, float damage, IBlockState state,
			@Nullable EntityLivingBase caster, EntityLivingBase target, float potency) {
		if (target.isEntityUndead()) {

			EntityUtils.attackEntityWithoutKnockback(target, source, damage * 2f);
			return true;
		}
		// living entities are not affected
		return false;
	}

	/**
	 * Same as {@link ParticleBuilder#spawnHealParticles(net.minecraft.world.World, net.minecraft.entity.EntityLivingBase)}, except it's red!
	 */
	public static void spawnRedHealingParticles(World world, EntityLivingBase entity) {

		for (int i = 0; i < 10; i++) {
			double x = entity.posX + world.rand.nextDouble() * 2 - 1;
			double y = entity.posY + entity.getEyeHeight() - 0.5 + world.rand.nextDouble();
			double z = entity.posZ + world.rand.nextDouble() * 2 - 1;
			ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).vel(0, 0.1, 0).clr(245, 47, 47).spawn(world);
		}

		ParticleBuilder.create(ParticleBuilder.Type.BUFF).entity(entity).clr(245, 47, 47).spawn(world);
	}
}
