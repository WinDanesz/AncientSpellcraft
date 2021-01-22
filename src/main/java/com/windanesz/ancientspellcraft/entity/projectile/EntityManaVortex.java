package com.windanesz.ancientspellcraft.entity.projectile;

import com.windanesz.ancientspellcraft.item.ItemNewArtefact;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftSpells;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.client.DrawingUtils;
import electroblob.wizardry.entity.construct.EntityScaledConstruct;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.MagicDamage.DamageType;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.List;

public class EntityManaVortex extends EntityScaledConstruct {

	private double velX, velZ;

	public EntityManaVortex(World world) {
		super(world);
		setSize(Spells.tornado.getProperty(Spell.EFFECT_RADIUS).floatValue() * 0.6f, 0.05f);
		this.isImmuneToFire = true;
	}

	@Override
	protected boolean shouldScaleHeight() {
		return false;
	}

	public void setHorizontalVelocity(double velX, double velZ) {
		this.velX = velX;
		this.velZ = velZ;
	}

	@Override
	public void onUpdate() {

		super.onUpdate();

		double radius = width / 2 * 2;

		if (this.ticksExisted % 120 == 1 && world.isRemote) {
			//			Wizardry.proxy.playMovingSound(this, WizardrySounds.ENTITY_TORNADO_AMBIENT, WizardrySounds.SPELLS, 1.0f, 1.0f, false);
		}

		this.move(MoverType.SELF, velX, motionY, velZ);
		if (!this.world.isRemote) {

			List<EntityLivingBase> targets = EntityUtils.getLivingWithinRadius(radius, this.posX, this.posY,
					this.posZ, this.world);

			float damage = 0;
			if (!targets.isEmpty()) {
				damage = AncientSpellcraftSpells.mana_vortex.getProperty(Spell.DAMAGE).floatValue() * damageMultiplier;

				// reduce on-hit damage if the artefact is active
				if (getCaster() instanceof EntityPlayer && ItemNewArtefact.isNewArtefactActive((EntityPlayer) getCaster(), AncientSpellcraftItems.belt_vortex)) {
					damage = damage * 0.5f;
				}
			}

			for (EntityLivingBase target : targets) {

				if (target instanceof EntityPlayer && ((getCaster() instanceof EntityPlayer && !Wizardry.settings.playersMoveEachOther)
						|| ItemArtefact.isArtefactActive((EntityPlayer) target, WizardryItems.amulet_anchoring))) {
					continue;
				}

				if (this.isValidTarget(target)) {
					if (this.getCaster() != null) {
						target.attackEntityFrom(MagicDamage.causeIndirectMagicDamage(this, getCaster(),
								DamageType.MAGIC), damage);
					} else {
						target.attackEntityFrom(DamageSource.MAGIC, damage);
					}

					EntityUtils.applyStandardKnockback(this, target, 0.1f);

				}
			}
		} else {
			for (int j = 0; j < 60; j++) {
				float r = world.rand.nextFloat();
				double speed = 0.02 / r * (1 + world.rand.nextDouble());//(world.rand.nextBoolean() ? 1 : -1) * (0.05 + 0.02 * world.rand.nextDouble());
				ParticleBuilder.create(Type.CLOUD)
						.entity(this)
						.pos(0, 1, 0)
						.clr(DrawingUtils.mix(DrawingUtils.mix(0xfffef5, 0xbd6fde, r / 0.7f), DrawingUtils.mix(0xbb57cd, 0x4a005c, r / 0.7f), (r - 0.6f) / 0.2f))
						.spin((r * (radius - 1) + 0.5), speed)
						.scale(0.2f)
						.spawn(world);
			}

			for (int j = 0; j < 5; j++) {
				radius = width / 10;
				float r = world.rand.nextFloat();
				double speed = 0.02 / r * (1 + world.rand.nextDouble());
				ParticleBuilder.create(Type.CLOUD)
						.entity(this)
						.pos(0, 1, 0)
						.clr(DrawingUtils.mix(0xf7f7f7, 0xffffd9, r / 0.7f))
						.spin(r * (radius - 1) + 0.5, speed)
						.scale(0.4f)
						.spawn(world);
			}

		}
	}

	public void despawn() {
		if (world.isRemote) {
			ParticleBuilder.create(Type.SPHERE)
					.pos(this.posX, this.posY + 1, this.posZ)
					.scale(width / 2 * 2 * 0.8f)
					.clr(DrawingUtils.mix(0xbb57cd, 0x4a005c, 0.7f))
					.spawn(world);

			double particleX, particleZ;

			for (int i = 0; i < 40; i++) {

				particleX = this.posX - 1.0d + 2 * world.rand.nextDouble();
				particleZ = this.posZ - 1.0d + 2 * world.rand.nextDouble();
				ParticleBuilder.create(Type.CLOUD)
						.pos(particleX, this.posY + 1, particleZ)
						.vel((particleX - this.posX) * 0.3, 0, (particleZ - this.posZ) * 0.3)
						.clr(DrawingUtils.mix(0xbb57cd, 0x4a005c, 0.7f))
						.scale(0.15f)
						.time(20)
						.spawn(world);

				particleX = this.posX - 1.0d + 2 * world.rand.nextDouble();
				particleZ = this.posZ - 1.0d + 2 * world.rand.nextDouble();
				ParticleBuilder.create(Type.SPARKLE).pos(particleX, this.posY + 1, particleZ).fade(0x5e5d5e)
						.vel((particleX - this.posX) * 0.7f, 0, (particleZ - this.posZ) * 0.7f).time(15).clr(DrawingUtils.mix(0xbb57cd, 0x4a005c, 0.7f)).spawn(world);
			}
		} else {
			float explosion = 1;
			if (getCaster() instanceof EntityPlayer && ItemNewArtefact.isNewArtefactActive((EntityPlayer) getCaster(), AncientSpellcraftItems.belt_vortex)) {
				explosion = 3.2f;
			}
			world.createExplosion(getCaster() != null ? getCaster() : this, this.posX, this.posY + 1, this.posZ, explosion, false);
		}
		super.despawn();
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		velX = nbttagcompound.getDouble("velX");
		velZ = nbttagcompound.getDouble("velZ");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setDouble("velX", velX);
		nbttagcompound.setDouble("velZ", velZ);
	}

	@Override
	public void writeSpawnData(ByteBuf data) {
		super.writeSpawnData(data);
		data.writeDouble(velX);
		data.writeDouble(velZ);
	}

	@Override
	public void readSpawnData(ByteBuf data) {
		super.readSpawnData(data);
		this.velX = data.readDouble();
		this.velZ = data.readDouble();
	}
}
