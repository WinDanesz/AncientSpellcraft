package com.windanesz.ancientspellcraft.entity.construct;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftSounds;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.entity.ICustomHitbox;
import electroblob.wizardry.entity.construct.EntityMagicConstruct;
import electroblob.wizardry.entity.projectile.EntityMagicArrow;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.GeometryUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Author: Dan
 * Adapted from: electroblob.wizardry.entity.construct.EntityForcefield (Author: Electroblob)
 */
public class EntitySpiritWard extends EntityMagicConstruct implements ICustomHitbox {

	/**
	 * Extra radius to search around the spirit ward for incoming entities. Any entities with a velocity greater than
	 * this could potentially penetrate the spirit ward.
	 */
	private static final double SEARCH_BORDER_SIZE = 4;

	private static final float BOUNCINESS = 0.2f;

	private float radius;

	public EntitySpiritWard(World world) {
		super(world);
		setRadius(3); // Shouldn't be needed but it's a good failsafe
		this.ignoreFrustumCheck = true;
		this.noClip = true;
	}

	public void setRadius(float radius) {
		this.radius = radius;
		this.height = 2 * radius;
		this.width = 2 * radius;
		// y-3 because it needs to be centred on the given position
		this.setEntityBoundingBox(new AxisAlignedBB(posX - radius, posY - radius, posZ - radius,
				posX + radius, posY + radius, posZ + radius));
	}

