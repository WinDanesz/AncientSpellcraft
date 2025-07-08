package com.windanesz.ancientspellcraft.loot;

import com.google.gson.*;
import com.windanesz.ancientspellcraft.item.ItemAncientWand;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.ItemScroll;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellProperties;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RandomBoundAncientSpell extends LootFunction {

	private final List<Spell> spells;
	private final boolean ignoreWeighting;
	private final float undiscoveredBias;
	private final List<Tier> tiers;

	protected RandomBoundAncientSpell(LootCondition[] conditions, List<Spell> spells, boolean ignoreWeighting, float undiscoveredBias, List<Tier> tiers) {
		super(conditions);
		this.spells = spells;
		this.ignoreWeighting = ignoreWeighting;
		this.undiscoveredBias = undiscoveredBias;
		this.tiers = tiers;
	}

	@Override
	public ItemStack apply(ItemStack stack, Random random, LootContext context) {

		if (!(stack.getItem() instanceof ItemSpellBook) && !(stack.getItem() instanceof ItemScroll))
			Wizardry.logger.warn("Applying the random_bound_ancient_spell loot function to an item that isn't a spell book or scroll.");

		SpellProperties.Context spellContext = context.getLootedEntity() == null ? SpellProperties.Context.TREASURE : SpellProperties.Context.LOOTING;

		// This method is badly-named, loot chests pass a player through too, not just mobs
		// (And WHY does it only return an entity?! The underlying field is always a player, so I'm casting it anyway)
		EntityPlayer player = (EntityPlayer) context.getKillerPlayer();

		Spell spell = pickRandomSpell(stack, random, spellContext, player);

		if (spell == Spells.none)
			Wizardry.logger.warn("Tried to apply the random_bound_ancient_spell loot function to an item, but no" + " enabled spells matched the criteria specified.");

		if (stack.getItem() instanceof ItemAncientWand && random.nextBoolean()) {
			WandHelper.setSpells(stack, new Spell[spell.metadata()]);
		}

		return stack;
	}

	private Spell pickRandomSpell(ItemStack stack, Random random, SpellProperties.Context spellContext, EntityPlayer player) {

		// We're now doing this first because we need to know which spells we have to play with before selecting a tier and element
		List<Spell> possibleSpells = Spell.getSpells(s -> s.isEnabled(spellContext) && s.applicableForItem(stack.getItem())
				// Remove excluded tiers
				&& (tiers == null || tiers.contains(s.getTier())) && s.getElement() == Element.MAGIC && s.applicableForItem(ASItems.ancient_spell_book));

		if (spells != null && !spells.isEmpty()) {
			possibleSpells.retainAll(spells); // Normally you wouldn't specify a spells list AND tiers/elements... but you could!
		}

		possibleSpells.removeIf(s -> !s.isEnabled(SpellProperties.Context.WANDS));
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

		Tier tier = ignoreWeighting ? possibleTiers.get(random.nextInt(possibleTiers.size())) : Tier.getWeightedRandomTier(random, possibleTiers.toArray(new Tier[0]));

		// Remove all spells that aren't of the selected tier
		possibleSpells.removeIf(s -> s.getTier() != tier);

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

	public static class Serializer extends LootFunction.Serializer<RandomBoundAncientSpell> {

		public Serializer() {
			super(new ResourceLocation(Wizardry.MODID, "random_bound_ancient_spell"), RandomBoundAncientSpell.class);
		}

		public void serialize(JsonObject object, RandomBoundAncientSpell function, JsonSerializationContext serializationContext) {

			if (function.spells != null && !function.spells.isEmpty()) {

				JsonArray jsonarray = new JsonArray();

				for (Spell spell : function.spells) {
					jsonarray.add(new JsonPrimitive(spell.getRegistryName().toString()));
				}

				object.add("spells", jsonarray);
			}

			object.addProperty("ignore_weighting", function.ignoreWeighting);

			object.addProperty("undiscovered_bias", function.undiscoveredBias);

			if (function.tiers != null && !function.tiers.isEmpty()) {

				JsonArray jsonarray = new JsonArray();

				for (Tier tier : function.tiers) {
					jsonarray.add(new JsonPrimitive(tier.getUnlocalisedName()));
				}

				object.add("tiers", jsonarray);
			}
		}

		public RandomBoundAncientSpell deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootCondition[] conditions) {

			List<Spell> spells = null;
			List<Tier> tiers = null;
			List<Element> elements = null;

			if (object.has("spells")) {

				spells = new ArrayList<>();

				// Importantly, it is necessary to specify a default (the new JsonArray) here because otherwise the
				// parameter will be mandatory, and the game will crash if it isn't present.
				for (JsonElement element : JsonUtils.getJsonArray(object, "spells", new JsonArray())) {

					String string = JsonUtils.getString(element, "spell");

					Spell spell = Spell.get(string);

					if (spell == null) {
						throw new JsonSyntaxException("Unknown spell \'" + string + "\'");
					}

					spells.add(spell);
				}
			}

			boolean ignoreWeighting = JsonUtils.getBoolean(object, "ignore_weighting", false);

			float undiscoveredBias = JsonUtils.getFloat(object, "undiscovered_bias", 0);

			if (object.has("tiers")) {

				tiers = new ArrayList<>();

				for (JsonElement element : JsonUtils.getJsonArray(object, "tiers", new JsonArray())) {

					String string = JsonUtils.getString(element, "tier");

					try {
						tiers.add(Tier.fromName(string));
					} catch (IllegalArgumentException e) {
						// If the string does not match any of the tiers, throws an exception.
						throw new JsonSyntaxException("Unknown tier \'" + string + "\'");
					}
				}
			}

			return new RandomBoundAncientSpell(conditions, spells, ignoreWeighting, undiscoveredBias, tiers);
		}
	}

}
