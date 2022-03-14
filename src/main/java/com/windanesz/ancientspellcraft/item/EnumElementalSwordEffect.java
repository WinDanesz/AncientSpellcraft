package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.block.BlockMagicMushroom;
import com.windanesz.ancientspellcraft.entity.living.EntityAnimatedItem;
import com.windanesz.ancientspellcraft.entity.projectile.EntitySafeIceShard;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASPotions;
import com.windanesz.ancientspellcraft.spell.Animate;
import com.windanesz.ancientspellcraft.spell.Runeword;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.spell.Disintegration;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum EnumElementalSwordEffect {

	MAGIC(Element.MAGIC) {
		@Override
		void onUpdateEffect(ItemStack stack, World world, Entity entity, int slot, boolean isHeld) {

		}

		@Override
		void lesserPowerOnEntityHit(ItemStack stack, EntityLivingBase target, EntityLivingBase wielder, boolean charged) {
		}

		@Override
		void greaterPowerOnEntityHit(ItemStack stack, EntityLivingBase target, EntityLivingBase wielder) {

		}
	},

	FIRE(Element.FIRE) {
		@Override
		void onUpdateEffect(ItemStack stack, World world, Entity entity, int slot, boolean isHeld) {

		}

		@Override
		void lesserPowerOnEntityHit(ItemStack stack, EntityLivingBase target, EntityLivingBase wielder, boolean charged) {
			target.setFire(3);

			// a chance to spawn embers
			if (AncientSpellcraft.rand.nextDouble() < 0.2 || target.getHealth() <= 0 && AncientSpellcraft.rand.nextDouble() < 0.4) {
				Disintegration.spawnEmbers(target.world, wielder, target, 6);
			}
		}

		@Override
		void greaterPowerOnEntityHit(ItemStack stack, EntityLivingBase target, EntityLivingBase wielder) {

			// Nether special
			wielder.addPotionEffect(new PotionEffect(WizardryPotions.fireskin, 160, 0));
			wielder.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 160, 0));

			if (target.isBurning()) {
				target.addPotionEffect(new PotionEffect(ASPotions.soul_scorch, 100, 0));
			}
		}
	},

	ICE(Element.ICE) {
		@Override
		void onUpdateEffect(ItemStack stack, World world, Entity entity, int slot, boolean isHeld) {}

		@Override
		void lesserPowerOnEntityHit(ItemStack stack, EntityLivingBase target, EntityLivingBase wielder, boolean charged) {
			int duration = 40;
			int amplifier = 0;

			if (target.isPotionActive(WizardryPotions.frost)) {

				PotionEffect effect = target.getActivePotionEffect(WizardryPotions.frost);
				if (effect != null) {
					duration = effect.getDuration() + duration;
					amplifier = effect.getAmplifier();

					// 20% chance to add to the current amplifier
					if (target.world.rand.nextDouble() < 0.2 && amplifier < 3) { amplifier++; }

				}
			}

			if (AncientSpellcraft.rand.nextDouble() < 0.25f && target.getHealth() == 0f && target.isPotionActive(WizardryPotions.frost)) {
				for (int i = 0; i < 8; i++) {
					double dx = AncientSpellcraft.rand.nextDouble() - 0.5;
					double dy = AncientSpellcraft.rand.nextDouble() - 0.5;
					double dz = AncientSpellcraft.rand.nextDouble() - 0.5;
					EntitySafeIceShard iceshard = new EntitySafeIceShard(target.world);
					iceshard.setPosition(target.posX + dx + Math.signum(dx) * target.width,
							target.posY + target.height / 2 + dy,
							target.posZ + dz + Math.signum(dz) * target.width);
					iceshard.motionX = dx * 1.5;
					iceshard.motionY = dy * 1.5;
					iceshard.motionZ = dz * 1.5;
					iceshard.setCaster(wielder);
					target.world.spawnEntity(iceshard);
				}
			}
			target.addPotionEffect(new PotionEffect(WizardryPotions.frost, duration, amplifier));
		}

		@Override
		void greaterPowerOnEntityHit(ItemStack stack, EntityLivingBase target, EntityLivingBase wielder) {

			for (EntityLivingBase currTarget : EntityUtils.getEntitiesWithinRadius(4, target.posX, target.posY, target.posZ, target.world, EntityLivingBase.class)) {

				if (currTarget == wielder || currTarget == target || AllyDesignationSystem.isAllied(wielder, currTarget)) {
					continue;
				}

				EntityUtils.attackEntityWithoutKnockback(currTarget, MagicDamage.causeDirectMagicDamage(wielder, MagicDamage.DamageType.FROST), 3.5f);

				currTarget.addPotionEffect(new PotionEffect(WizardryPotions.frost, 60, 0));

				double angle = (getAngleBetweenEntities(wielder, currTarget) + 90) * Math.PI / 180;
				double distance = wielder.getDistance(currTarget) - 4;
				currTarget.motionX += Math.min(1 / (distance * distance), 1) * -1 * Math.cos(angle);
				currTarget.motionZ += Math.min(1 / (distance * distance), 1) * -1 * Math.sin(angle);
			}
		}
	},

	LIGHTNING(Element.LIGHTNING) {
		@Override
		void onUpdateEffect(ItemStack stack, World world, Entity entity, int slot, boolean isHeld) {
			if (world.getTotalWorldTime() % 20 == 0 && entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				if (player.onGround && hasPosChanged(player)) {
					if (ItemBattlemageSword.hasManaStorage(stack) && !((IManaStoringItem) stack.getItem()).isManaFull(stack)) {
						((IManaStoringItem) stack.getItem()).rechargeMana(stack, 1);
					} else if (player.getHeldItemOffhand().getItem() instanceof IManaStoringItem) {
						((IManaStoringItem) player.getHeldItemOffhand().getItem()).rechargeMana(player.getHeldItemOffhand(), 1);
					}
				}
			}
		}

		@Override
		void lesserPowerOnEntityHit(ItemStack stack, EntityLivingBase target, EntityLivingBase wielder, boolean charged) {
			List<EntityLivingBase> secondaryTargets = EntityUtils.getLivingWithinRadius(
					4, target.posX, target.posY + target.height / 2,
					target.posZ, wielder.world);

			secondaryTargets.stream()
					.filter(entity -> !entity.equals(target))
					.filter(EntityUtils::isLiving)
					.filter(e -> AllyDesignationSystem.isValidTarget(wielder, e))
					.limit(3)
					.forEach(secondaryTarget -> electrocute(target.world, wielder,
							target.getPositionVector().add(0, target.height / 2, 0),
							secondaryTarget,
							4,
							0));
		}

		@Override
		void greaterPowerOnEntityHit(ItemStack stack, EntityLivingBase target, EntityLivingBase wielder) {
			int amount = 60;
			if (ItemBattlemageSword.hasManaStorage(stack) && !((IManaStoringItem) stack.getItem()).isManaFull(stack)) {
				((IManaStoringItem) stack.getItem()).rechargeMana(stack, amount);
			} else if (wielder.getHeldItemOffhand().getItem() instanceof IManaStoringItem) {
				((IManaStoringItem) wielder.getHeldItemOffhand().getItem()).rechargeMana(wielder.getHeldItemOffhand(), amount);
			}
		}
	},

	NECROMANCY(Element.NECROMANCY) {
		@Override
		void onUpdateEffect(ItemStack stack, World world, Entity entity, int slot, boolean isHeld) {
		}

		@Override
		void lesserPowerOnEntityHit(ItemStack stack, EntityLivingBase target, EntityLivingBase wielder, boolean charged) {
			target.addPotionEffect(new PotionEffect(MobEffects.WITHER, 60, 1));
			wielder.heal(0.5f);
			if (target.getHealth() == 0f) {

				wielder.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 200, 0));
				wielder.addPotionEffect(new PotionEffect(MobEffects.SPEED, 100, 0));
			}
		}

		@Override
		void greaterPowerOnEntityHit(ItemStack stack, EntityLivingBase target, EntityLivingBase wielder) {

			EntityUtils.attackEntityWithoutKnockback(target, MagicDamage.causeDirectMagicDamage(wielder, MagicDamage.DamageType.WITHER), 2.5f);
			wielder.heal(1.5f);
			if (wielder instanceof EntityPlayer) {
				((EntityPlayer) wielder).getFoodStats().addStats(2, 0.1F);
			}

			if (AncientSpellcraft.rand.nextDouble() < 0.3) {
				if (wielder instanceof EntityPlayer && ItemGlyphArtefact.isArtefactActive((EntityPlayer) wielder, ASItems.charm_glyph_leeching)) {

					// get the beneficial effects only and only those which has a rather limited duration as potions with a very long lifetime are usually stuff like abilities and such
					Map<Potion, PotionEffect> beneficialPotions = target.getActivePotionMap()
							.entrySet().stream()

							// isBeneficial() is client-side only!
							.filter(p -> !p.getKey().isBadEffect()) // filter for beneficial potions
							.filter(p -> p.getValue().getDuration() <= 12000) // 10 <= minutes
							.filter(p -> p.getValue().getAmplifier() < 3)
							.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

					if (!beneficialPotions.isEmpty()) {
						// get a random potion
						List<PotionEffect> effects = new ArrayList<>(beneficialPotions.values());

						if (!effects.isEmpty()) { // this should probably never happen anyways
							PotionEffect potionToSteal = effects.get(AncientSpellcraft.rand.nextInt(effects.size()));
							wielder.addPotionEffect(potionToSteal);
							target.removePotionEffect(potionToSteal.getPotion());
						}
					}
				}
			}
		}
	},

	EARTH(Element.EARTH) {
		@Override
		void onUpdateEffect(ItemStack stack, World world, Entity entity, int slot, boolean isHeld) {

		}

		@Override
		void lesserPowerOnEntityHit(ItemStack stack, EntityLivingBase target, EntityLivingBase wielder, boolean charged) {
			int duration = 80;
			int amplifier = 0;

			if (target.isPotionActive(MobEffects.POISON)) {

				PotionEffect effect = target.getActivePotionEffect(MobEffects.POISON);
				if (effect != null) {
					duration = (int) (effect.getDuration() * 0.1 + duration);
					amplifier = effect.getAmplifier();

					// 20% chance to add to the current amplifier
					if (target.world.rand.nextDouble() < 0.2 && amplifier < 2) { amplifier++; }

				}
			}

			target.addPotionEffect(new PotionEffect(MobEffects.POISON, duration, amplifier));

			// chance to grow a mushroom
			if (!wielder.world.isRemote && AncientSpellcraft.rand.nextDouble() < 0.5) {
				BlockPos pos = BlockUtils.findNearbyFloorSpace(target, 4, 7);

				if (pos != null) {
					BlockMagicMushroom.tryPlaceMushroom(wielder.world, pos, wielder, BlockMagicMushroom.getRandomMushroom(0.1f, 0.06f), 600);
				}
			}
		}

		@Override
		void greaterPowerOnEntityHit(ItemStack stack, EntityLivingBase target, EntityLivingBase wielder) {

		}
	},

	SORCERY(Element.SORCERY) {
		@Override
		void onUpdateEffect(ItemStack stack, World world, Entity entity, int slot, boolean isHeld) {

		}

		@Override
		void lesserPowerOnEntityHit(ItemStack stack, EntityLivingBase target, EntityLivingBase wielder, boolean charged) {
			// handled in LefClick event

		}

		@Override
		void greaterPowerOnEntityHit(ItemStack stack, EntityLivingBase target, EntityLivingBase wielder) {
			if (!wielder.world.isRemote) {
				double d = AncientSpellcraft.rand.nextDouble();
				if (d < 0.2) {
					target.addPotionEffect(new PotionEffect(WizardryPotions.containment, 60));
				} else if (d < 0.5) {
					target.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 60));
				}

				ItemStack conjured;

				int duration = 100;
				if (wielder instanceof EntityPlayer && ItemArtefact.isArtefactActive((EntityPlayer) wielder, WizardryItems.ring_conjurer)) {
					duration += 60;
				}
				if (AncientSpellcraft.rand.nextDouble() < 0.8f) {
					conjured = Animate.conjureItem(new SpellModifiers(), WizardryItems.spectral_sword);
				} else {
					conjured = Animate.conjureItem(new SpellModifiers(), WizardryItems.spectral_bow);
					duration += 60;
				}

				BlockPos pos = BlockUtils.findNearbyFloorSpace(wielder, 4, 8);
				if (pos != null && pos != BlockPos.ORIGIN) {

					EntityAnimatedItem minion = new EntityAnimatedItem(wielder.world);
					// In this case we don't care whether the minions can fly or not.
					minion.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
					minion.setLifetime(duration);
					minion.setCaster(wielder);
					minion.setHeldItem(EnumHand.MAIN_HAND, conjured);
					//AnimateWeapon.addAnimatedEntityExtras(minion, wielder.getPosition(), wielder, new SpellModifiers());
					wielder.world.spawnEntity(minion);
				}
			}
		}
	},

	HEALING(Element.HEALING) {
		@Override
		void onUpdateEffect(ItemStack stack, World world, Entity entity, int slot, boolean isHeld) {

		}

		@Override
		void lesserPowerOnEntityHit(ItemStack stack, EntityLivingBase target, EntityLivingBase wielder, boolean charged) {
			if (ASUtils.isEntityConsideredUndead(target)) {

				// sets undeads on fire
				target.setFire(2);

				// add mark if it was not present
				if (!target.isPotionActive(WizardryPotions.mark_of_sacrifice)) {
					target.addPotionEffect(new PotionEffect(WizardryPotions.mark_of_sacrifice, 40, 1));
				}

			}
		}

		@Override
		void greaterPowerOnEntityHit(ItemStack stack, EntityLivingBase target, EntityLivingBase wielder) {
			List<EntityLivingBase> nearbyEntities = EntityUtils.getEntitiesWithinRadius(16, wielder.posX, wielder.posY, wielder.posZ, wielder.world, EntityLivingBase.class);
			List<EntityLivingBase> entitiesToBuff = nearbyEntities.stream().filter(e -> AllyDesignationSystem.isAllied(wielder, e)).collect(Collectors.toList());

			List<PotionEffect> healingElementApplicablePotionList = Arrays.asList(
					new PotionEffect(MobEffects.REGENERATION, 100, 1),
					new PotionEffect(MobEffects.FIRE_RESISTANCE, 200, 1),
					new PotionEffect(MobEffects.ABSORPTION, 200, 1),
					new PotionEffect(MobEffects.STRENGTH, 200, 0),
					new PotionEffect(MobEffects.SPEED, 200, 0),
					new PotionEffect(MobEffects.HASTE, 200, 0),
					new PotionEffect(MobEffects.NIGHT_VISION, 200, 0),
					new PotionEffect(MobEffects.SATURATION, 60, 0),
					new PotionEffect(WizardryPotions.empowerment, 200, 0),
					new PotionEffect(WizardryPotions.font_of_mana, 200, 0),
					new PotionEffect(WizardryPotions.ward, 200, 0),
					new PotionEffect(ASPotions.fortified_archery, 200, 0),
					new PotionEffect(ASPotions.projectile_ward, 200, 0),
					new PotionEffect(ASPotions.wizard_shield, 200, 8)
			);

			if (!entitiesToBuff.isEmpty()) {
				PotionEffect potionToApply = healingElementApplicablePotionList.get(AncientSpellcraft.rand.nextInt(healingElementApplicablePotionList.size()));
				entitiesToBuff.forEach(e -> e.addPotionEffect(potionToApply));
			}
		}
	};

	private final Element element;

	EnumElementalSwordEffect(Element element) {
		this.element = element;
	}

	abstract void onUpdateEffect(ItemStack stack, World world, Entity entity, int slot, boolean isHeld);

	abstract void lesserPowerOnEntityHit(ItemStack stack, EntityLivingBase target, EntityLivingBase wielder, boolean charged);

	abstract void greaterPowerOnEntityHit(ItemStack stack, EntityLivingBase target, EntityLivingBase wielder);

	public static void onUpdate(Element element, ItemStack stack, World world, EntityLivingBase entity, int slot, boolean isHeld) {
		fromElement(element).onUpdateEffect(stack, world, entity, slot, isHeld);
	}

	// called by com.windanesz.ancientspellcraft.item.ItemBattlemageSword.hitEntity which is server side only
	public static void hitEntity(Element element, ItemStack stack, EntityLivingBase target, EntityLivingBase wielder) {
		EnumElementalSwordEffect effect = fromElement(element);
		boolean charged = WizardClassWeaponHelper.isChargeFull(stack);

		// Apply on-hit effects
		for (Map.Entry<Runeword, Integer> activeRuneword : ItemBattlemageSword.getActiveRunewords(stack).entrySet()) {
			((Runeword) activeRuneword.getKey()).onAboutToHitEntity(wielder.world, wielder, target, EnumHand.MAIN_HAND, stack, new SpellModifiers(), charged);
		}

		// Apply passive effects
		for (Spell spell : WandHelper.getSpells(stack)) {
			if (spell instanceof Runeword && ((Runeword) spell).isPassive()) {
				((Runeword) spell).onAboutToHitEntity(wielder.world, wielder, target, EnumHand.MAIN_HAND, stack, new SpellModifiers(), charged);
			}
		}

		// Lesser Power - all hit
		effect.lesserPowerOnEntityHit(stack, target, wielder, charged);

		if (charged) {
			effect.greaterPowerOnEntityHit(stack, target, wielder);
			WizardClassWeaponHelper.resetChargeProgress(stack);
		}

		// Greater Power - charged hit only

	}

	private static EnumElementalSwordEffect fromElement(Element element) {

		for (EnumElementalSwordEffect effect : values()) {
			if (effect.element == element) { return effect; }
		}

		return EnumElementalSwordEffect.MAGIC;
	}

	//--------------------------  Helper methods  --------------------------//

	public static boolean hasPosChanged(EntityPlayer player) {
		WizardData data = WizardData.get(player);
		BlockPos pos = data.getVariable(ItemBattlemageSword.LAST_POS);
		data.setVariable(ItemBattlemageSword.LAST_POS, player.getPosition());
		if (pos == null) {
			return true;
		} else {
			return !pos.equals(player.getPosition());
		}
	}

	public static double getAngleBetweenEntities(Entity first, Entity second) {
		return Math.atan2(second.posZ - first.posZ, second.posX - first.posX) * (180 / Math.PI) + 90;
	}

	public static void electrocute(World world, Entity caster, Vec3d origin, Entity target, float damage, int ticksInUse) {

		if (MagicDamage.isEntityImmune(MagicDamage.DamageType.SHOCK, target)) {
			if (!world.isRemote && ticksInUse == 1 && caster instanceof EntityPlayer) {
				((EntityPlayer) caster).sendStatusMessage(new TextComponentTranslation("spell.resist", target.getName(),
						"lightning damage"), true);
			}
		} else {
			if (!world.isRemote) {
				EntityUtils.attackEntityWithoutKnockback(target,
						MagicDamage.causeDirectMagicDamage(caster, MagicDamage.DamageType.SHOCK), damage);
			}
		}

		if (world.isRemote) {

			ParticleBuilder.create(ParticleBuilder.Type.BEAM).entity(caster).clr(0.2f, 0.6f, 1)
					.pos(caster != null ? origin.subtract(caster.getPositionVector()) : origin).target(target).spawn(world);

			if (ticksInUse % 3 == 0) {
				ParticleBuilder.create(ParticleBuilder.Type.LIGHTNING).entity(caster)
						.pos(caster != null ? origin.subtract(caster.getPositionVector()) : origin).target(target).spawn(world);
			}

			// Particle effect
			for (int i = 0; i < 5; i++) {
				ParticleBuilder.create(ParticleBuilder.Type.SPARK, target).spawn(world);
			}
		}
	}
}
