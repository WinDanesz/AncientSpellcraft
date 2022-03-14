package com.windanesz.ancientspellcraft.entity.projectile;

import com.windanesz.ancientspellcraft.material.IDevoritium;
import com.windanesz.ancientspellcraft.registry.ASPotions;
import com.windanesz.ancientspellcraft.registry.ASSounds;
import electroblob.wizardry.entity.construct.EntityMagicConstruct;
import electroblob.wizardry.entity.projectile.EntityBomb;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.List;

public class EntityDevoritiumBomb extends EntityBomb implements IDevoritium {

	public EntityDevoritiumBomb(World world){
		super(world);
	}

	@Override
	public int getLifetime(){
		return -1;
	}

	@Override
	protected void onImpact(RayTraceResult rayTrace){

		// Particle effect
		if(world.isRemote){
			
			ParticleBuilder.create(Type.FLASH).pos(this.getPositionVector()).scale(5 * blastMultiplier).clr(0, 0, 0).spawn(world);
			
			this.world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.posX, this.posY, this.posZ, 0, 0, 0);
						
			for(int i = 0; i < 60 * blastMultiplier; i++){

				float brightness = rand.nextFloat() * 0.1f + 0.1f;

				brightness = rand.nextFloat() * 0.3f;
				ParticleBuilder.create(Type.SPARKLE, rand, posX, posY, posZ, 2*blastMultiplier, false)
				.clr(brightness, brightness, brightness).spawn(world);

			}
		}

		if(!this.world.isRemote){

			this.playSound(ASSounds.DEVORITIUM_BOMB_HIT, 1.5F, rand.nextFloat() * 0.4F + 0.6F);
			this.playSound(WizardrySounds.ENTITY_SMOKE_BOMB_SMOKE, 1.2F, 1.0f);

			double range = Spells.smoke_bomb.getProperty(Spell.BLAST_RADIUS).floatValue() * blastMultiplier;

			List<EntityLivingBase> targets = EntityUtils.getLivingWithinRadius(range, this.posX, this.posY,
					this.posZ, this.world);

			int duration = Spells.smoke_bomb.getProperty(Spell.EFFECT_DURATION).intValue();

			for(EntityLivingBase target : targets){
				damageSummonedCreature(target, 3);
				target.addPotionEffect(new PotionEffect(ASPotions.magical_exhaustion, duration, 2));

			}

			List<EntityMagicConstruct> constructTargets = EntityUtils.getEntitiesWithinRadius(4, posX, posY, posZ, world, EntityMagicConstruct.class);
			if (!constructTargets.isEmpty()) {
				for (EntityMagicConstruct constructTarget : constructTargets) {
						int oldLifetime = constructTarget.lifetime;

						if (oldLifetime < 0 && world.rand.nextFloat() <= 0.5) {
							constructTarget.setDead();
						} else {
							constructTarget.lifetime = (int)(oldLifetime * 0.3f);
						}

				}
			}


			EntityAreaEffectCloud entityareaeffectcloud = new EntityAreaEffectCloud(this.world, this.posX, this.posY, this.posZ);
			entityareaeffectcloud.setRadius(3.5F);
			entityareaeffectcloud.setRadiusOnUse(-0.5F);
			entityareaeffectcloud.setWaitTime(10);
			entityareaeffectcloud.setDuration(entityareaeffectcloud.getDuration() / 2);
			entityareaeffectcloud.setParticle(EnumParticleTypes.FALLING_DUST);
			entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
			entityareaeffectcloud.addEffect(new PotionEffect(ASPotions.magical_exhaustion, 60,2));

			this.world.spawnEntity(entityareaeffectcloud);


			this.setDead();
		}
	}
}
