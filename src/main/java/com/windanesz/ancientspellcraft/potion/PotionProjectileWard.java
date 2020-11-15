package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.entity.projectile.EntityMagicArrow;
import electroblob.wizardry.entity.projectile.EntityMagicProjectile;
import electroblob.wizardry.potion.PotionMagicEffect;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class PotionProjectileWard extends PotionMagicEffect {

	private boolean deflectsMagic;
	private float deflectChance;
	private Potion potion;

	private static int MAGIC_PROJECTILE_CONSUME_AMOUNT = 20;
	private static int PHYSICAL_PROJECTILE_CONSUME_AMOUNT = 10;

	public PotionProjectileWard(Potion potion, String name, int liquidColor, boolean deflectsMagic, float deflectChance) {
		super(false, 0xfcfde4, new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/potion_icon_" + name + ".png"));
		setBeneficial();
		this.setPotionName("potion." + AncientSpellcraft.MODID + ":" + name);
		this.deflectsMagic = deflectsMagic;
		this.deflectChance = deflectChance;
		this.potion = potion;
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true; // Execute the effect every tick
	}

	@Override
	public void performEffect(EntityLivingBase entity, int strength) {
		if (entity != null) {

			List<Entity> projectiles = EntityUtils.getEntitiesWithinRadius(3.5, entity.posX, entity.posY, entity.posZ, entity.world, Entity.class);
			for (Entity projectile : projectiles) {
				if (!isProjectileOnGround(projectile)) {

					if (projectile instanceof EntityMagicProjectile) {

						// don't reflect the caster's own projectiles
						if (((EntityMagicProjectile) projectile).getThrower() == entity) {
							return;
						}

						if (deflectsMagic) {
							if (entity.world.rand.nextDouble() <= deflectChance) {

								if (projectile.getCustomNameTag().equals("marked") || ASUtils.attemptConsumeManaFromHand(entity, MAGIC_PROJECTILE_CONSUME_AMOUNT)) {
									projectile.setCustomNameTag("marked");
									onDeflect(entity, projectile);
								}
							} else {
								projectile.setCustomNameTag("marked");
							}
						}
					} else if (projectile instanceof IProjectile) {

						if (projectile instanceof EntityArrow && ((EntityArrow) projectile).shootingEntity == entity) {
							return;
						}

						if (entity.world.rand.nextDouble() <= deflectChance) {

							if (projectile instanceof EntityMagicArrow || projectile.getCustomNameTag().equals("marked") || ASUtils.attemptConsumeManaFromHand(entity, PHYSICAL_PROJECTILE_CONSUME_AMOUNT)) {
								projectile.setCustomNameTag("marked");
								if (projectile instanceof EntityArrow) {
									((EntityArrow) projectile).pickupStatus = EntityArrow.PickupStatus.DISALLOWED;
								}

								onDeflect(entity, projectile);
							}
						} else {
							projectile.setCustomNameTag("marked");
						}
					}
				}
			}
		}

	}

	private boolean isProjectileOnGround(Entity projectile) {
		return projectile.world.collidesWithAnyBlock(projectile.getEntityBoundingBox().grow(0.05D));
	}

	private void onDeflect(EntityLivingBase entity, Entity projectile) {
		deflectProjectile(entity, projectile);
		if (entity.world.isRemote)
			spawnParticle(entity, projectile);
	}

	private void spawnParticle(Entity entity, Entity projectile) {
		ParticleBuilder.create(ParticleBuilder.Type.FLASH).entity(projectile).pos(0, 0.3, 0).clr(255, 255, 255).time(5).scale(2).spawn(projectile.world);

		ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(projectile.posX, projectile.posY, projectile.posZ).clr(255, 255, 235).scale(2).time(5).
				entity(entity).
				pos(projectile.getLookVec().x, entity.getEyeHeight(), projectile.getLookVec().z * -1).
				spawn(entity.world);
	}

	private void deflectProjectile(EntityLivingBase entity, Entity projectile) {
		Vec3d centre = entity.getPositionEyes(0).subtract(0, 0.1, 0);
		Vec3d vec = projectile.getPositionVector().subtract(centre).normalize().scale(0.6);
		projectile.addVelocity(vec.x, vec.y, vec.z);
	}

}
