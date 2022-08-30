package com.windanesz.ancientspellcraft.entity.construct;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.entity.ICustomHitbox;
import electroblob.wizardry.entity.construct.EntityMagicConstruct;
import electroblob.wizardry.entity.projectile.EntityMagicArrow;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.GeometryUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

// TODO: Possibly convert this to EntityScaledConstruct
@Mod.EventBusSubscriber
public class EntityArcaneBarrier extends EntityMagicConstruct implements ICustomHitbox {

	/**
	 * Extra radius to search around the forcefield for incoming entities. Any entities with a velocity greater than
	 * this could potentially penetrate the forcefield.
	 */
	private static final double SEARCH_BORDER_SIZE = 5;

	private static final float BOUNCINESS = 0.2f;
	private static String RADIUS_TAG = "radius";
	private float radius;

	private static final DataParameter<Integer> COLOUR = EntityDataManager.<Integer>createKey(EntityArcaneBarrier.class, DataSerializers.VARINT);

	private static final DataParameter<Float> RADIUS = EntityDataManager.<Float>createKey(EntityArcaneBarrier.class, DataSerializers.FLOAT);

	private Colour colour = Colour.MAGENTA;

	private List<Entity> entityCache = new ArrayList<>();

	public EntityArcaneBarrier(World world) {
		super(world);
		setRadius(1); // Shouldn't be needed but it's a good failsafe
		this.ignoreFrustumCheck = true;
		this.noClip = true;

	}

	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(COLOUR, 13);
		this.dataManager.register(RADIUS, 1f);
	}

	public void setColour(int newColour) {
		this.dataManager.set(COLOUR, newColour);
	}

	public Colour getColour() {
		return Colour.getByIndex(this.dataManager.get(COLOUR).intValue());
	}

	public void setRadius(float radius) {
		this.dataManager.set(RADIUS, radius);
		this.radius = radius;
		this.height = 2 * radius;
		this.width = 2 * radius;
		// y-3 because it needs to be centred on the given position
		this.setEntityBoundingBox(new AxisAlignedBB(posX - radius, posY - radius, posZ - radius,
				posX + radius, posY + radius, posZ + radius));
	}

	public float getRadius() {
		return this.dataManager.get(RADIUS);
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
	public void setDead() {
		AncientSpellcraft.proxy.handleRemoveBarrier(this);
		super.setDead();
	}

	@Override
	public void onUpdate() {
		boolean useCache = false;

		if (ticksExisted == 1 || ticksExisted % 60 == 0) {
			this.radius = dataManager.get(RADIUS);
			if (world.getBlockState(this.getPosition()).getBlock() != ASBlocks.PLACED_RUNE) {
				this.setDead();
				return;
			}

			AncientSpellcraft.proxy.handleAddBarrier(this);
		}

		if(this.ticksExisted > lifetime && lifetime != -1){
			this.despawn();
		}


		// TODO: maybe add some kind of caching mechanism, to store the recently affected entities and only check for new ones less frequently

		//		if (ticksExisted % 8 != 0) {
		//			return;
		//		}

		if (ticksExisted == 1 && world.isRemote) {
			Wizardry.proxy.playMovingSound(this, WizardrySounds.ENTITY_FORCEFIELD_AMBIENT, WizardrySounds.SPELLS, 0.5f, 1, true);
		}

		List<Entity> targets;

		if (ticksExisted % 80 == 0) {

			targets = EntityUtils.getEntitiesWithinRadius(radius + SEARCH_BORDER_SIZE, posX, posY, posZ, world, Entity.class);
			//			System.out.println("large search");
			useCache = true;
		} else {
			targets = entityCache;
			//			System.out.println("use cache");
		}



		targets.remove(this);

		targets.removeIf(t -> t instanceof EntityArmorStand || AllyDesignationSystem.isAllied((EntityLivingBase) t, getCaster()) || t instanceof EntityMagicConstruct || t instanceof EntityXPOrb || t instanceof EntityAnimal || t instanceof EntityMagicArrow && !this.isValidTarget(((EntityMagicArrow) t).getCaster())
				|| t instanceof EntityThrowable && !this.isValidTarget(((EntityThrowable) t).getThrower())
				|| t instanceof EntityArrow && !this.isValidTarget(((EntityArrow) t).shootingEntity));

		for (Entity target : targets) {

			if (target != null && this.isValidTarget(target)) {

				if (useCache) {
					entityCache = targets;
				}

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
					// Non-living entities will bounce off if they hit the forcefield within the next tick...
					flag = (currentDistance > radius && nextTickDistance <= radius) // ...from the outside...
							|| (currentDistance < radius && nextTickDistance >= radius); // ...or from the inside
				}

				if (flag) {

					// Ring of interdiction
					if (getCaster() instanceof EntityPlayer && ItemArtefact.isArtefactActive((EntityPlayer) getCaster(),
							WizardryItems.ring_interdiction) && EntityUtils.isLiving(target)) {
						target.attackEntityFrom(MagicDamage.causeIndirectMagicDamage(this, getCaster(),
								MagicDamage.DamageType.MAGIC), 1);
					}

					Vec3d targetRelativePos = currentPos.subtract(this.getPositionVector());

					double nudgeVelocity = this.contains(target) ? -0.1 : 0.1;
					if (EntityUtils.isLiving(target))
						nudgeVelocity = 0.25;
					Vec3d extraVelocity = targetRelativePos.normalize().scale(nudgeVelocity);

					// ...make it bounce off!

					if (currentDistance < radius) {

					} else {

						target.motionX = target.motionX * -BOUNCINESS + extraVelocity.x;
						target.motionY = target.motionY * -BOUNCINESS + extraVelocity.y;
						target.motionZ = target.motionZ * -BOUNCINESS + extraVelocity.z;

						// Prevents the forcefield bouncing things into the floor
						if (target.onGround && target.motionY < 0)
							target.motionY = 0.1;
					}

					// How far the target needs to move towards the centre (negative means away from the centre)
					double distanceTowardsCentre = -(targetRelativePos.length() - radius - 3) - (radius - 3 - nextTickDistance);
					double test = targetRelativePos.length() - radius - 1;
					//					double test = targetRelativePos.length() - radius - 2;
					Vec3d targetNewPos = target.getPositionVector().add(targetRelativePos.normalize().scale(distanceTowardsCentre));
					Vec3d targetNewPos2 = target.getPositionVector().add(targetRelativePos.normalize().scale(-test));

					if (currentDistance - 3 < radius) {
						target.setPosition(targetNewPos2.x, targetNewPos2.y, targetNewPos2.z);

					}
					//					else {
					//						target.setPosition(targetNewPos.x, targetNewPos.y, targetNewPos.z);
					//					}

					world.playSound(target.posX, target.posY, target.posZ, WizardrySounds.ENTITY_FORCEFIELD_DEFLECT,
							WizardrySounds.SPELLS, 0.3f, 1.3f, false);

					if (!world.isRemote) {
						// Player motion is handled on that player's client so needs packets
						if (target instanceof EntityPlayerMP) {
							((EntityPlayerMP) target).connection.sendPacket(new SPacketEntityVelocity(target));
						}

					} else {

						Vec3d relativeImpactPos = targetRelativePos.normalize().scale(radius);

						float yaw = (float) Math.atan2(relativeImpactPos.x, -relativeImpactPos.z);
						float pitch = (float) Math.asin(relativeImpactPos.y / radius);

						ParticleBuilder.create(Type.FLASH).pos(this.getPositionVector().add(relativeImpactPos))
								.time(6).face((float) (yaw * 180 / Math.PI), (float) (pitch * 180 / Math.PI))
								.clr(0.9f, 0.95f, 1).spawn(world);

						for (int i = 0; i < 12; i++) {

							float yaw1 = yaw + 0.3f * (rand.nextFloat() - 0.5f) - (float) Math.PI / 2;
							float pitch1 = pitch + 0.3f * (rand.nextFloat() - 0.5f);

							float brightness = rand.nextFloat();

							double r = radius + 0.05;
							double x = this.posX + r * MathHelper.cos(yaw1) * MathHelper.cos(pitch1);
							double y = this.posY + r * MathHelper.sin(pitch1);
							double z = this.posZ + r * MathHelper.sin(yaw1) * MathHelper.cos(pitch1);

							ParticleBuilder.create(Type.DUST).pos(x, y, z).time(6 + rand.nextInt(6))
									.face((float) (yaw1 * 180 / Math.PI) + 90, (float) (pitch1 * 180 / Math.PI)).scale(1.5f)
									.clr(0.7f + 0.3f * brightness, 0.85f + 0.15f * brightness, 1).spawn(world);
						}
					}
				}
			}
		}
		entityCache = targets;
	}

	@Override
	public boolean contains(Vec3d vec) {
		return vec.distanceTo(this.getPositionVector()) < radius; // The surface counts as outside
	}

	/**
	 * Returns true if the given bounding box is completely inside this forcefield (the surface counts as outside).
	 */
	public boolean contains(AxisAlignedBB box) {
		return Arrays.stream(GeometryUtils.getVertices(box)).allMatch(this::contains);
	}

	/**
	 * Returns true if the given entity is completely inside this forcefield (the surface counts as outside).
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

	@Override
	public void writeSpawnData(ByteBuf data) {
		super.writeSpawnData(data);
		data.writeFloat(getRadius());
	}

	@Override
	public boolean isRidingOrBeingRiddenBy(Entity entityIn) {
		return entityIn instanceof EntityPlayer;
	}

	@Override
	public void readSpawnData(ByteBuf data) {
		super.readSpawnData(data);
		setRadius(data.readFloat());
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setFloat(RADIUS_TAG, radius);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		radius = nbt.getFloat(RADIUS_TAG);
	}

	@Override
	public boolean canRenderOnFire() {
		return false;
	}

	// Prevents any kind of interactions or attacks through the forcefield
	// We may as well include projectile damage for this, then it will act as a failsafe

	@SubscribeEvent
	public static void onLivingAttackEvent(LivingAttackEvent event) {

		//		if (event.getSource().getTrueSource() instanceof EntityPlayer && event.getSource().isProjectile()
		//				&& ItemArtefact.isArtefactActive((EntityPlayer) event.getSource().getTrueSource(), WizardryItems.ring_defender)) {
		//			return; // Players wearing a ring of the defender can shoot stuff as normal, so don't cancel the event
		//		}
		//
		//		if (!event.getSource().isUnblockable() && event.getSource().getTrueSource() != null && event.getEntityLiving() != null
		//				&& !(event.getSource().getImmediateSource() instanceof EntityArcaneBarrier)) { // If the damage was from a forcefield that's ok
		//			// This condition will be false if both entities are outside a forcefield or both are in the same one
		//			if (getSurroundingForcefield(event.getEntityLiving()) != getSurroundingForcefield(event.getSource().getTrueSource())) {
		//				event.setCanceled(true);
		//			}
		//		}
	}

	@Nullable
	private static EntityArcaneBarrier getSurroundingForcefield(World world, Vec3d vec) {

		double searchRadius = 20;

		List<EntityArcaneBarrier> forcefields = EntityUtils.getEntitiesWithinRadius(searchRadius, vec.x,
				vec.y, vec.z, world, EntityArcaneBarrier.class);

		forcefields.removeIf(f -> !f.contains(vec));
		// There should only be one left at this point since we now have anti-overlap, but commands might bypass that
		return forcefields.stream().min(Comparator.comparingDouble(f -> vec.squareDistanceTo(f.getPositionVector())))
				.orElse(null);
	}

	@Nullable
	private static EntityArcaneBarrier getSurroundingForcefield(World world, AxisAlignedBB box, Vec3d vec) {

		double searchRadius = 20;

		List<EntityArcaneBarrier> forcefields = EntityUtils.getEntitiesWithinRadius(searchRadius, vec.x,
				vec.y, vec.z, world, EntityArcaneBarrier.class);

		forcefields.removeIf(f -> !f.contains(box));
		// There should only be one left at this point since we now have anti-overlap, but commands might bypass that
		return forcefields.stream().min(Comparator.comparingDouble(f -> vec.squareDistanceTo(f.getPositionVector())))
				.orElse(null);
	}

	@Nullable
	private static EntityArcaneBarrier getSurroundingForcefield(Entity entity) {
		return getSurroundingForcefield(entity.world, entity.getEntityBoundingBox(), entity.getPositionVector());
	}

	@SubscribeEvent
	public static void onPlayerInteractEvent(PlayerInteractEvent event) {

		if (!event.isCancelable())
			return; // We don't care about clicking empty space

		// For some reason block bounding boxes are relative whereas entity bounding boxes are absolute
		AxisAlignedBB box = event.getWorld().getBlockState(event.getPos()).getBoundingBox(event.getWorld(), event.getPos())
				.offset(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ());

		if (event instanceof PlayerInteractEvent.EntityInteract) {
			box = ((PlayerInteractEvent.EntityInteract) event).getTarget().getEntityBoundingBox();
		} else if (event instanceof PlayerInteractEvent.EntityInteractSpecific) {
			box = ((PlayerInteractEvent.EntityInteractSpecific) event).getTarget().getEntityBoundingBox();
		}

		// If the player is trying to interact across a forcefield boundary, cancel the event
		// The most pragmatic solution here is to use the centres - it's not perfect, but it's simple!
		if (getSurroundingForcefield(event.getWorld(), GeometryUtils.getCentre(box))
				!= getSurroundingForcefield(event.getWorld(), event.getEntityPlayer().getPositionVector())) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onExplosionEvent(ExplosionEvent event) {

		EntityArcaneBarrier forcefield = getSurroundingForcefield(event.getWorld(), event.getExplosion().getPosition());
		// Not a particularly efficient way of doing it but explosions are laggy anyway, and the code is neat :P
		event.getExplosion().getAffectedBlockPositions().removeIf(p -> getSurroundingForcefield(event.getWorld(),
				new Vec3d(p).add(0.5, 0.5, 0.5)) != forcefield);

		event.getExplosion().getPlayerKnockbackMap().keySet().removeIf(p -> getSurroundingForcefield(p) != forcefield);
	}

	public enum Colour {
		MAGENTA(13, 0.67f, 0.28f, 0.85f),
		RED(1, 0.85f, 0.27f, 0.27f),
		GREEN(2, 0.2f, 0.92f, 0.22f),
		ORANGE(14, 0.92f, 0.69f, 0.2f),
		BLUE(2, 0.27f, 0.31f, 0.85f);

		private int colourIndex;
		private final float r;
		private final float g;
		private final float b;

		Colour(int colourIndex, float r, float g, float b) {
			this.colourIndex = colourIndex;
			this.r = r;
			this.g = g;
			this.b = b;
		}

		public static Colour getByIndex(int index) {
			for (int i = 0; i < values().length; i++) {
				if (values()[i].colourIndex == index) {
					return values()[i];
				}
			}
			return Colour.MAGENTA;
		}

		public float getR() { return r; }

		public float getG() { return g; }

		public float getB() { return b; }
	}

}
