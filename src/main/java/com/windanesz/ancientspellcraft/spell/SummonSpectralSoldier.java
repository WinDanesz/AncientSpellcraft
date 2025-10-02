package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.living.EntityAnimatedItem;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class SummonSpectralSoldier extends Animate {

	public SummonSpectralSoldier() {
		super(AncientSpellcraft.MODID, "summon_spectral_soldier");
	}

	@Override
	protected void addMinionExtras(EntityAnimatedItem minion, BlockPos pos, @Nullable EntityLivingBase caster, SpellModifiers modifiers, int alreadySpawned) {
		super.addMinionExtras(minion, pos, caster, modifiers, alreadySpawned);

		// Equip the spectral soldier with basic spectral armor
		minion.setItemStackToSlot(EntityEquipmentSlot.HEAD, conjureItem(modifiers, WizardryItems.spectral_helmet));
		minion.setItemStackToSlot(EntityEquipmentSlot.CHEST, conjureItem(modifiers, WizardryItems.spectral_chestplate));
		minion.setItemStackToSlot(EntityEquipmentSlot.LEGS, conjureItem(modifiers, WizardryItems.spectral_leggings));
		minion.setItemStackToSlot(EntityEquipmentSlot.FEET, conjureItem(modifiers, WizardryItems.spectral_boots));

		// Equip with a spectral sword
		minion.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, conjureItem(modifiers, WizardryItems.spectral_sword));

		// Mark as having armor for display purposes
		minion.setHasArmour(true);

		// Apply balanced stats for an apprentice-level summon
		// Moderate attack damage boost
		IAttributeInstance attack_damage = minion.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		if (attack_damage != null) {
			attack_damage.applyModifier(
					new AttributeModifier(POTENCY_ATTRIBUTE_MODIFIER, 0.8 * (modifiers.get(SpellModifiers.POTENCY) - 1), EntityUtils.Operations.MULTIPLY_CUMULATIVE));
		}

		// Slightly reduced movement speed for balance
		IAttributeInstance speed = minion.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
		if (speed != null) {
			speed.applyModifier(
					new AttributeModifier("speed_modifier", -0.2f, EntityUtils.Operations.MULTIPLY_FLAT));
		}

		// Set health based on modifiers
		minion.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(
				new AttributeModifier(HEALTH_MODIFIER, 0.3 * modifiers.get(HEALTH_MODIFIER) - 1, EntityUtils.Operations.MULTIPLY_CUMULATIVE));
		minion.setHealth(minion.getMaxHealth());
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}