package com.windanesz.ancientspellcraft.entity.construct;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftSounds;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftSpells;
import electroblob.wizardry.entity.construct.EntityScaledConstruct;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.MagicDamage.DamageType;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class EntityHealingSigil extends EntityScaledConstruct {

	public EntityHealingSigil(World world){
		super(world);
		setSize(AncientSpellcraftSpells.healing_sigil.getProperty(Spell.EFFECT_RADIUS).floatValue() * 2, 0.2f);
	}

	@Override
	protected boolean shouldScaleHeight(){
		return false;
	}

	@Override
	public void onUpdate(){

		super.onUpdate();

		if(!this.world.isRemote){

			List<EntityLivingBase> targets = EntityUtils.getLivingWithinRadius(width/2, this.posX, this.posY,
					this.posZ, this.world);

			for(EntityLivingBase target : targets){

				if(this.getCaster() == null || this.getCaster() != null && AllyDesignationSystem.isAllied(this.getCaster(), target)){

					target.heal(AncientSpellcraftSpells.healing_sigil.getProperty(Spell.HEALTH).floatValue() * damageMultiplier);
					this.playSound(AncientSpellcraftSounds.ENTITY_HEALING_HEATH_HEALS, 1.0f, 1.0f);

				} else if(target.isEntityUndead()){

					double velX = target.motionX;
					double velY = target.motionY;
					double velZ = target.motionZ;

					if(this.getCaster() != null){
						target.attackEntityFrom(
								MagicDamage.causeIndirectMagicDamage(this, getCaster(), DamageType.RADIANT),
								Spells.healing_aura.getProperty(Spell.DAMAGE).floatValue() * damageMultiplier);
					}else{
						target.attackEntityFrom(DamageSource.MAGIC, AncientSpellcraftSpells.healing_sigil.getProperty(Spell.HEALTH).floatValue() * damageMultiplier);
					}

					// Removes knockback
					target.motionX = velX;
					target.motionY = velY;
					target.motionZ = velZ;
					this.playSound(AncientSpellcraftSounds.ENTITY_HEALING_HEATH_HEALS, 1.0f, 1.0f);
				}
				this.setDead();
			}
		}else if(this.rand.nextInt(15) == 0){
			for(int i=1; i<3; i++){
				float brightness = 0.5f + (rand.nextFloat() * 0.5f);
				double radius = rand.nextDouble() * (width/2);
				float angle = rand.nextFloat() * (float)Math.PI * 2;
				ParticleBuilder.create(ParticleBuilder.Type.SPARKLE)
						.pos(this.posX + radius * MathHelper.cos(angle), this.posY, this.posZ + radius * MathHelper.sin(angle))
						.vel(0, 0.05, 0)
						.time(48 + this.rand.nextInt(12))
						.clr(1.0f, 1.0f, brightness)
						.spawn(world);
			}
		}
	}

	@Override
	public boolean canRenderOnFire(){
		return false;
	}

}
