package com.windanesz.ancientspellcraft.mixin.ebwizardry;

import com.windanesz.ancientspellcraft.integration.baubles.ASBaublesIntegration;
import com.windanesz.ancientspellcraft.item.AbstractItemArtefactWithSlots;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.ItemScroll;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.loot.RandomSpell;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellProperties;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Mixin(RandomSpell.class)
public abstract class MixinRandomSpell {

	@Shadow(remap = false)
	@Final
	private List<Tier> tiers;

	@Shadow(remap = false)
	@Final
	private List<Spells> spells;

	@Shadow(remap = false)
	@Final
	private List<Element> elements;

	@Shadow(remap = false)
	@Final
	private boolean ignoreWeighting;

	@Shadow(remap = false)
	@Final
	private float undiscoveredBias;

	/**
	 * @author WinDanesz
	 * @reason Overwrite the pickRandomSpell method for ASItems.amulet_talisman_of_affinity
	 */
	@Overwrite(remap = false)
	private Spell pickRandomSpell(ItemStack stack, Random random, SpellProperties.Context spellContext, EntityPlayer player) {

		// We're now doing this first because we need to know which spells we have to play with before selecting a tier and element
		List<Spell> possibleSpells = Spell.getSpells(s -> s.isEnabled(spellContext) && s.applicableForItem(stack.getItem())
				// Remove excluded tiers/elements immediately (mainly because empty tier checks should account for excluded elements)
				&& (tiers == null || tiers.contains(s.getTier())) && (elements == null || elements.contains(s.getElement())));

		if (spells != null && !spells.isEmpty()) {
			possibleSpells.retainAll(spells); // Normally you wouldn't specify a spells list AND tiers/elements... but you could!
		}

		if (stack.getItem() instanceof ItemScroll)
			possibleSpells.removeIf(s -> !s.isEnabled(SpellProperties.Context.SCROLL));
		if (stack.getItem() instanceof ItemSpellBook)
			possibleSpells.removeIf(s -> !s.isEnabled(SpellProperties.Context.BOOK));

		// Select a tier...

		List<Tier> possibleTiers = new ArrayList<>();

		if (tiers == null || tiers.isEmpty()) {
			possibleTiers.addAll(Arrays.asList(Tier.values()));
		} else {
			possibleTiers.addAll(tiers);
		}
		// Remove all empty tiers
		possibleTiers.removeIf(t -> possibleSpells.stream().noneMatch(s -> s.getTier() == t)); // Lambdaception!

		if (possibleTiers.isEmpty()) return Spells.none; // Gotta disable a lot of spells for this to happen

		Tier tier = ignoreWeighting ? possibleTiers.get(random.nextInt(possibleTiers.size()))
				: Tier.getWeightedRandomTier(random, possibleTiers.toArray(new Tier[0]));

		// Remove all spells that aren't of the selected tier
		possibleSpells.removeIf(s -> s.getTier() != tier);
		if (possibleSpells.isEmpty())
			return Spells.none; // Should be caught by the no-elements check but we might as well

		// Select an element...

		List<Element> possibleElements = new ArrayList<>();

		// Elements aren't weighted
		if (elements == null || elements.isEmpty()) {
			possibleElements.addAll(Arrays.asList(Element.values()));
		} else {
			possibleElements.addAll(elements);
		}
		// Remove all empty elements
		possibleElements.removeIf(e -> possibleSpells.stream().noneMatch(s -> s.getElement() == e));

		if (possibleElements.isEmpty()) return Spells.none; // A bit more likely I guess, but still pretty unlikely

		Element element = possibleElements.get(random.nextInt(possibleElements.size()));

		/////////////// MIXIN CHANGES
		// Check if the player has ASItems.amulet_talisman_of_affinity and filter spells accordingly
		if (player != null && ASBaublesIntegration.enabled()) {
			List<ItemStack> equippedArtefacts = ASBaublesIntegration.getEquippedArtefactStacks(player, ItemArtefact.Type.AMULET);
			equippedArtefacts.stream()
					.filter(s -> s.getItem() == ASItems.amulet_talisman_of_affinity)
					.findFirst()
					.ifPresent(s -> {
						if (s.getItem() instanceof AbstractItemArtefactWithSlots) {
							ItemStack crystalStack = AbstractItemArtefactWithSlots.getItemForSlot(s, 0);
							Element element1 = Element.values()[crystalStack.getMetadata()];
							if (random.nextFloat() < 0.99) {
								possibleElements.clear();
								possibleElements.add(element1);
							}
						}
					});
		}
		/////////////// MIXIN CHANGES

		// Remove all spells that aren't of the selected tier
		possibleSpells.removeIf(s -> s.getElement() != element);
		if (possibleSpells.isEmpty()) return Spells.none; // If it fails anywhere, it'll most likely be here

		if (player != null) {

			float bias = undiscoveredBias;
			// Archivist's eyeglass increases undiscovered bias by 0.4 up to a maximum of 0.9
			if (ItemArtefact.isArtefactActive(player, WizardryItems.charm_spell_discovery))
				bias = Math.min(bias + 0.4f, 0.9f);

			// Remove either the undiscovered spells or the discovered ones, depending on the bias
			if (bias > 0) {

				WizardData data = WizardData.get(player);

				int discoveredCount = (int) possibleSpells.stream().filter(data::hasSpellBeenDiscovered).count();
				// If none have been discovered or they've all been discovered, don't bother!
				if (discoveredCount > 0 && discoveredCount < possibleSpells.size()) {
					// Kinda unintuitive but it's very neat!
					boolean keepDiscovered = random.nextFloat() > 0.5f + 0.5f * bias;
					possibleSpells.removeIf(s -> keepDiscovered != data.hasSpellBeenDiscovered(s));
				}
			}
		}

		return possibleSpells.get(random.nextInt(possibleSpells.size())); // Finally pick a spell
	}
}
