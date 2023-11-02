package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.util.WizardArmourUtils;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ItemGlyphAuraArtefact extends ItemGlyphArtefact implements ITickableArtefact {
	public ItemGlyphAuraArtefact(EnumRarity rarity, Type type) {
		super(rarity, type);
	}

	@Override
	public void onWornTick(ItemStack itemstack, EntityLivingBase wearingEntity) {
		if (wearingEntity instanceof EntityPlayer && !wearingEntity.world.isRemote && wearingEntity.ticksExisted % 10 == 0) {

			EntityPlayer player = (EntityPlayer) wearingEntity;

			if (WizardArmourUtils.isWearingFullSet(player, null, ItemWizardArmour.ArmourClass.BATTLEMAGE)) {

				if (this == ASItems.charm_aura_alacrity) {
					getNearbyAllies(wearingEntity).forEach(e -> applyPotionIfNotActive(player, e, MobEffects.SPEED, 60, 0, false));
					applyPotionIfNotActive(player, player, MobEffects.SPEED, 60, 0, false);
				} else if (this == ASItems.charm_aura_hatred) {
					getNearbyAllies(wearingEntity).stream().filter(EntityLivingBase::isEntityUndead).forEach(e -> applyPotionIfNotActive(player, e, MobEffects.STRENGTH, 60, 0, false));
				} else if (this == ASItems.charm_aura_life) {
					getNearbyAllies(wearingEntity).forEach(e -> e.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 60, 0)));
					applyPotionIfNotActive(player, player, MobEffects.REGENERATION, 60, 0, false);
				} else if (this == ASItems.charm_aura_purity) {
					getNearbyEnemies(wearingEntity).stream().filter(EntityLivingBase::isEntityUndead).forEach(e -> applyPotionIfNotActive(player, e, MobEffects.SLOWNESS, 60, 0, false));
					getNearbyEnemies(wearingEntity).stream().filter(EntityLivingBase::isEntityUndead).forEach(e -> applyPotionIfNotActive(player, e, MobEffects.WEAKNESS, 60, 0, false));
				} else if (this == ASItems.charm_aura_warding) {
					getNearbyAllies(wearingEntity).forEach(e -> applyPotionIfNotActive(player, e, WizardryPotions.ward, 60, 0, false));
					applyPotionIfNotActive(player, player, WizardryPotions.ward, 60, 1, false);
				} else if (this == ASItems.charm_aura_wither) {
					getNearbyEnemies(wearingEntity).forEach(e -> applyPotionIfNotActive(player, e, MobEffects.WITHER, 60, 0, true));
				}
			}
		}

	}

	public static List<EntityLivingBase> getNearbyAllies(EntityLivingBase player) {
		List<EntityLivingBase> entitiesWithinRadius = EntityUtils.getEntitiesWithinRadius(10, player.posX, player.posY, player.posZ, player.world, EntityLivingBase.class);
		return entitiesWithinRadius.stream().filter(e -> e != player).filter(e -> AllyDesignationSystem.isAllied(player, e)).collect(Collectors.toList());
	}

	public static void applyPotionIfNotActive(EntityPlayer player, EntityLivingBase entity, Potion potion, int duration, int amplifier, boolean damaging) {
		//noinspection DataFlowIssue
		if (!entity.isPotionActive(potion) || entity.getActivePotionEffect(potion).getAmplifier() < amplifier) {
			if (damaging && entity.getRevengeTarget() != player) {
				EntityUtils.attackEntityWithoutKnockback(entity, MagicDamage.causeDirectMagicDamage(player, MagicDamage.DamageType.MAGIC), 0.01f);
			}
			entity.addPotionEffect(new PotionEffect(potion, duration, amplifier));
		}
	}

	public static List<EntityLivingBase> getNearbyEnemies(EntityLivingBase player) {
		List<EntityLivingBase> entitiesWithinRadius = EntityUtils.getEntitiesWithinRadius(10, player.posX, player.posY, player.posZ, player.world, EntityLivingBase.class);
		return entitiesWithinRadius.stream()
				.filter(e -> e != player)
				.filter(not(e -> e instanceof EntityAnimal))
				.filter(e -> !AllyDesignationSystem.isAllied(player, e))
				.collect(Collectors.toList());
	}

	public static <R> Predicate<R> not(Predicate<R> predicate) {
		return predicate.negate();
	}

}
