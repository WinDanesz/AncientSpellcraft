package com.windanesz.ancientspellcraft.potion;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.potion.PotionMagicEffect;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class PotionUnlimitedPower extends PotionMagicEffect {

	public PotionUnlimitedPower() {
		super(false, 0xe65aff, new ResourceLocation(AncientSpellcraft.MODID, "textures/gui/potion_unlimited_power.png"));
	}

	/**
	 * The fraction by which stats are increased per level of the effect.
	 */
	public static final float IF_POTENCY_PER_LEVEL = 0.5f;
	public static final float IF_MODIFIERS_PER_LEVEL = 1f;

	//from EmpoweringPresence.onSpellCastPreEvent()
	@SubscribeEvent(priority = EventPriority.NORMAL) // Doesn't really matter but there's no point processing it if casting is blocked
	public static void onSpellCastPreEvent(SpellCastEvent.Pre event) {
		// Unlimited Power stacks extra potency on top of the existing potency.
		if (event.getCaster() != null && event.getCaster().isPotionActive(AncientSpellcraftPotions.unlimited_power)
			//			&& !(event.getSpell() instanceof EmpoweringPresence) // Prevent exponential empowerment stacking!
		) {
			int amplifier = event.getCaster().getActivePotionEffect(AncientSpellcraftPotions.unlimited_power).getAmplifier() + 1;
			float potency = 1 + IF_POTENCY_PER_LEVEL * amplifier;
			float upgrade_modifier = 1 + IF_MODIFIERS_PER_LEVEL * amplifier;

			event.getModifiers()
					.set(SpellModifiers.POTENCY, event.getModifiers().get(SpellModifiers.POTENCY) * potency, true)
					.set(WizardryItems.blast_upgrade, event.getModifiers().get(WizardryItems.blast_upgrade) * upgrade_modifier, true)
					.set(WizardryItems.range_upgrade, event.getModifiers().get(WizardryItems.range_upgrade) * upgrade_modifier, true)
					.set(WizardryItems.duration_upgrade, event.getModifiers().get(WizardryItems.duration_upgrade) * upgrade_modifier, true)
					.set(WizardryItems.cooldown_upgrade, event.getModifiers().get(WizardryItems.cooldown_upgrade) * upgrade_modifier, true);
		}
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

	@Override
	public void performEffect(EntityLivingBase entitylivingbase, int amplifier) {
		if (!entitylivingbase.world.isRemote) {
			if (amplifier >= 0) {
				if (!entitylivingbase.isPotionActive(MobEffects.REGENERATION)) {
					entitylivingbase.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 60, 1));
				}
				entitylivingbase.addPotionEffect(new PotionEffect(MobEffects.GLOWING, 20));
			}
			if (amplifier >= 1) {
				entitylivingbase.addPotionEffect(new PotionEffect(MobEffects.LEVITATION, 5));
			}
		}
		if (entitylivingbase.world.isRemote) {
			World world = entitylivingbase.world;
			for (int i = 0; i < 10; i++) {
				double dx = (world.rand.nextDouble() * 2 - 1) * 3;
				double dy = (world.rand.nextDouble() * 2 - 1) * 3;
				double dz = (world.rand.nextDouble() * 2 - 1) * 3;
				// These particles use the velocity args differently; they behave more like portal particles
				world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, entitylivingbase.posX, entitylivingbase.posY + 1, entitylivingbase.posZ, dx, dy, dz);
			}
		}
	}

	@Override
	public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier) {
		super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);

		// add side effects followed by the potion effect
		addAfterEffects(entityLivingBaseIn, amplifier);
	}

	//	private void addActiveEffects(EntityLivingBase entitylivingbase, int strength) {
	//
	//	}

	private void addAfterEffects(EntityLivingBase entitylivingbase, int amplifier) {
		float durationTicks = amplifier * 20;
//		entitylivingbase.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 200));
//		entitylivingbase.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 200));
//		entitylivingbase.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200));
	}
}
