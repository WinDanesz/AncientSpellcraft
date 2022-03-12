package com.windanesz.ancientspellcraft.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.windanesz.ancientspellcraft.AncientSpellcraft;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.item.ItemArtefact;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Random;

public class ArtefactCondition implements LootCondition {
	private final String requiredArtefact;
	private final float chance;

	public ArtefactCondition(String requiredArtefact, float chance) {
		this.requiredArtefact = requiredArtefact;
		this.chance = chance;
	}

	public boolean testCondition(Random rand, LootContext context) {

		// This method is badly-named, loot chests pass a player through too, not just mobs
		// (And WHY does it only return an entity?! The underlying field is always a player so I'm casting it anyway)
		EntityPlayer player = (EntityPlayer) context.getKillerPlayer();

		if (rand.nextFloat() <= chance) {
			Item artefact = ForgeRegistries.ITEMS.getValue(new ResourceLocation(requiredArtefact));
			if (artefact == null) {
				Wizardry.logger.warn("Couldn't locate required artefact item in loottable condition!");
			} else if (player != null) {
				return ItemArtefact.isArtefactActive(player, artefact);
			}
		}
		return false;
	}

	public static class Serializer extends LootCondition.Serializer<ArtefactCondition> {

		public Serializer() {
			super(new ResourceLocation(AncientSpellcraft.MODID, "artefact_condition"), ArtefactCondition.class);
		}

		public void serialize(JsonObject json, ArtefactCondition value, JsonSerializationContext context) {
			json.addProperty("required_artefact", value.requiredArtefact);
			json.addProperty("chance", value.chance);
		}

		public ArtefactCondition deserialize(JsonObject object, JsonDeserializationContext context) {

			String requiredArtefact = JsonUtils.getString(object, "required_artefact");
			float chance = JsonUtils.getFloat(object, "chance", 1.0f);

			return new ArtefactCondition(requiredArtefact, chance);
		}
	}
}
