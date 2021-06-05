package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.integration.artemislib.ASArtemisLibIntegration;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.function.Supplier;

public class SpellResizeSelf extends SpellBuffAS {

	public SpellResizeSelf(String name, float r, float g, float b, Supplier<Potion>... effects) {
		super(name, r, g, b, effects);

		if (!ASArtemisLibIntegration.enabled()) {
			this.setEnabled(false);
		}
	}

	@Override
	protected boolean applyEffects(EntityLivingBase caster, SpellModifiers modifiers) {

		if (caster != null) {
			int modifier = caster.isSneaking() ? 0 : 1;

			for (Potion potion : potionSet) {
				int duration = (int) (getProperty(getDurationKey(potion)).floatValue() * modifiers.get(WizardryItems.duration_upgrade));

				if (caster instanceof EntityPlayer && ((
						potion == AncientSpellcraftPotions.shrinkage && ItemArtefact.isArtefactActive((EntityPlayer) caster, AncientSpellcraftItems.ring_permanent_shrinkage)) ||
						potion == AncientSpellcraftPotions.growth && ItemArtefact.isArtefactActive((EntityPlayer) caster, AncientSpellcraftItems.ring_permanent_growth))) {
					duration = Integer.MAX_VALUE;
				}

				caster.addPotionEffect(new PotionEffect(potion, duration, modifier, false, false));
			}
		}

		return true;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
