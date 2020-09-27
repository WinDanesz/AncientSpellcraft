package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellBuff;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class Candlelight extends SpellBuff {

	public Candlelight() {
		super(AncientSpellcraft.MODID, "candlelight", 216, 26, 11, () -> AncientSpellcraftPotions.candlelight);
		soundValues(0.7f, 1.2f, 0.4f);
	}

	/**
	 * <b>Overriding as we don't want to spawn particles.</b>
	 */
	protected boolean applyEffects(EntityLivingBase caster, SpellModifiers modifiers) {
		// magelight and candlelight is mutually exclusive
		if (caster.isPotionActive(AncientSpellcraftPotions.magelight)) {
			caster.removePotionEffect(AncientSpellcraftPotions.magelight);
		}

		for (Potion potion : potionSet) {
			caster.addPotionEffect(new PotionEffect(potion, potion.isInstant() ? 1 :
					(int) (getProperty(getDurationKey(potion)).floatValue() * modifiers.get(WizardryItems.duration_upgrade)),
					(int) getProperty(getStrengthKey(potion)).floatValue(),
					false, false));
		}

		return true;
	}

	@Override
	protected void spawnParticles(World world, EntityLivingBase entity, SpellModifiers modifiers) {
		//noop
	}

	/**
	 * Returns the number to be added to the potion amplifier(s) based on the given potency modifier. Override
	 * to define custom modifier handling. Delegates to {@link SpellBuff#getStandardBonusAmplifier(float)} by
	 * default.
	 * <b>Maximum level of this buff is 0</>
	 */
	protected int getBonusAmplifier(float potencyModifier) {
		return 0;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
