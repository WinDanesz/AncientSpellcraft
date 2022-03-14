package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASPotions;
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

		if (caster.isPotionActive(ASPotions.projectile_ward)) {
			caster.removePotionEffect(ASPotions.projectile_ward);
		}
		if (caster.isPotionActive(ASPotions.bulwark)) {
			caster.removePotionEffect(ASPotions.bulwark);
		}
		if (caster.isPotionActive(ASPotions.arcane_aegis)) {
			caster.removePotionEffect(ASPotions.arcane_aegis);
		}

		return super.applyEffects(caster, modifiers);
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
