package com.windanesz.ancientspellcraft.spell;

import electroblob.wizardry.Wizardry;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.RayTracer;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class Regrowth extends SpellBuffAS {

	protected boolean ignoreLivingEntities = false;

	/**
	 * The distance below the caster's eyes that the bolt particles start from.
	 */
	protected static final double Y_OFFSET = 0.25;

	protected double particleSpacing = 0.85;
	/**
	 * The maximum jitter (random position offset) for spawned particles. Defaults to 0.1.
	 */
	protected double particleJitter = 0.1;
	/**
	 * The velocity of spawned particles in the direction the caster is aiming, can be negative. Defaults to 0.
	 */
	protected double particleVelocity = 0;

	public Regrowth() {
		super("regrowth", 201, 90, 168, () -> MobEffects.REGENERATION);
		addProperties(RANGE);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {

		if (caster.isSneaking()) {

			Vec3d look = caster.getLookVec();
			Vec3d origin = new Vec3d(caster.posX, caster.posY + caster.getEyeHeight() - Y_OFFSET, caster.posZ);
			if (!this.isContinuous && world.isRemote && !Wizardry.proxy.isFirstPerson(caster)) {
				origin = origin.add(look.scale(1.2));
			}

			if (!shootSpell(world, origin, look, caster, ticksInUse, modifiers))
				return false;

			caster.swingArm(hand);
			this.playSound(world, caster, ticksInUse, -1, modifiers);
			return true;
		}

		return super.cast(world, caster, hand, ticksInUse, modifiers);
	}

	/**
	 * Takes care of the shared stuff for the three casting methods. This is mainly for internal use.
	 */
	protected boolean shootSpell(World world, Vec3d origin, Vec3d direction, @Nullable EntityLivingBase caster, int ticksInUse, SpellModifiers modifiers) {

		double range = getProperty(RANGE).doubleValue() * modifiers.get(WizardryItems.range_upgrade);
		Vec3d endpoint = origin.add(direction.scale(range));

		// Change the filter depending on whether living entities are ignored or not
		RayTraceResult rayTrace = RayTracer.rayTrace(world, origin, endpoint, 0, false,
				true, false, Entity.class, ignoreLivingEntities ? EntityUtils::isLiving
						: RayTracer.ignoreEntityFilter(caster));

		boolean flag = false;

		if (rayTrace != null) {
			// Doesn't matter which way round these are, they're mutually exclusive
			if (rayTrace.typeOfHit == RayTraceResult.Type.ENTITY) {
				// Do whatever the spell does when it hits an entity
				// FIXME: Some spells (e.g. lightning web) seem to not render when aimed at item frames
				flag = onEntityHit(world, rayTrace.entityHit, rayTrace.hitVec, caster, origin, ticksInUse, modifiers);
				// If the spell succeeded, clip the particles to the correct distance so they don't go through the entity
				if (flag)
					range = origin.distanceTo(rayTrace.hitVec);

			} else {
				return false;
			}
		}

		// If flag is false, either the spell missed or the relevant entity/block hit method returned false
		if (!flag)
			return false;

		// Particle spawning
		if (world.isRemote) {
			spawnParticleRay(world, origin, direction, caster, range);
		}

		return true;
	}

	private boolean onEntityHit(World world, Entity target, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		if (target instanceof EntityLivingBase) {
			return this.applyEffects((EntityLivingBase) target, modifiers);
		}
		return false;
	}

	protected void spawnParticleRay(World world, Vec3d origin, Vec3d direction, @Nullable EntityLivingBase caster, double distance) {

		Vec3d velocity = direction.scale(particleVelocity);

		for (double d = particleSpacing; d <= distance; d += particleSpacing) {
			double x = origin.x + d * direction.x + particleJitter * (world.rand.nextDouble() * 2 - 1);
			double y = origin.y + d * direction.y + particleJitter * (world.rand.nextDouble() * 2 - 1);
			double z = origin.z + d * direction.z + particleJitter * (world.rand.nextDouble() * 2 - 1);
			spawnParticle(world, x, y, z, velocity.x, velocity.y, velocity.z);
		}
	}

	protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
		ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(x, y, z).clr(201, 90, 168).spawn(world);
		ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).time(12 + world.rand.nextInt(8)).clr(80, 187, 114).spawn(world);
	}

}
