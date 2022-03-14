package com.windanesz.ancientspellcraft.entity.construct;

import com.windanesz.ancientspellcraft.registry.ASPotions;
import com.windanesz.ancientspellcraft.registry.ASSpells;
import electroblob.wizardry.entity.construct.EntityMagicConstruct;
import electroblob.wizardry.potion.Curse;
import electroblob.wizardry.potion.PotionMagicEffect;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityAntiMagicField extends EntityMagicConstruct {

	public EntityAntiMagicField(World world) {
		super(world);
		this.height = 0.1f;
		this.width = 0.1f;
	}

	public void onUpdate() {

		if (this.ticksExisted % 120 == 1) {
			this.playSound(WizardrySounds.ENTITY_BLIZZARD_AMBIENT, 1.0f, 1.0f);
		}

		super.onUpdate();

		// This is a good example of why you might define a spell base property without necessarily using it in the
		// spell - in fact, blizzard doesn't even have a spell class (yet)
		double radius = ASSpells.antimagic_field.getProperty(Spell.EFFECT_RADIUS).doubleValue();

		if (!this.world.isRemote) {

			List<EntityLivingBase> targets = EntityUtils.getEntitiesWithinRadius(radius, this.posX, this.posY,
					this.posZ, this.world, EntityLivingBase.class);

			for (EntityLivingBase target : targets) {

				// All entities are slowed, even the caster (except those immune to frost effects)
				target.addPotionEffect(new PotionEffect(ASPotions.magical_exhaustion, 40, 3));

				Map<Potion, PotionEffect> activeEffects = new HashMap<>(target.getActivePotionMap());
				for (Potion potion : activeEffects.keySet()) {
					if (potion instanceof PotionMagicEffect && !(potion instanceof Curse) && potion != ASPotions.magical_exhaustion) {
						target.removePotionEffect(potion);
					}
				}
			}

		} else {

			for (int i = 1; i < 6; i++) {
				float brightness = 0.5f + (rand.nextFloat() * 0.5f);
				//				radius = rand.nextDouble() * 2.0;
				radius = radius * rand.nextDouble();

				ParticleBuilder.create(ParticleBuilder.Type.DUST)
						.pos(this.posX + radius, this.posY * radius, this.posZ)
						.vel(0, 0.01, 0)
						.clr(0x5be3bb)
						.time(15)
						.scale(1F)
						.spawn(world);
			}
		}
	}
}
