package com.windanesz.ancientspellcraft.entity;

import com.windanesz.ancientspellcraft.spell.SpellProjectileAOEPotion;
import electroblob.wizardry.entity.projectile.EntityMagicProjectile;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.MagicDamage.DamageType;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A projectile which can apply its effect where it hits an entity or the ground
 */
@Mod.EventBusSubscriber
public class EntityAOEProjectile extends EntityMagicProjectile {

	private int lifetime = 16;

	/**
	 * The spell related to this AoE projectile entity. used to determine the damage
	 */
	private SpellProjectileAOEPotion relatedSpell;

	public EntityAOEProjectile(World world) {
		super(world);
		this.setSize(0.5f, 0.5f);
	}

	public float getDamage() {
		return relatedSpell != null && relatedSpell.hasProperty(Spell.DAMAGE) ? relatedSpell.getProperty(Spell.DAMAGE).floatValue() : 0;
	}

	@Override
	protected void onImpact(@Nullable RayTraceResult rayTrace) {

		if (!world.isRemote) {

			for (EntityLivingBase entity : getEntitiesWithinRadius(rayTrace)) {
				affectEntity(entity);
			}
		} else {
			ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(this.posX, this.posY,this.posZ).scale(4f).time(60).clr(1, 1, 0.65f).fade(0.85f, 0.5f, 0.8f).spawn(world);		}


		//this.playSound(WizardrySounds.ENTITY_MAGIC_FIREBALL_HIT, 2, 0.8f + rand.nextFloat() * 0.3f);

			if (!world.isRemote)
				this.setDead();
	}

	private void affectEntity(Entity entity) {
		if (entity != null) {

			float damage = getDamage() * damageMultiplier;

			if (damage > 0) {
				entity.attackEntityFrom(getDamageSource(), damage);
			}

			if (!MagicDamage.isEntityImmune(relatedSpell.getDamageType(), entity)) {
				relatedSpell.applyPotionEffects((EntityLivingBase) entity, new SpellModifiers());
			}
		}
	}

	protected List<EntityLivingBase> getEntitiesWithinRadius(RayTraceResult rayTraceResult) {
		List<EntityLivingBase> affectedEntities = new ArrayList<>();
		BlockPos hitPos;
		if (rayTraceResult != null) {

			if (rayTraceResult.entityHit != null) {
				hitPos = rayTraceResult.entityHit.getPosition();
			} else {
				hitPos = rayTraceResult.getBlockPos().offset(rayTraceResult.sideHit);
			}

			affectedEntities = EntityUtils.getEntitiesWithinRadius(relatedSpell.getProperty(Spell.EFFECT_RADIUS).intValue(),
					hitPos.getX(), hitPos.getY(), hitPos.getZ(), world, EntityLivingBase.class).stream().filter(e -> e != thrower).collect(Collectors.toList());
		}
		return affectedEntities;
	}

	protected DamageSource getDamageSource() {
		return MagicDamage.causeIndirectMagicDamage(this, this.getThrower(), DamageType.MAGIC).setProjectile();
	}

	@Override
	public void onUpdate() {

		super.onUpdate();

		if (world.isRemote) {

			for (int i = 0; i < 5; i++) {

				double dx = (rand.nextDouble() - 0.5) * width;
				double dy = (rand.nextDouble() - 0.5) * height;
				double dz = (rand.nextDouble() - 0.5) * width;
				double v = 0.06;
				ParticleBuilder.create(relatedSpell.getParticle())
						.clr(relatedSpell.getR(), relatedSpell.getG(), relatedSpell.getB())
						.pos(this.getPositionVector().add(dx - this.motionX / 2, dy, dz - this.motionZ / 2))
						.vel(-v * dx, -v * dy, -v * dz).scale(width * 2).time(10).spawn(world);

				if (ticksExisted > 1) {
					dx = (rand.nextDouble() - 0.5) * width;
					dy = (rand.nextDouble() - 0.5) * height + this.height / 2 - 0.1;
					dz = (rand.nextDouble() - 0.5) * width;
					ParticleBuilder.create(relatedSpell.getParticle())
							.clr(relatedSpell.getR(), relatedSpell.getG(), relatedSpell.getB())
							.pos(this.getPositionVector().add(dx - this.motionX, dy, dz - this.motionZ))
							.vel(-v * dx, -v * dy, -v * dz).scale(width * 2).time(10).spawn(world);
				}
			}
		}
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public float getCollisionBorderSize() {
		return 1.0F;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {

		if (this.isEntityInvulnerable(source)) {
			return false;

		} else {

			this.markVelocityChanged();

			if (source.getTrueSource() != null) {

				Vec3d vec3d = source.getTrueSource().getLookVec();

				if (vec3d != null) {

					double speed = MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);

					this.motionX = vec3d.x * speed;
					this.motionY = vec3d.y * speed;
					this.motionZ = vec3d.z * speed;

					this.lifetime = 160;

				}

				if (source.getTrueSource() instanceof EntityLivingBase) {
					this.setCaster((EntityLivingBase) source.getTrueSource());
				}

				return true;

			} else {
				return false;
			}
		}
	}

	public void setRelatedSpell(SpellProjectileAOEPotion relatedSpell) {
		this.relatedSpell = relatedSpell;
	}

	public void setLifetime(int lifetime) {
		this.lifetime = lifetime;
	}

	@Override
	public int getLifetime() {
		return lifetime;
	}

	@Override
	public boolean hasNoGravity() {
		return true;
	}

	@Override
	public boolean canRenderOnFire() {
		return false;
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeInt(lifetime);
		buffer.writeInt(relatedSpell.networkID());
		super.writeSpawnData(buffer);
	}

	@Override
	public void readSpawnData(ByteBuf buffer) {
		lifetime = buffer.readInt();
		relatedSpell = (SpellProjectileAOEPotion) Spell.byNetworkID(buffer.readInt());
		super.readSpawnData(buffer);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		lifetime = nbt.getInteger("lifetime");
		relatedSpell = (SpellProjectileAOEPotion) Spell.get(nbt.getString("spell"));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("lifetime", lifetime);
		nbt.setString("spell", relatedSpell.getRegistryName().toString());
	}
}
