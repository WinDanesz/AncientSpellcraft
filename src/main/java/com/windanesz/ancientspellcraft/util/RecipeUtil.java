package com.windanesz.ancientspellcraft.util;

/**
 * Licenses for assets can be found in ASSETS_LICENSE.txt. All other files are licensed under the terms below:
 *
 * The MIT License (MIT)
 *
 * Test Mod 3 - Copyright (c) 2015-2022 Choonster
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.Map;
import java.util.Set;

/**
 * Utility methods for {@link IRecipe}s.
 *
 * @author Choonster
 */
public class RecipeUtil {

	/**
	 * Parse the input of a shaped recipe.
	 * <p>
	 * Adapted from {@link ShapedOreRecipe#factory}.
	 *
	 * @param context The parsing context
	 * @param json    The recipe's JSON object
	 * @return A ShapedPrimer containing the input specified in the JSON object
	 */
	public static CraftingHelper.ShapedPrimer parseShaped(final JsonContext context, final JsonObject json) {
		final Map<Character, Ingredient> ingredientMap = Maps.newHashMap();
		for (final Map.Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "key").entrySet()) {
			if (entry.getKey().length() != 1) {
				throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
			}
			if (" ".equals(entry.getKey())) { throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol."); }

			ingredientMap.put(entry.getKey().toCharArray()[0], CraftingHelper.getIngredient(entry.getValue(), context));
		}

		ingredientMap.put(' ', Ingredient.EMPTY);

		final JsonArray patternJ = JsonUtils.getJsonArray(json, "pattern");

		if (patternJ.size() == 0) { throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed"); }

		final String[] pattern = new String[patternJ.size()];
		for (int x = 0; x < pattern.length; ++x) {
			final String line = JsonUtils.getString(patternJ.get(x), "pattern[" + x + "]");
			if (x > 0 && pattern[0].length() != line.length()) { throw new JsonSyntaxException("Invalid pattern: each row must  be the same width"); }
			pattern[x] = line;
		}

		final CraftingHelper.ShapedPrimer primer = new CraftingHelper.ShapedPrimer();
		primer.width = pattern[0].length();
		primer.height = pattern.length;
		primer.mirrored = JsonUtils.getBoolean(json, "mirrored", true);
		primer.input = NonNullList.withSize(primer.width * primer.height, Ingredient.EMPTY);

		final Set<Character> keys = Sets.newHashSet(ingredientMap.keySet());
		keys.remove(' ');

		int index = 0;
		for (final String line : pattern) {
			for (final char chr : line.toCharArray()) {
				final Ingredient ing = ingredientMap.get(chr);
				if (ing == null) { throw new JsonSyntaxException("Pattern references symbol '" + chr + "' but it's not defined in the key"); }
				primer.input.set(index++, ing);
				keys.remove(chr);
			}
		}

		if (!keys.isEmpty()) { throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + keys); }

		return primer;
	}

	/**
	 * Parse the input of a shapeless recipe.
	 * <p>
	 * Adapted from {@link ShapelessOreRecipe#factory}.
	 *
	 * @param context The parsing context
	 * @param json    The recipe's JSON object
	 * @return A NonNullList containing the ingredients specified in the JSON object
	 */
	public static NonNullList<Ingredient> parseShapeless(final JsonContext context, final JsonObject json) {
		final NonNullList<Ingredient> ingredients = NonNullList.create();
		for (final JsonElement element : JsonUtils.getJsonArray(json, "ingredients")) { ingredients.add(CraftingHelper.getIngredient(element, context)); }

		if (ingredients.isEmpty()) { throw new JsonParseException("No ingredients for shapeless recipe"); }

		return ingredients;
	}
}