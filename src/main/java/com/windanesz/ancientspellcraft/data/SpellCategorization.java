package com.windanesz.ancientspellcraft.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.windanesz.ancientspellcraft.AncientSpellcraft;
import electroblob.wizardry.spell.Spell;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SpellCategorization {

	private SpellCategorization(){};

	private static final Gson gson = new Gson();

	private static HashMap<Spell, SpellCategory> spellCategoryHashMap = new HashMap<>();

	public static SpellCategory getCategoryFor(Spell spell) {
		return spellCategoryHashMap.getOrDefault(spell, SpellCategory.UNCATEGORISED);
	}

	public static void init() {
		loadSpellData();
	}

	public enum SpellCategory {
		UNCATEGORISED,
		DISENGAGE,
		RANGED_ATTACK,
		CLOSE_COMBAT,
		CURSE,
		MINION,
		BUFF,
		LIFE_SAVING;
	}

	private static boolean loadSpellData() {

		ModContainer mod = Loader.instance().getModList().stream().filter(m -> m.getModId().equals(AncientSpellcraft.MODID)).findFirst().orElse(null);


		// This method is used by Forge to load mod recipes and advancements, so it's a fair bet it's the right one
		// In the absence of Javadoc, here's what the non-obvious parameters do:
		// - preprocessor is called once with just the root directory, allowing any global index files to be processed
		// - processor is called once for each file in the directory so processing can be done
		// - defaultUnfoundRoot is the default value to return if the root specified isn't found
		// - visitAllFiles determines whether the method short-circuits; in other words, if the processor returns false
		// at any point and visitAllFiles is false, the method returns immediately.
		boolean success = CraftingHelper.findFiles(mod, "assets/" + AncientSpellcraft.MODID + "/spelldata", null,

				(root, file) -> {

					String relative = root.relativize(file).toString();
					if (!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.startsWith("_"))
						return true; // True or it'll look like it failed just because it found a non-JSON file

					String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/");

					BufferedReader reader = null;

					try {

						reader = Files.newBufferedReader(file);

						JsonObject json = JsonUtils.fromJson(gson, reader, JsonObject.class);

						JsonObject enabled = JsonUtils.getJsonObject(json, "spells");

						for (Map.Entry<String, JsonElement> entry : enabled.entrySet()) {

							Spell spell = Spell.get(entry.getKey());
							if (spell != null) {
								String category = entry.getValue().getAsString();
								if (Arrays.stream(SpellCategory.values()).anyMatch(v -> v.toString().toLowerCase().equals(category.toLowerCase()))) {
									spellCategoryHashMap.put(spell, SpellCategory.valueOf(category.toUpperCase()));
								}
							}
						}
					}

					catch (JsonParseException jsonparseexception) {
						AncientSpellcraft.logger.error("Parsing error loading spell data property file " + file, jsonparseexception);
						return false;
					}
					catch (IOException ioexception) {
						AncientSpellcraft.logger.error("Couldn't read spell data property file " + file, ioexception);
						return false;
					}
					finally {
						IOUtils.closeQuietly(reader);
					}

					return true;

				},
				true, true);
		return success;
	}

}