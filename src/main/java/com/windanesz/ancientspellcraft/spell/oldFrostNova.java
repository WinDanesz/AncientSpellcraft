package com.windanesz.ancientspellcraft.spell;

//public class FrostNova extends Spell implements IChargingSpell {
public class oldFrostNova {
//
	public oldFrostNova() {
//		super(AncientSpellcraft.MODID, "eye_of_the_storm", EnumAction.BLOCK, true);
	}
//
//	@Override
//	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
//
//		if (ticksInUse > 1 && ticksInUse % 60 == 0) {
//			if (!world.isRemote) {
//
//				for (EntityLivingBase entity : WizardryUtilities.getEntitiesWithinRadius(15, caster.posX, caster.posY, caster.posZ, world)) {
//					if (!AllyDesignationSystem.isAllied(caster, entity)) {
//
//						// Releases shards
//						for (int i = 0; i < 3; i++) {
//							double dx = world.rand.nextDouble() - 0.5;
//							double dy = 1;
//							double dz = world.rand.nextDouble() - 0.5;
//							EntityLightningArrow iceshard = new EntityLightningArrow(world);
//							//				EntityIceShard iceshard = new EntityIceShard(world);
//
//							iceshard.aim(caster, entity, 0.6F, 0F);
//							//							iceshard.posY = iceshard.posY + 1.5F;
//							//							iceshard.setPosition(caster.xposX + d, caster.posY + dy + 3, caster.posZ + dz);
//
//							//						iceshard.motionX = dx / 2;
//							//						iceshard.motionY = dy / 1.2;
//							//						//				iceshard.motionY = dy * 1.5;
//							//						iceshard.motionZ = dz;
//							iceshard.setCaster(caster);
//							iceshard.damageMultiplier = modifiers.get(DAMAGE);
//							world.spawnEntity(iceshard);
//						}
//					}
//				}
//			}
//
//			if (world.isRemote) {
//				System.out.println("bumm! finished charging");
//				for (int i = 0; i < 50 * modifiers.get(WizardryItems.blast_upgrade); i++) {
//
//					//					ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).vel(0, 0.03, 0).time(50).clr(157, 168, 249).spawn(world);
//
//				}
//				//				spawnParticleEffect(world, caster, modifiers);
//			}
//			return true;
//		} else {
//
//			if (!Wizardry.settings.replaceVanillaFallDamage) {
//				caster.fallDistance = 0;
//			}
//
//			caster.motionY = caster.motionY < 0.01f ? caster.motionY + 0.1f : caster.motionY;
//
//			if (world.isRemote) {
//
//				//				ParticleBuilder.create(ParticleBuilder.Type.LIGHTNING).scale(ticksInUse / 10F).pos(caster.posX, caster.posY + 1.5F, caster.posZ).time(4).spawn(world);
//
//				for (int i = 0; i < ticksInUse / 2; i++) {
//
//					ParticleBuilder.create(ParticleBuilder.Type.SPARK).pos(
//							caster.posX + ((world.rand.nextDouble() / 5) * (ticksInUse / 10F) * (world.rand.nextBoolean() ? -1 : 1)),
//							caster.posY + +(0.3 + (world.rand.nextDouble() / 5) * (ticksInUse / 10F) * (world.rand.nextBoolean() ? -1 : 1)),
//							caster.posZ + (world.rand.nextDouble() / 5) * (ticksInUse / 10F) * (world.rand.nextBoolean() ? -1 : 1)).time(4).spawn(world);
//
//				}
//			}
//			if (ticksInUse > 61) {
//				return false;
//			}
//		}
//		return true;
//	}
//	//
//	//		List<EntityLivingBase> targets = WizardryUtilities.getEntitiesWithinRadius(getProperty(EFFECT_RADIUS).floatValue()
//	//				* modifiers.get(WizardryItems.blast_upgrade), caster.posX, caster.posY, caster.posZ, world);
//	//
//	//		targets.removeIf(target -> !AllyDesignationSystem.isValidTarget(caster, target));
//	//
//	//		for (EntityLivingBase target : targets) {
//	//			affectEntity(world, caster, target, modifiers);
//	//		}
//	//
//	//		if (world.isRemote) {
//	//			spawnParticleEffect(world, caster, modifiers);
//	//		}
//	//
//	//		this.playSound(world, caster, ticksInUse, -1, modifiers);
//	//		return true;
//
//	/**
//	 * Only called client-side
//	 *
//	 * @param world     The world to spawn the particles in.
//	 * @param caster    The caster of the spell.
//	 * @param modifiers The modifiers the spell was cast with.
//	 */
//	//	protected void spawnParticleEffect(World world, EntityLivingBase caster, SpellModifiers modifiers) {
//	//
//	//		double maxRadius = 3; //getProperty(EFFECT_RADIUS).floatValue() * modifiers.get(WizardryItems.blast_upgrade);
//	//		int particleCount = (int) Math.round(particleDensity * Math.PI * maxRadius * maxRadius);
//	//
//	//		for (int i = 0; i < particleCount; i++) {
//	//
//	//			double radius = (1 + world.rand.nextDouble() * (maxRadius - 1));
//	//			float angle = world.rand.nextFloat() * (float) Math.PI * 2f;
//	//
//	//			spawnParticle(world, caster.posX + radius * MathHelper.cos(angle),
//	//					caster.getEntityBoundingBox().minY,
//	//					caster.posZ + radius * MathHelper.sin(angle));
//	//		}
//	//	}
//
//	/**
//	 * Called to do something to each entity within the spell's area of effect.
//	 *
//	 * @param world     The world in which the spell was cast.
//	 * @param caster    The entity that cast the spell.
//	 * @param target    The entity to do something to.
//	 * @param modifiers The modifiers the spell was cast with.
//	 */
//	protected void affectEntity(World world, EntityLivingBase caster, EntityLivingBase target, SpellModifiers modifiers) {
//
//	}
//
//	protected void spawnParticle(World world, double x, double y, double z) {
//
//	}

}
