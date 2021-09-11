package com.windanesz.ancientspellcraft.entity.living;

import com.windanesz.ancientspellcraft.data.SpellCategorization;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellProperties;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.List;
import java.util.Random;

public interface IArmourClassWizard {

	/**
	 * This method was package private so we had to duplicate it
	 * <p>
	 * Adds n random spells to the given list. The spells will be of the given element if possible. Extracted as a
	 * separate function since it was the same in both EntityWizard and EntityEvilWizard.
	 *
	 * @param wizard The wizard whose spells are to be populated.
	 * @param spells The spell list to be populated.
	 * @param e      The element that the spells should belong to, or {@link Element#MAGIC} for a random element each time.
	 * @param master Whether to include master spells.
	 * @param n      The number of spells to add.
	 * @param random A random number generator to use.
	 * @return The tier of the highest-tier spell that was added to the list.
	 * @author: Electroblob
	 */
	static Tier populateSpells(final EntityLiving wizard, List<Spell> spells, Element e, boolean master, int n, Random random) {

		// This is the tier of the highest tier spell added.
		Tier maxTier = Tier.NOVICE;

		List<Spell> npcSpells = Spell.getSpells(s -> s.canBeCastBy(wizard, false));
		npcSpells.removeIf(s -> !s.applicableForItem(WizardryItems.spell_book));

		for (int i = 0; i < n; i++) {

			boolean battleMage = wizard instanceof IArmourClassWizard && ((IArmourClassWizard) wizard).getArmourClass() == ItemWizardArmour.ArmourClass.BATTLEMAGE;

			Tier tier;
			// If the wizard has no element, it picks a random one each time.
			Element element = e == Element.MAGIC ? Element.values()[random.nextInt(Element.values().length)] : e;

			int randomiser = random.nextInt(20);

			// Uses its own special weighting
			if (randomiser < 10) {
				tier = Tier.NOVICE;
			} else if (randomiser < 16) {
				tier = Tier.APPRENTICE;
			} else if (randomiser < 19 || !master) {
				tier = Tier.ADVANCED;
			} else {
				tier = Tier.MASTER;
			}

			if (tier.ordinal() > maxTier.ordinal()) { maxTier = tier; }

			List<Spell> list;

			if (battleMage) {
				switch (i) {
					case 0: // slot 0 - preferably a self buff spell
						list = SpellCategorization.getSpellsForCategory(SpellCategorization.SpellCategory.BUFF);
						break;
					case 1: // slot 0 - preferably a close combat spell
						list = SpellCategorization.getSpellsForCategory(SpellCategorization.SpellCategory.CLOSE_COMBAT);
						break;
					case 2: // slot 0 - preferably a close combat spell
						list = SpellCategorization.getSpellsForCategory(SpellCategorization.SpellCategory.DISABLE);
						break;
					default: // any spell
						list = Spell.getSpells(new Spell.TierElementFilter(tier, element, SpellProperties.Context.NPCS));
				}
				// Keeps only spells which can be cast by NPCs
				list.retainAll(npcSpells);
				// Removes spells that the wizard already has
				list.removeAll(spells);

				list.removeIf(s -> s.getElement() != element);

				if (list.isEmpty()) {
					list = Spell.getSpells(new Spell.TierElementFilter(tier, element, SpellProperties.Context.NPCS));
				}

			} else {
				// Finds all the spells of the chosen tier and element
				list = Spell.getSpells(new Spell.TierElementFilter(tier, element, SpellProperties.Context.NPCS));
			}

			// Keeps only spells which can be cast by NPCs
			list.retainAll(npcSpells);
			// Removes spells that the wizard already has
			list.removeAll(spells);

			// Ensures the tier chosen actually has spells in it. (isEmpty() is exactly the same as size() == 0)
			if (list.isEmpty()) {
				// If there are no spells applicable, tier and element restrictions are removed to give maximum
				// possibility of there being an applicable spell.
				list = npcSpells;
				// Removes spells that the wizard already has
				list.removeAll(spells);
			}

			// If the list is still empty now, there must be less than 3 enabled spells that can be cast by wizards
			// (excluding magic missile). In this case, having empty slots seems reasonable.
			if (!list.isEmpty()) { spells.add(list.get(random.nextInt(list.size()))); }

		}

		return maxTier;
	}

	ItemWizardArmour.ArmourClass getArmourClass();

	void setArmourClass(ItemWizardArmour.ArmourClass armourClass);

	default ITextComponent getArmourClassNameFor(ItemWizardArmour.ArmourClass armourClass) {
		return new TextComponentTranslation("wizard_armour_class." + armourClass.name().toLowerCase());
	}
}
