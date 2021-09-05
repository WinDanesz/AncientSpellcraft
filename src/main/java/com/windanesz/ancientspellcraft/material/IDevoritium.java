package com.windanesz.ancientspellcraft.material;

import com.windanesz.ancientspellcraft.entity.living.EntitySpiritBear;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import electroblob.wizardry.entity.living.EntityRemnant;
import electroblob.wizardry.entity.living.EntitySpiritWolf;
import electroblob.wizardry.entity.living.ISummonedCreature;
import electroblob.wizardry.util.EntityUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IDevoritium {

	int DEFAULT_SUMMON_DAMAGE = 3;

	default void onEntityWalkDelegate(World worldIn, BlockPos pos, Entity entityIn) {
		if (entityIn instanceof EntityLivingBase) {
			((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(AncientSpellcraftPotions.magical_exhaustion, 40, 2));
			damageSummonedCreature(entityIn, 1);
		}
	}

	default void damageSummonedCreature(Entity entityIn, float multiplier) {
		if (entityIn instanceof ISummonedCreature || entityIn instanceof EntitySpiritWolf || entityIn instanceof EntitySpiritBear) {
			EntityUtils.attackEntityWithoutKnockback(entityIn, DamageSource.GENERIC, multiplier > 0 ? (DEFAULT_SUMMON_DAMAGE * multiplier) : DEFAULT_SUMMON_DAMAGE);
		}
	}

	default void onEntityCollisionDelegate(World worldIn, BlockPos pos, IBlockState state, Entity entity) {
		if (entity instanceof EntityRemnant) {
			entity.addVelocity(0, 1, 0);
		}
	}

	default void hitEntityDelegate(Entity attacker, Entity target) {
		hitEntityDelegate(attacker, target, 0, 0);
	}

	default void hitEntityDelegate(Entity attacker, Entity target, int bonusAmplifier, int bonusDuration) {
		if (target instanceof EntityLivingBase) {
			EntityLivingBase entityLivingBase = (EntityLivingBase) target;
			if (!entityLivingBase.isPotionActive(AncientSpellcraftPotions.magical_exhaustion)) {
				entityLivingBase.addPotionEffect(new PotionEffect(AncientSpellcraftPotions.magical_exhaustion, 30 + bonusDuration, + bonusAmplifier));
			} else {
				int amplifier = entityLivingBase.getActivePotionEffect(AncientSpellcraftPotions.magical_exhaustion).getAmplifier();
				switch (amplifier) {
					case 0:
						entityLivingBase.addPotionEffect(new PotionEffect(AncientSpellcraftPotions.magical_exhaustion, 45 + bonusDuration, 1 + bonusAmplifier));
						break;
					case 1:
						entityLivingBase.addPotionEffect(new PotionEffect(AncientSpellcraftPotions.magical_exhaustion, 45 + bonusDuration, 2 + bonusAmplifier));
						break;
					case 2:
						entityLivingBase.addPotionEffect(new PotionEffect(AncientSpellcraftPotions.magical_exhaustion, 45 + bonusDuration, 3 + bonusAmplifier));
						break;
					default:
						break;
				}
			}
		}
	}

	default void onUpdateDelegate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (worldIn.getTotalWorldTime() % 20 == 0) {

			if (entityIn instanceof EntityPlayer) {
				if (isSelected || ((EntityPlayer) entityIn).getHeldItemOffhand() == stack) {
					((EntityPlayer) entityIn).addPotionEffect(new PotionEffect(AncientSpellcraftPotions.magical_exhaustion, 40, 2));
				} else {
					((EntityPlayer) entityIn).addPotionEffect(new PotionEffect(AncientSpellcraftPotions.magical_exhaustion, 40, 1));
				}
			}
		}
	}
}
