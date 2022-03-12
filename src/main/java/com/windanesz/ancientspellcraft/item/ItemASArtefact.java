package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftTabs;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.event.ImbuementActivateEvent;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellProperties;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber
public class ItemASArtefact extends ItemArtefact {

	public ItemASArtefact(EnumRarity rarity, Type type) {
		super(rarity, type);
		setCreativeTab(AncientSpellcraftTabs.ANCIENTSPELLCRAFT_GEAR);
	}

	@SubscribeEvent
	public static void onImbuement(ImbuementActivateEvent event) {
		if (event.lastUser != null && ItemArtefact.isArtefactActive(event.lastUser, AncientSpellcraftItems.amulet_imbued_marble)) {
			List<Element> elements = Arrays.asList(event.receptacleElements);

			Element firstElement = elements.get(0);
			if (elements.stream().anyMatch(e -> e != firstElement)) {
				// at least one dust was not matching..
				return;
			}
			// otherwise, all of them were the same, 80% chance to imbue this to the element..
			if (itemRand.nextFloat() < 0.8) {
				for (int i = 0; i < 10; i++) {
					List<Spell> randomSpell = Spell.getSpells(new Spell.TierElementFilter(Tier.ADVANCED, firstElement, SpellProperties.Context.BOOK));
					Spell spell = randomSpell.get(itemRand.nextInt(randomSpell.size() - 1));
					if (spell.applicableForItem(WizardryItems.spell_book)) {
						event.result = new ItemStack(WizardryItems.spell_book, 1, spell.metadata());
						break;
					} else if (spell.applicableForItem(AncientSpellcraftItems.ancient_spellcraft_spell_book)) {
						event.result = new ItemStack(AncientSpellcraftItems.ancient_spellcraft_spell_book, 1, spell.metadata());
						break;
					}
				}
			}
		}
	}
}
