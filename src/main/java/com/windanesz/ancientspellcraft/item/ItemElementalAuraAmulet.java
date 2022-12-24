package com.windanesz.ancientspellcraft.item;

import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

import java.util.Random;

public class ItemElementalAuraAmulet extends ItemASArtefact implements ITickableArtefact {

	public ItemElementalAuraAmulet(EnumRarity rarity, Type type) {
		super(rarity, type);
	}

	@Override
	public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
		if (player.ticksExisted % 20 == 0) {
			// using a seeded random to have the same result in both sides for particles
			Random rand = new Random(player.ticksExisted);
			if (rand.nextInt(3) == 0) {

				World world = player.world;
				int effect = player.isPotionActive(WizardryPotions.static_aura) ? 1
						: player.isPotionActive(WizardryPotions.fireskin) ? 2
						: player.isPotionActive(WizardryPotions.ice_shroud) ? 3
						: 0;

				if (effect > 0) {
					for (EntityLivingBase target : EntityUtils.getEntitiesWithinRadius(5, player.posX, player.posY, player.posZ, world, EntityLivingBase.class)) {
						if (target == player || AllyDesignationSystem.isAllied(player, target)) {continue;}

						if (effect == 1) {
							if (!world.isRemote) {
								EntityUtils.attackEntityWithoutKnockback(target, MagicDamage.causeDirectMagicDamage(player,
										MagicDamage.DamageType.SHOCK, false), Spells.static_aura.getProperty(Spell.DAMAGE).floatValue());
								target.playSound(WizardrySounds.SPELL_STATIC_AURA_RETALIATE, 1.0F, world.rand.nextFloat() * 0.4F + 1.5F);
							} else {
								ParticleBuilder.create(ParticleBuilder.Type.LIGHTNING).entity(player).pos(0, player.height / 2, 0)
										.target(target).spawn(world);
								ParticleBuilder.spawnShockParticles(world, target.posX,
										target.posY + target.height / 2, target.posZ);
								break;
							}
						} else if (effect == 2) {
							if (!world.isRemote && !MagicDamage.isEntityImmune(MagicDamage.DamageType.FIRE, target)) {
								target.setFire(Spells.fire_breath.getProperty(Spell.BURN_DURATION).intValue() * 5);
							}
						} else if (effect == 3) {
							// Fake players cause problems
							if (!world.isRemote && !MagicDamage.isEntityImmune(MagicDamage.DamageType.FROST, target) && !(target instanceof FakePlayer)) {
								target.addPotionEffect(new PotionEffect(WizardryPotions.frost,
										Spells.ice_shroud.getProperty(Spell.EFFECT_DURATION).intValue(),
										Spells.ice_shroud.getProperty(Spell.EFFECT_STRENGTH).intValue()));
							}
						}
					}
				}
			}
		}
	}
}