	public float getRadius() {
		return radius;
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;//!this.isDead;
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity entity) {
		return null;//entity.getEntityBoundingBox();
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox() {
		return super.getCollisionBoundingBox();
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}

	@Override
	public void onUpdate() {

		super.onUpdate();

		if(this.ticksExisted % 50  == 0){
			this.playSound(AncientSpellcraftSounds.SHADOW_MAGIC_LOOP, 1f, 1f);
		}

		if (world.isRemote) {
			ParticleBuilder.create(Type.FLASH)
					.pos(this.posX, this.posY + 0.1, this.posZ)
					.face(EnumFacing.UP)
					.clr(93, 12, 174)
					.collide(false)
					.scale(6.0F)
					.time(10)
					.spawn(world);

			for (int i = 1; i < 3; i++) {
				//				double radius = rand.nextDouble() * 2.0;
				double radius = 3.2;
				float angle = rand.nextFloat() * (float) Math.PI * 2;
				;
				ParticleBuilder.create(Type.SPARKLE)
						.pos(this.posX + radius * MathHelper.cos(angle), this.posY, this.posZ + radius * MathHelper.sin(angle))
						.vel(0, 0.05, 0)
						.time(48 + this.rand.nextInt(12))
						.clr(0, 0, 0)
						.spawn(world);
			}
		}

		List<Entity> targets = EntityUtils.getEntitiesWithinRadius(radius + SEARCH_BORDER_SIZE, posX, posY, posZ, world, Entity.class);

		targets.remove(this);
		targets.removeIf(t -> t instanceof EntityXPOrb); // Gets annoying since they're attracted to the player

		// Spirit wards doesn't stop projectiles
		targets.removeIf(t -> t instanceof EntityMagicArrow || t instanceof EntityThrowable || t instanceof EntityArrow);

		for (Entity target : targets) {

			// only prevents undeads from entering the circle
			if (EntityUtils.isLiving(target) && ((EntityLivingBase) target).isPotionActive(WizardryPotions.curse_of_undeath) || (this.isValidTarget(target) && ((EntityLivingBase) target).isEntityUndead())) {

				Vec3d currentPos = Arrays.stream(GeometryUtils.getVertices(target.getEntityBoundingBox()))
						.min(Comparator.comparingDouble(v -> v.distanceTo(this.getPositionVector())))
						.orElse(target.getPositionVector()); // This will never happen, it's just here to make the compiler happy

				double currentDistance = target.getDistance(this);

				// Estimate the target's position next tick
				// We have to assume the same vertex is closest or the velocity will be wrong
				Vec3d nextTickPos = currentPos.add(target.motionX, target.motionY, target.motionZ);
				double nextTickDistance = nextTickPos.distanceTo(this.getPositionVector());

				boolean flag;

				if (EntityUtils.isLiving(target)) {
					// Non-allied living entities shouldn't be inside at all
					flag = nextTickDistance <= radius;
				} else {
					// undead entities will bounce off if they hit the spirit ward within the next tick...
					flag = (currentDistance > radius && nextTickDistance <= radius) // ...from the outside...
							|| (currentDistance < radius && nextTickDistance >= radius); // ...or from the inside
				}

				if (flag) {

					Vec3d targetRelativePos = currentPos.subtract(this.getPositionVector());

					double nudgeVelocity = this.contains(target) ? -0.1 : 0.1;
					if (EntityUtils.isLiving(target))
						nudgeVelocity = 0.25;
					Vec3d extraVelocity = targetRelativePos.normalize().scale(nudgeVelocity);

					// ...make it bounce off!
					target.motionX = target.motionX * -BOUNCINESS + extraVelocity.x;
					target.motionY = target.motionY * -BOUNCINESS + extraVelocity.y;
					target.motionZ = target.motionZ * -BOUNCINESS + extraVelocity.z;

					// Prevents the spirit ward bouncing things into the floor
					if (target.onGround && target.motionY < 0)
						target.motionY = 0.1;

					// How far the target needs to move towards the centre (negative means away from the centre)
					double distanceTowardsCentre = -(targetRelativePos.length() - radius) - (radius - nextTickDistance);
					Vec3d targetNewPos = target.getPositionVector().add(targetRelativePos.normalize().scale(distanceTowardsCentre));
					target.setPosition(targetNewPos.x, targetNewPos.y, targetNewPos.z);

					world.playSound(target.posX, target.posY, target.posZ, AncientSpellcraftSounds.SHADOW_MAGIC_CHARGE, // TODO replace
							WizardrySounds.SPELLS, 0.4f, 1.0f, false);

					if (world.isRemote) {

						Vec3d relativeImpactPos = targetRelativePos.normalize().scale(radius);

						float yaw = (float) Math.atan2(relativeImpactPos.x, -relativeImpactPos.z);
						float pitch = (float) Math.asin(relativeImpactPos.y / radius);

						ParticleBuilder.create(Type.FLASH).pos(this.getPositionVector().add(relativeImpactPos))
								.time(6).face((float) (yaw * 180 / Math.PI), (float) (pitch * 180 / Math.PI))
								.clr(0, 0, 0).spawn(world);

						for (int i = 0; i < 12; i++) {

							float yaw1 = yaw + 0.3f * (rand.nextFloat() - 0.5f) - (float) Math.PI / 2;
							float pitch1 = pitch + 0.3f * (rand.nextFloat() - 0.5f);

							double r = radius + 0.05;
							double x = this.posX + r * MathHelper.cos(yaw1) * MathHelper.cos(pitch1);
							double y = this.posY + r * MathHelper.sin(pitch1);
							double z = this.posZ + r * MathHelper.sin(yaw1) * MathHelper.cos(pitch1);

							ParticleBuilder.create(Type.DUST).pos(x, y, z).time(6 + rand.nextInt(6))
									.face((float) (yaw1 * 180 / Math.PI) + 90, (float) (pitch1 * 180 / Math.PI)).scale(1.5f)
									.clr(0, 0, 0).spawn(world);
						}
					}
				}
			}
		}
	}

	@Override
	public boolean contains(Vec3d vec) {
		return vec.distanceTo(this.getPositionVector()) < radius; // The surface counts as outside
	}

	/**
	 * Returns true if the given bounding box is completely inside this spirit ward (the surface counts as outside).
	 */
	public boolean contains(AxisAlignedBB box) {
		return Arrays.stream(GeometryUtils.getVertices(box)).allMatch(this::contains);
	}

	/**
	 * Returns true if the given entity is completely inside this spirit ward (the surface counts as outside).
	 */
	public boolean contains(Entity entity) {
		return contains(entity.getEntityBoundingBox());
	}

	@Override
	public Vec3d calculateIntercept(Vec3d origin, Vec3d endpoint, float fuzziness) {

		// We want the intercept between the line and a sphere
		// First we need to find the point where the line is closest to the centre
		// Then we can use a bit of geometry to find the intercept

		// Find the closest point to the centre
		// http://mathworld.wolfram.com/Point-LineDistance3-Dimensional.html
		Vec3d line = endpoint.subtract(origin);
		double t = -origin.subtract(this.getPositionVector()).dotProduct(line) / line.lengthSquared();
		Vec3d closestPoint = origin.add(line.scale(t));
		// Now calculate the distance from that point to the centre (squared because that's all we need)
		double dsquared = closestPoint.squareDistanceTo(this.getPositionVector());
		double rsquared = Math.pow(radius + fuzziness, 2);
		// If the minimum distance is outside the radius (plus fuzziness) then there is no intercept
		if (dsquared > rsquared)
			return null;
		// Now do pythagoras to find the other side of the triangle, which is the distance along the line from
		// the closest point to the edge of the sphere, and go that far back towards the origin - and that's it!
		return closestPoint.subtract(line.normalize().scale(MathHelper.sqrt(rsquared - dsquared)));
	}

	// Need to sync the caster because we're now dealing with client-side motion

	@Override
	public void writeSpawnData(ByteBuf data) {
		super.writeSpawnData(data);
		data.writeFloat(getRadius());
		if (getCaster() != null)
			data.writeInt(getCaster().getEntityId());
	}

	@Override
	public void readSpawnData(ByteBuf data) {

		super.readSpawnData(data);

		setRadius(data.readFloat());

		if (!data.isReadable())
			return;

		Entity entity = world.getEntityByID(data.readInt());

		if (entity instanceof EntityLivingBase) {
			setCaster((EntityLivingBase) entity);
		} else {
			Wizardry.logger.warn("Spirit Ward caster with ID in spawn data not found");
		}
	}

	@Override
	public boolean canRenderOnFire() {
		return false;
	}
}
