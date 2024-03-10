package com.windanesz.ancientspellcraft.spell;

import com.google.common.collect.ImmutableMap;
import com.windanesz.ancientspellcraft.registry.ASSpells;
import com.windanesz.ancientspellcraft.util.SpellcastUtils;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.Map;

public class WarlockElementalSpellEffects {

	public static Map<Element, int[]> PARTICLE_COLOURS = ImmutableMap.<Element, int[]>builder()
			.put(Element.MAGIC, new int[]{0xfc0303, 0x400101, 0x9d2cf3, 0xffffff})
			.put(Element.FIRE, new int[]{0xff9600, 0xab3603, 0xd02700, 0xffffff})
			.put(Element.ICE, new int[]{0xf2fdff, 0xebfcff, 0xb5f4ff, 0xffffff})
			.put(Element.LIGHTNING, new int[]{0x579dcf, 0x2f3d52, 0x225474, 0xffffff})
			.put(Element.NECROMANCY, new int[]{0xa811ce, 0x540154, 0x382366, 0xffffff})
			.put(Element.EARTH, new int[]{0x75a808, 0x6e8014, 0x795c28, 0xffffff})
			.put(Element.SORCERY, new int[]{0x56e88e, 0x308264, 0x16a676, 0xffffff})
			.put(Element.HEALING, new int[]{0xfffff6, 0xfff69e, 0xffe163, 0xffffff})
			.build();

	private static final Map<Element, MagicDamage.DamageType> DAMAGE_TYPE = ImmutableMap.<Element, MagicDamage.DamageType>builder()
			.put(Element.MAGIC, MagicDamage.DamageType.MAGIC)
			.put(Element.FIRE, MagicDamage.DamageType.FIRE)
			.put(Element.ICE, MagicDamage.DamageType.FROST)
			.put(Element.LIGHTNING, MagicDamage.DamageType.SHOCK)
			.put(Element.NECROMANCY, MagicDamage.DamageType.WITHER)
			.put(Element.EARTH, MagicDamage.DamageType.POISON)
			.put(Element.SORCERY, MagicDamage.DamageType.FORCE)
			.put(Element.HEALING, MagicDamage.DamageType.RADIANT)
			.build();

	public static ImmutableMap<Element, ResourceLocation> ELEMENTAL_PARTICLES = ImmutableMap.<Element, ResourceLocation>builder()
			.put(Element.MAGIC, ParticleBuilder.Type.SPARK)
			.put(Element.FIRE, ParticleBuilder.Type.MAGIC_FIRE)
			.put(Element.ICE, ParticleBuilder.Type.SNOW)
			.put(Element.LIGHTNING, ParticleBuilder.Type.SPARK)
			.put(Element.NECROMANCY, ParticleBuilder.Type.SPARKLE)
			.put(Element.EARTH, ParticleBuilder.Type.LEAF)
			.put(Element.SORCERY, ParticleBuilder.Type.FLASH)
			.put(Element.HEALING, ParticleBuilder.Type.SPARKLE)
			.build();

	public static MagicDamage.DamageType getDamageType(Element element) {
		return DAMAGE_TYPE.get(element);
	}

	public static ResourceLocation getElementalParticle(Element element) {
		return ELEMENTAL_PARTICLES.get(element);
	}

	public static void affectEntity(EntityLivingBase target, Element element, EntityLivingBase caster, SpellModifiers modifiers, boolean damageEntity) {
		World world = target.world;
		switch (element) {
			case MAGIC:
				if (damageEntity && !MagicDamage.isEntityImmune(MagicDamage.DamageType.MAGIC, target)) {
					EntityUtils.attackEntityWithoutKnockback(target, MagicDamage.causeDirectMagicDamage(caster, MagicDamage.DamageType.MAGIC), 4);
				}
				break;
			case FIRE:
				if (!target.isBurning()) {
					target.setFire(4);
				}
				if (damageEntity && !MagicDamage.isEntityImmune(MagicDamage.DamageType.FIRE, target)) {
					EntityUtils.attackEntityWithoutKnockback(target, MagicDamage.causeDirectMagicDamage(caster, MagicDamage.DamageType.FIRE), 2);
				}
				break;
			case ICE:
				target.addPotionEffect(new PotionEffect(WizardryPotions.frost, 60));
				if (damageEntity && !MagicDamage.isEntityImmune(MagicDamage.DamageType.FROST, target)) {
					EntityUtils.attackEntityWithoutKnockback(target, MagicDamage.causeDirectMagicDamage(caster, MagicDamage.DamageType.FROST), 2);
				}
				break;
			case LIGHTNING:
				if(target.world.isRemote){
					// Rather neatly, the entity can be set here and if it's null nothing will happen.
					ParticleBuilder.spawnShockParticles(target.world, target.posX, target.posY + target.height/2, target.posZ);
				}

				// This is a lot neater than it was, thanks to the damage type system.
				if (damageEntity && !MagicDamage.isEntityImmune(MagicDamage.DamageType.SHOCK, target)) {
					target.attackEntityFrom(MagicDamage.causeDirectMagicDamage(caster, MagicDamage.DamageType.SHOCK),
							3);
				}
				break;
			case NECROMANCY:
				if (damageEntity && !MagicDamage.isEntityImmune(MagicDamage.DamageType.WITHER, target)) {
					EntityUtils.attackEntityWithoutKnockback(target, MagicDamage.causeDirectMagicDamage(caster, MagicDamage.DamageType.WITHER), 2);
				}
				if (!target.isPotionActive(MobEffects.WITHER)) {
					target.addPotionEffect(new PotionEffect(MobEffects.WITHER, 40, 1));
				}
				break;
			case EARTH:
				if (damageEntity && !MagicDamage.isEntityImmune(MagicDamage.DamageType.POISON, target)) {
					EntityUtils.attackEntityWithoutKnockback(target, MagicDamage.causeDirectMagicDamage(caster, MagicDamage.DamageType.POISON), 2);
				}
				if (!target.isPotionActive(MobEffects.POISON)) {
					target.addPotionEffect(new PotionEffect(MobEffects.POISON, 60));
				}
				break;
			case SORCERY:
				if (!MagicDamage.isEntityImmune(MagicDamage.DamageType.FORCE, target)) {
					modifiers.set(SpellModifiers.POTENCY, damageEntity ? 0.8f : 0, false);
					if (caster instanceof EntityPlayer) {
						SpellcastUtils.proxyTargetedSpell(world, caster, target, ASSpells.force_shove, modifiers);
					} else {
						SpellcastUtils.tryCastSpellAsMob((EntityLiving) caster, ASSpells.force_shove, target);
					}
				}
				break;

			case HEALING:
				if (damageEntity && !MagicDamage.isEntityImmune(MagicDamage.DamageType.RADIANT, target)) {
					EntityUtils.attackEntityWithoutKnockback(target, MagicDamage.causeDirectMagicDamage(caster, MagicDamage.DamageType.RADIANT), 2);
				}
				if (!target.isPotionActive(MobEffects.BLINDNESS)) {
					target.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 40));
				}
				if (target.isEntityUndead() && !target.isBurning()) {
					target.setFire(4);
				}
				break;

		}
	}

}
