package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftPotions;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class WizardShield extends Spell {

	public WizardShield() {

		super(AncientSpellcraft.MODID, "wizard_shield", SpellActions.POINT_UP, true);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		int amplifier = 0;

		if (caster.isPotionActive(AncientSpellcraftPotions.wizard_shield)) {
			PotionEffect effect = caster.getActivePotionEffect(AncientSpellcraftPotions.wizard_shield);
			if (effect != null) {
				amplifier = effect.getAmplifier();
			}
		}

		if (ticksInUse <= 80 && ticksInUse % 2 == 0) {
			caster.addPotionEffect(new PotionEffect(AncientSpellcraftPotions.wizard_shield, 80, amplifier + 1));
		} else if (ticksInUse % 40 == 0 && amplifier <= 15) {
			caster.addPotionEffect(new PotionEffect(AncientSpellcraftPotions.wizard_shield, 80, amplifier + 1));
		}
		return true;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
