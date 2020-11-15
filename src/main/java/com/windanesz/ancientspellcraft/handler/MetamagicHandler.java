package com.windanesz.ancientspellcraft.handler;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftSpells;
import com.windanesz.ancientspellcraft.spell.MetaSpellBuff;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Map;

import static electroblob.wizardry.constants.Constants.*;

@Mod.EventBusSubscriber
public final class MetamagicHandler {

	private MetamagicHandler() {} // no instances

	public enum MetaEffect {

		ARCANE_AUGMENTATION(AncientSpellcraftPotions.arcane_augmentation),
		INTENSIFYING_FOCUS(AncientSpellcraftPotions.intensifying_focus);

		private final Potion effect;

		MetaEffect(Potion effect) {
			this.effect = effect;
		}

		/**
		 * Returns the spell type with the given name, or throws an {@link java.lang.IllegalArgumentException} if no such
		 * spell type exists.
		 */
		public static MetaEffect fromPotion(Potion potion) {

			for (MetaEffect effect : values()) {
				if (effect.effect.equals(potion))
					return effect;
			}

			throw new IllegalArgumentException("No such metamagic type");
		}

		public Potion getEffect() {
			return effect;
		}

		@SideOnly(Side.CLIENT)
		public String getDisplayName() {
			return net.minecraft.client.resources.I18n.format("spelltype." + effect);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH) // processing after electroblob.wizardry.item.ItemArtefact.onSpellCastPreEvent (EventPriority.LOW)
	public static void onSpellCastPreEvent(SpellCastEvent.Pre event) {
		if (event.getCaster() instanceof EntityPlayer && !(event.getSpell() instanceof MetaSpellBuff)) {

			EntityPlayer player = (EntityPlayer) event.getCaster();

			Map<Potion, PotionEffect> potionEffectMap = player.getActivePotionMap();

			for (Map.Entry<Potion, PotionEffect> entry : potionEffectMap.entrySet()) {

				if (entry.getKey().equals(AncientSpellcraftPotions.arcane_augmentation)) {
					PotionEffect effect = entry.getValue();
					SpellModifiers modifiers = event.getModifiers();

					float range = modifiers.get(WizardryItems.range_upgrade);
					float blast = modifiers.get(WizardryItems.blast_upgrade);

					int level = effect.getAmplifier() + 1;

					if (level > 0) {
						modifiers.set(WizardryItems.range_upgrade, blast + level * Constants.RANGE_INCREASE_PER_LEVEL, true);
						modifiers.set(WizardryItems.blast_upgrade, range + level * Constants.BLAST_RADIUS_INCREASE_PER_LEVEL, true);
					}
					onMetaMagicFinished(player, AncientSpellcraftSpells.arcane_augmentation, AncientSpellcraftPotions.arcane_augmentation);

					break;
				}

				if (entry.getKey().equals(AncientSpellcraftPotions.intensifying_focus)) {
					PotionEffect effect = entry.getValue();
					SpellModifiers modifiers = event.getModifiers();

					float potency = modifiers.get(SpellModifiers.POTENCY);
					float range = modifiers.get(WizardryItems.range_upgrade);
					float blast = modifiers.get(WizardryItems.range_upgrade);

					int level = effect.getAmplifier() + 1;
					if (level > 0) {

						modifiers.set(SpellModifiers.POTENCY, potency + level * POTENCY_INCREASE_PER_TIER, true);
						modifiers.set(WizardryItems.range_upgrade, blast - level * Constants.RANGE_INCREASE_PER_LEVEL, true);
						modifiers.set(WizardryItems.blast_upgrade, range - level * Constants.BLAST_RADIUS_INCREASE_PER_LEVEL, true);
					}
					setCooldown(player, AncientSpellcraftSpells.intensifying_focus);
					player.removePotionEffect(AncientSpellcraftPotions.intensifying_focus);
					onMetaMagicFinished(player, AncientSpellcraftSpells.intensifying_focus, AncientSpellcraftPotions.intensifying_focus);

					break;
				}

				if (entry.getKey().equals(AncientSpellcraftPotions.continuity_charm)) {
					PotionEffect effect = entry.getValue();
					SpellModifiers modifiers = event.getModifiers();

					float duration = modifiers.get(WizardryItems.duration_upgrade);
					float cost = modifiers.get(SpellModifiers.COST);
					int level = effect.getAmplifier() + 1;
					if (level > 0) {

						modifiers.set(WizardryItems.duration_upgrade, duration + level * DURATION_INCREASE_PER_LEVEL, true);
						modifiers.set(SpellModifiers.COST, cost + level * COST_REDUCTION_PER_ARMOUR, true);
					}
					onMetaMagicFinished(player, AncientSpellcraftSpells.continuity_charm, AncientSpellcraftPotions.continuity_charm);
					break;
				}

			}

		}

	}

	private static void onMetaMagicFinished(EntityPlayer player, Spell spell, Potion effect) {
		setCooldown(player, spell);
		player.removePotionEffect(effect);
	}

	private static void setCooldown(EntityPlayer player, Spell spell) {

		List<ItemStack> wands = ASUtils.getAllHotbarWands(player);
		if (wands != null && !wands.isEmpty()) {

			for (ItemStack wand : wands) {
				int index = 0;

				for (Spell currentSpell : WandHelper.getSpells(wand)) {

					int[] cooldowns = WandHelper.getCooldowns(wand);

					if (cooldowns.length == 0) {
						int count = WandHelper.getSpells(wand).length - 1;
						cooldowns = new int[count];
					}

					if (currentSpell == spell) {

						int[] maxCooldowns = WandHelper.getMaxCooldowns(wand);
						if (maxCooldowns.length == 0) {
							int count = WandHelper.getSpells(wand).length - 1;
							maxCooldowns = new int[count];
						}

						cooldowns[index] = 1200;
						maxCooldowns[index] = 1200;
						WandHelper.setCooldowns(wand, cooldowns);
						WandHelper.setMaxCooldowns(wand, maxCooldowns);
					}
					index++;
				}
			}
		}
	}
}
