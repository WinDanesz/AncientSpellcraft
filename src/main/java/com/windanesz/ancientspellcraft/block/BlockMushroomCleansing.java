package com.windanesz.ancientspellcraft.block;

import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class BlockMushroomCleansing extends BlockMagicMushroom {

	public BlockMushroomCleansing() {
		super();
	}

	@Override
	public boolean applyBeneficialEffect(World world,
			@Nullable Block block, BlockPos pos, DamageSource source, float damage, IBlockState state,
			@Nullable EntityLivingBase caster, EntityLivingBase target, float potency) {

		if (target != null) {
			if (!target.getActivePotionEffects().isEmpty()) {

				ItemStack milk = new ItemStack(Items.MILK_BUCKET);

				boolean flag = false;

				for (PotionEffect effect : new ArrayList<>(caster.getActivePotionEffects())) { // Get outta here, CMEs
					// The PotionEffect version (as opposed to Potion) does not call cleanup callbacks
					if (effect.isCurativeItem(milk)) {
						caster.removePotionEffect(effect.getPotion());
						flag = true;
					}
				}

				if (world.isRemote) {
					for (int i = 0; i < 10; i++) {
						double x = target.posX + world.rand.nextDouble() * 2 - 1;
						double y = target.posY + target.getEyeHeight() - 0.5 + world.rand.nextDouble();
						double z = target.posZ + world.rand.nextDouble() * 2 - 1;
						ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).vel(0, 0.1, 0).clr(0.8f, 0.8f, 1).spawn(world);
					}

					ParticleBuilder.create(ParticleBuilder.Type.BUFF).entity(target).clr(0.8f, 0.8f, 1).spawn(world);
				}
				return flag;
			}

		}
		return true;
	}


	@Override
	public boolean applyHarmfulEffect(World world,
			@Nullable Block block, BlockPos pos, DamageSource source, float damage, IBlockState state,
			@Nullable EntityLivingBase caster, EntityLivingBase target, float potency) {
		return true;
	}

}
