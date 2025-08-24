package com.windanesz.ancientspellcraft.loot;

import com.google.gson.*;
import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.registry.Spells;
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
	private final float chance;
	private final List<Tier> tiers;

	protected RandomBoundAncientSpell(LootCondition[] conditions, List<Spell> spells, boolean ignoreWeighting, float chance, List<Tier> tiers) {
		super(conditions);
		this.spells = spells;
		this.chance = chance;
		this.tiers = tiers;
	}

	@Override
	public ItemStack apply(ItemStack stack, Random random, LootContext context) {

		if (random.nextFloat() >= chance) {
			return stack;
		}
		//if (!(stack.getItem() instanceof ItemSpellBook) && !(stack.getItem() instanceof ItemScroll))
		//	Wizardry.logger.warn("Applying the random_bound_ancient_spell loot function to an item that isn't a spell book or scroll.");

		SpellProperties.Context spellContext = context.getLootedEntity() == null ? SpellProperties.Context.TREASURE : SpellProperties.Context.LOOTING;

		// This method is badly-named, loot chests pass a player through too, not just mobs
		// (And WHY does it only return an entity?! The underlying field is always a player, so I'm casting it anyway)
		EntityPlayer player = (EntityPlayer) context.getKillerPlayer();

		Spell spell = pickRandomSpell(stack, random, spellContext, player);

		if (spell == Spells.none)
			Wizardry.logger.warn("Tried to apply the random_bound_ancient_spell loot function to an item, but no" + " enabled spells matched the criteria specified.");

		WandHelper.setSpells(stack, new Spell[]{spell});

		return stack;
	}

	private Spell pickRandomSpell(ItemStack stack, Random random, SpellProperties.Context spellContext, EntityPlayer player) {

		// We're now doing this first because we need to know which spells we have to play with before selecting a tier and element
		List<Spell> possibleSpells = Spell.getSpells(s -> s.applicableForItem(ASItems.ancient_spell_book)
				&& (tiers == null || tiers.contains(s.getTier())));

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

		Tier tier = possibleTiers.get(random.nextInt(possibleTiers.size()));

		// Remove all spells that aren't of the selected tier
		possibleSpells.removeIf(s -> s.getTier() != tier);

		possibleSpells.removeIf(s -> s.getElement() != Element.MAGIC);
		if (possibleSpells.isEmpty()) return Spells.none; // If it fails anywhere, it'll most likely be here

		return possibleSpells.get(random.nextInt(possibleSpells.size())); // Finally pick a spell
	}

	public static class Serializer extends LootFunction.Serializer<RandomBoundAncientSpell> {

		public Serializer() {
			super(new ResourceLocation(AncientSpellcraft.MODID, "random_bound_ancient_spell"), RandomBoundAncientSpell.class);
		}

		public void serialize(JsonObject object, RandomBoundAncientSpell function, JsonSerializationContext serializationContext) {

			if (function.spells != null && !function.spells.isEmpty()) {

				JsonArray jsonarray = new JsonArray();

				for (Spell spell : function.spells) {
					jsonarray.add(new JsonPrimitive(spell.getRegistryName().toString()));
				}

				object.add("spells", jsonarray);
			}

			object.addProperty("chance", function.chance);

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

			float chance = JsonUtils.getFloat(object, "chance", 0);

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

			return new RandomBoundAncientSpell(conditions, spells, ignoreWeighting, chance, tiers);
		}
	}

}
