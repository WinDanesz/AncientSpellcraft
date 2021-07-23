package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.RayTracer;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class PrismaticSpray extends Spell {
	protected static final double Y_OFFSET = 0.25;

	public PrismaticSpray() {

		super(AncientSpellcraft.MODID, "prismatic_spray", SpellActions.SUMMON, false);
		addProperties(BLAST_RADIUS, DIRECT_DAMAGE, EFFECT_DURATION);
		soundValues(1.0f, 1.0f, 0.1f);
	}

	@Override
	public boolean requiresPacket() {
		return false;
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {

		boolean foundTarget = false;

		if (!ItemArtefact.isArtefactActive(caster, AncientSpellcraftItems.charm_prismatic_spray)) {
			Random rnd = new Random(42);

			double radius = getProperty(BLAST_RADIUS).floatValue() * modifiers.get(WizardryItems.blast_upgrade);

			List<EntityLivingBase> targets = EntityUtils.getEntitiesWithinRadius(radius, caster.posX, caster.posY, caster.posZ, world, EntityLivingBase.class);

			ArrayList<Element> beamTypes = new ArrayList<>(EnumSet.allOf(Element.class));
			// removing MAGIC
			beamTypes.remove(0);

			for (EntityLivingBase target : targets) {
				if (target == caster) {
					continue;
				}

				foundTarget = true;

				int currIndex = (int) ((rnd.nextFloat() * beamTypes.size()));
				while (beamTypes.get(currIndex) == Element.MAGIC) {
					currIndex = (int) ((rnd.nextFloat() * beamTypes.size()));
				}

				Element element = beamTypes.get(currIndex);
				performEffect(element, caster, target, modifiers);
				beamTypes.remove(currIndex);
				if (beamTypes.isEmpty()) {
					break;
				}
			}

			if (!foundTarget && !caster.world.isRemote) {
				caster.sendStatusMessage(new TextComponentTranslation("spell." + this.getRegistryName() + ".no_target"), true);
			}
		} else {
			float damage = getProperty(DIRECT_DAMAGE).intValue() * modifiers.get(SpellModifiers.POTENCY);
			int duration = (int) (getProperty(EFFECT_DURATION).intValue() * modifiers.get(WizardryItems.duration_upgrade));

			Vec3d look = caster.getLookVec();
			Vec3d origin = new Vec3d(caster.posX, caster.posY + caster.getEyeHeight() - Y_OFFSET, caster.posZ);
			double range = getRange(world, origin, look, caster, ticksInUse, modifiers);
			Vec3d endpoint = origin.add(look.scale(range));

			// Change the filter depending on whether living entities are ignored or not
			RayTraceResult rayTrace = RayTracer.rayTrace(world, origin, endpoint, 0f, false,
					true, false, Entity.class, (java.util.function.Predicate<? super Entity>) RayTracer.ignoreEntityFilter(caster));

			boolean flag;
			if (rayTrace != null) {
				if (rayTrace.typeOfHit == RayTraceResult.Type.ENTITY) {
					flag = onEntityHit(world, rayTrace.entityHit, rayTrace.hitVec, caster, origin, ticksInUse, modifiers);
					if (flag)
						range = origin.distanceTo(rayTrace.hitVec);

					if (rayTrace.entityHit instanceof EntityLivingBase) {
						foundTarget = true;
						int precision = 7;
						float radius = 2;
						EntityLivingBase target = (EntityLivingBase) rayTrace.entityHit;

						for (int i = 0; i < precision; i++) {
							double p1 = (i * Math.PI) / (precision / 2);

							double x1 = caster.posX;
							double y1 = (caster.posY + caster.getEyeHeight()) + Math.cos(p1) * radius;
							double z1 = caster.posZ;
							Vec3d endpoint2 = new Vec3d(x1, y1, z1);

							if (i == 0) {
								spawnBeamFocused(world, origin, look, endpoint2, caster, target, range, Element.EARTH, 58, 92, 24);
							}
							if (i == 1) {
								spawnBeamFocused(world, origin, look, endpoint2, caster, target, range, Element.LIGHTNING, 70, 76, 163);
							}
							if (i == 2) {
								spawnBeamFocused(world, origin, look, endpoint2, caster, target, range, Element.NECROMANCY, 99, 9, 93);
							}
							if (i == 3) {
								spawnBeamFocused(world, origin, look, endpoint2, caster, target, range, Element.FIRE, 252, 118, 23);
							}
							if (i == 4) {
								spawnBeamFocused(world, origin, look, endpoint2, caster, target, range, Element.HEALING, 253, 255, 122);
							}
							if (i == 5) {
								spawnBeamFocused(world, origin, look, endpoint2, caster, target, range, Element.SORCERY, 0, 222, 23);
							}
							if (i == 6) {
								spawnBeamFocused(world, origin, look, endpoint2, caster, target, range, Element.ICE, 144, 236, 252);
							}
						}
						EntityUtils.attackEntityWithoutKnockback(target, MagicDamage.causeDirectMagicDamage(caster, MagicDamage.DamageType.MAGIC), damage * 1.5f);
						target.addPotionEffect(new PotionEffect(MobEffects.POISON, duration));
						target.addPotionEffect(new PotionEffect(WizardryPotions.paralysis, duration, 0));
						target.addPotionEffect(new PotionEffect(MobEffects.WITHER, duration));
						target.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, duration, 0));
						target.addPotionEffect(new PotionEffect(WizardryPotions.frost, duration, 0));
						target.setFire(duration / 20);
					}
				}
			}
		}
		if (foundTarget) {
			playSound(world, caster.posX, caster.posY, caster.posZ, 0, 0, modifiers);
		}
		return foundTarget;
	}

	protected boolean onEntityHit(World world, Entity target, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return true;
	}

	private void performEffect(Element element, EntityPlayer caster, EntityLivingBase target, SpellModifiers modifiers) {
		float damage = getProperty(DIRECT_DAMAGE).intValue() * modifiers.get(SpellModifiers.POTENCY);
		int duration = (int) (getProperty(EFFECT_DURATION).intValue() * modifiers.get(WizardryItems.duration_upgrade));

		Vec3d look = caster.getLookVec();
		Vec3d origin = new Vec3d(caster.posX, caster.posY + caster.getEyeHeight() - Y_OFFSET, caster.posZ);

		if (caster.world.isRemote && !Wizardry.proxy.isFirstPerson(caster)) {
			origin = origin.add(look.scale(1.2));
		}

		double range = caster.getDistance(target);

		switch (element) {
			case EARTH:
				EntityUtils.attackEntityWithoutKnockback(target, MagicDamage.causeDirectMagicDamage(caster, MagicDamage.DamageType.POISON), damage);
				target.addPotionEffect(new PotionEffect(MobEffects.POISON, duration));
				spawnBeam(caster.world, origin, look, caster, target, range, element, 58, 92, 24);
				break;

			case LIGHTNING:
				EntityUtils.attackEntityWithoutKnockback(target, MagicDamage.causeDirectMagicDamage(caster, MagicDamage.DamageType.SHOCK), damage);
				target.addPotionEffect(new PotionEffect(WizardryPotions.paralysis, duration, 0));
				spawnBeam(caster.world, origin, look, caster, target, range, element, 70, 76, 163);
				break;

			case NECROMANCY:
				EntityUtils.attackEntityWithoutKnockback(target, MagicDamage.causeDirectMagicDamage(caster, MagicDamage.DamageType.WITHER), damage);
				target.addPotionEffect(new PotionEffect(MobEffects.WITHER, duration));
				spawnBeam(caster.world, origin, look, caster, target, range, element, 99, 9, 93);
				break;

			case FIRE:
				EntityUtils.attackEntityWithoutKnockback(target, MagicDamage.causeDirectMagicDamage(caster, MagicDamage.DamageType.FIRE), damage);
				target.setFire(duration / 20);
				spawnBeam(caster.world, origin, look, caster, target, range, element, 252, 118, 23);
				break;

			case HEALING:
				EntityUtils.attackEntityWithoutKnockback(target, MagicDamage.causeDirectMagicDamage(caster, MagicDamage.DamageType.RADIANT), damage);
				target.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, duration, 0));
				if (target.isEntityUndead()) {
					target.setFire(duration / 40);
				}
				spawnBeam(caster.world, origin, look, caster, target, range, element, 253, 255, 122);

				break;

			case SORCERY:
				EntityUtils.attackEntityWithoutKnockback(target, MagicDamage.causeDirectMagicDamage(caster, MagicDamage.DamageType.FORCE), damage);
				EntityUtils.applyStandardKnockback(caster, target, 2f);
				spawnBeam(caster.world, origin, look, caster, target, range, element, 0, 222, 23);
				break;

			case ICE:
				EntityUtils.attackEntityWithoutKnockback(target, MagicDamage.causeDirectMagicDamage(caster, MagicDamage.DamageType.FROST), damage);
				target.addPotionEffect(new PotionEffect(WizardryPotions.frost, duration, 0));
				spawnBeam(caster.world, origin, look, caster, target, range, element, 144, 236, 252);
				break;
			default:
				break;
		}
	}

	protected void spawnBeam(World world, Vec3d origin, Vec3d direction, EntityLivingBase caster, EntityLivingBase target, double distance, Element element, int r, int g, int b) {
		if (world.isRemote) {
			ParticleBuilder.create(ParticleBuilder.Type.BEAM)
					.entity(caster)
					.time(40)
					.pos(0, caster.getEyeHeight() - 0.25, 0)
					.target(target)
					.clr(r, g, b)
					.scale(2f)
					.seed(world.getTotalWorldTime())
					.spawn(world);

		}
	}

	protected void spawnBeamFocused(World world, Vec3d origin, Vec3d direction, Vec3d offset, EntityLivingBase caster, EntityLivingBase target, double distance, Element element, int r, int g, int b) {
		if (world.isRemote) {
			ParticleBuilder.create(ParticleBuilder.Type.BEAM)
					.time(10)
					.pos(offset)
					.target(target)
					.clr(r, g, b)
					.scale(1f)
					.seed(world.getTotalWorldTime())
					.spawn(world);
		}
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}

	protected double getRange(World world, Vec3d origin, Vec3d direction, @Nullable EntityLivingBase caster, int ticksInUse, SpellModifiers modifiers) {
		return 10d * modifiers.get(WizardryItems.range_upgrade);
	}
}