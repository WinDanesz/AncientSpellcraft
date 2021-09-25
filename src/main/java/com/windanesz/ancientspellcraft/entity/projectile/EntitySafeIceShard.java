package com.windanesz.ancientspellcraft.entity.projectile;

import electroblob.wizardry.entity.projectile.EntityIceShard;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.MagicDamage.DamageType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntitySafeIceShard extends EntityIceShard {

	/** Creates a new ice shard in the given world. */
	public EntitySafeIceShard(World world){
		super(world);
	}

	@Override
	public void onEntityHit(EntityLivingBase entityHit){

		// Won't hurt the caster anymore
		if (getCaster() != null && getCaster() == entityHit) {
			return;
		}

		// Adds a freeze effect to the target.
		if(!MagicDamage.isEntityImmune(DamageType.FROST, entityHit))
			entityHit.addPotionEffect(new PotionEffect(WizardryPotions.frost,
					Spells.ice_shard.getProperty(Spell.EFFECT_DURATION).intValue(),
					Spells.ice_shard.getProperty(Spell.EFFECT_STRENGTH).intValue()));

		this.playSound(WizardrySounds.ENTITY_ICE_SHARD_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
	}
}