package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;

import java.util.function.Supplier;

public class SpellWard extends SpellBuffAS {

	public SpellWard(String name, float r, float g, float b, Supplier<Potion>... effects) {
		super(name, r, g, b, effects);
	}

	@Override
	protected boolean applyEffects(EntityLivingBase caster, SpellModifiers modifiers) {
		// magelight and candlelight is mutually exclusive

		if (caster.isPotionActive(AncientSpellcraftPotions.projectile_ward)) {
			caster.removePotionEffect(AncientSpellcraftPotions.projectile_ward);
		}
		if (caster.isPotionActive(AncientSpellcraftPotions.bulwark)) {
			caster.removePotionEffect(AncientSpellcraftPotions.bulwark);
		}
		if (caster.isPotionActive(AncientSpellcraftPotions.arcane_aegis)) {
			caster.removePotionEffect(AncientSpellcraftPotions.arcane_aegis);
		}

		return super.applyEffects(caster, modifiers);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
