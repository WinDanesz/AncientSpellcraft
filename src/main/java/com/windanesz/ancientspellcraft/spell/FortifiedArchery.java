package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.potion.PotionMagicEffectAS;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class FortifiedArchery extends PotionMagicEffectAS {

	public static float AMPLIFIER = 1.4f;

	public FortifiedArchery(String name, boolean isBadEffect, int liquidColour, ResourceLocation texture) {
		super(name, isBadEffect, liquidColour, texture);
		this.setPotionName("potion." + AncientSpellcraft.MODID + ":" + name);

	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

	@Override
	public void performEffect(EntityLivingBase entity, int amplifier) {
		// Decreases the timer by the amount it should have been decreased while the bow was in use.
		List<EntityArrow> projectiles = EntityUtils.getEntitiesWithinRadius(3.5, entity.posX, entity.posY, entity.posZ, entity.world, EntityArrow.class);

		// grants +0,5 speed multiplier per amplifier lvl: amplifier lvl 0 grants * 1.5 speed, amplifier lvl 1 grants *2.0 speed, etc...
		//		float multiplier = 1 + ((amplifier + 1) / 2f);
		// grants +0,5 speed multiplier per amplifier lvl: amplifier lvl 0 grants * 1.5 speed, amplifier lvl 1 grants *2.0 speed, etc...
		float multiplier = 1 + ((amplifier + 1) / 2f);

		if (!projectiles.isEmpty()) {
			for (EntityArrow arrow : projectiles) {
				if (arrow.shootingEntity != null && arrow.shootingEntity.getUniqueID() == entity.getUniqueID() && !isProjectileOnGround(arrow)) {

					if (!arrow.getCustomNameTag().equals("Amplified Arrow")) {
						arrow.setCustomNameTag("Amplified Arrow");
						arrow.pickupStatus = EntityArrow.PickupStatus.DISALLOWED;
						arrow.addVelocity(arrow.motionX * AMPLIFIER, arrow.motionY * AMPLIFIER, arrow.motionZ * AMPLIFIER);
						try {
							spawnParticle(arrow);
						} catch (Exception e){ }
					}
				}
			}
		}

	}

	private boolean isProjectileOnGround(Entity projectile) {
		return projectile.world.collidesWithAnyBlock(projectile.getEntityBoundingBox().grow(0.05D));
	}

	private void spawnParticle(Entity projectile) {
		if (projectile != null && projectile.world.isRemote)
			ParticleBuilder.create(ParticleBuilder.Type.FLASH).entity(projectile).pos(0, 0.3, 0).clr(230, 184, 228).time(3).scale(2).spawn(projectile.world);
	}
}
