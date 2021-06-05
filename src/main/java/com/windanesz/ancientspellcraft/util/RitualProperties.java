package com.windanesz.ancientspellcraft.util;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.constants.RitualTier;
import com.windanesz.ancientspellcraft.registry.Rituals;
import com.windanesz.ancientspellcraft.ritual.Ritual;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.SpellType;
import electroblob.wizardry.spell.Spell;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Heavily based on Electroblob's {@link electroblob.wizardry.util.SpellProperties} for simplicity. All the glory goes to him <3
 * <p>
 * Object that stores base properties associated with rituals. Each rituals has a single instance of this class which
 * stores its base properties and other data. This class also handles loading of the properties from JSON.
 * <p></p>
 * All the fields in this class are final and are assigned during object creation. This is because the intent is that a
 * new RitualProperties object is created on load and synced with each client on player login. Having final fields
 * therefore guarantees that the properties are always synced whenever necessary, but cannot otherwise be fiddled
 * with programmatically.
 * <p></p>
 * Generally, users need not worry about this class; it is intended that the various property getters in Ritual be used
 * rather than querying this class directly <i>(in fact, you can't do that without reflection anyway)</i>.
 * <p></p>
 *
 * @author Electroblob (@link electroblob.wizardry.util.SpellProperties), WinDanesz
 * @since Ancient Spellcraft 1.4
 */
public final class RitualProperties {

	private static final Gson gson = new Gson();

	/**
	 * Set of enum constants representing contexts in which a ritual can be enabled/disabled.
	 */
	public enum Context {

		/**
		 * Disabling this context will make a ritual's book unobtainable and unusable.
		 */
		BOOK("book"),
		/**
		 * Disabling this context will make a ritual's scroll unobtainable and unusable.
		 */
		SCROLL("scroll"),
		/**
		 * Disabling this context will prevent a ritual from being cast using a wand.
		 */
		WANDS("wands"),
		/**
		 * Disabling this context will prevent NPCs from casting or dropping a ritual.
		 */
		NPCS("npcs"),
		/**
		 * Disabling this context will prevent dispensers from casting a spell.
		 */
		DISPENSERS("dispensers"),
		/**
		 * Disabling this context will prevent a ritual from being cast using commands.
		 */
		COMMANDS("commands"),
		/**
		 * Disabling this context will prevent a ritual's book or scroll generating in chests.
		 */
		TREASURE("treasure"),
		/**
		 * Disabling this context will prevent a ritual's book or scroll from being sold by NPCs.
		 */
		TRADES("trades"),
		/**
		 * Disabling this context will prevent a ritual's book or scroll being dropped by mobs.
		 */
		LOOTING("looting");

		/**
		 * The JSON identifier for this context.
		 */
		public final String name;

		Context(String name) {
			this.name = name;
		}
	}

	/**
	 * A map storing whether each context is enabled for this ritual.
	 */
	private final Map<Context, Boolean> enabledContexts;
	/**
	 * A map storing the base values for this ritual. These values are defined by the ritual class and cannot be
	 * changed.
	 */
	// We're using Number here because it makes implementors think about what they convert it to.
	// If we did what attributes do and just use doubles, people (myself included!) might plug them into calculations
	// without thinking. However, with Number you can't just do that, you have to convert and therefore you have to
	// decide how to do the conversion. Internally they're handled as floats though.
	private final Map<String, Number> baseValues;

	// TODO: review if we need all of these

	/**
	 * The tier this ritual belongs to.
	 */
	public final RitualTier tier;
	/**
	 * The element this ritual belongs to.
	 */
	public final Element element;
	/**
	 * The type of ritual this is classified as.
	 */
	public final SpellType type;
	/**
	 * Mana cost of the ritual. If it is a continuous ritual the cost is per second.
	 */
	public final int cost;
	/**
	 * The charge-up time of the ritual, in ticks.
	 */
	public final int chargeup;
	/**
	 * The cooldown time of the ritual, in ticks.
	 */
	public final int cooldown;
	/**
	 * The lifetime of the ritual's tileentity, in ticks, when all conditions are met.
	 * Use -1 for infinite lifetime.
	 */
	public final int lifetime;

	public final int size;

	public int centerPieceIndex;
	public Item centerpiece;

	public int width = 0;
	public int height = 0;
	/**
	 * The required ingredients of the ritual.
	 */
	//	public ItemStack[] ingredients;

	public NonNullList<Ingredient> pattern;

	public Ingredient centerPiece;

	// Sometimes it just makes more sense to do the JSON parsing in the constructor
	// It's the only way we're gonna keep the fields final!

	/**
	 * Parses the given JSON object and constructs a new {@code SpellProperties} from it, setting all the relevant
	 * fields and references.
	 *
	 * @param json   A JSON object representing the spell properties to be constructed.
	 * @param ritual The ritual that this {@code RitualProperties} object is for.
	 * @throws JsonSyntaxException if at any point the JSON object is found to be invalid.
	 */
	private RitualProperties(JsonObject json, Ritual ritual) {

		String[] baseValueNames = ritual.getPropertyKeys();
		int width = 0;
		int height = 0;
		enabledContexts = new EnumMap<>(Context.class);
		baseValues = new HashMap<>();

		JsonObject enabled = JsonUtils.getJsonObject(json, "enabled");

		// This time we know the exact set of properties so we can iterate over them instead of the json object
		// In fact, we actually want to throw an exception if any of them are missing
		for (Context context : Context.values()) {
			enabledContexts.put(context, JsonUtils.getBoolean(enabled, context.name));
		}

		try {
			tier = RitualTier.fromName(JsonUtils.getString(json, "tier"));
			element = Element.fromName(JsonUtils.getString(json, "element"));
			type = SpellType.fromName(JsonUtils.getString(json, "type"));
		}
		catch (IllegalArgumentException e) {
			throw new JsonSyntaxException("Incorrect spell property value", e);
		}

		cost = JsonUtils.getInt(json, "cost");
		chargeup = JsonUtils.getInt(json, "chargeup");
		cooldown = JsonUtils.getInt(json, "cooldown");
		lifetime = JsonUtils.getInt(json, "lifetime");
		size = JsonUtils.getInt(json, "size");
		//////////////////////

		///////////////////

		//		String group = JsonUtils.getString(json, "group", "");

		Map<Character, Ingredient> ingMap = Maps.newHashMap();
		for (Map.Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "key").entrySet()) {
			if (entry.getKey().length() != 1)
				throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
			if (" ".equals(entry.getKey()))
				throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");

			ingMap.put(entry.getKey().toCharArray()[0], CraftingHelper.getIngredient(entry.getValue(), new JsonContext(AncientSpellcraft.MODID)));
		}
		ingMap.put(' ', Ingredient.EMPTY);

		JsonArray patternJ = JsonUtils.getJsonArray(json, "pattern");
		if (patternJ.size() == 0)
			throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
		//		if (patternJ.size() > 3)
		//			throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");

		String[] pattern = new String[patternJ.size()];
		for (int x = 0; x < pattern.length; ++x) {
			String line = JsonUtils.getString(patternJ.get(x), "pattern[" + x + "]");
			//			if (line.length() != patternJ.size())
			//				throw new JsonSyntaxException("Invalid pattern: width does not equals with height");
			//			if (x > 0 && pattern[0].length() != line.length())
			//				throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
			pattern[x] = line;
			this.width = line.length();
		}
		this.height = patternJ.size();
		this.pattern = NonNullList.withSize(pattern[0].length() * pattern.length, Ingredient.EMPTY);
		Set<Character> keys = Sets.newHashSet(ingMap.keySet());
		keys.remove(' ');

		int x = 0;
		for (String line : pattern) {
			for (char chr : line.toCharArray()) {
				Ingredient ing = ingMap.get(chr);
				if (ing == null)
					throw new JsonSyntaxException("Pattern references symbol '" + chr + "' but it's not defined in the key");
				this.pattern.set(x++, ing);
				keys.remove(chr);
			}
		}

		if (!keys.isEmpty())
			throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + keys);

		//		centerPiece =//CraftingHelper.getIngredient(JsonUtils.getJsonObject(json, "centerpiece"), new JsonContext(AncientSpellcraft.MODID));
		centerPieceIndex = (int) Math.ceil(this.pattern.size() / 2);
		Ingredient test = this.pattern.get(centerPieceIndex);
		centerpiece = this.pattern.get(centerPieceIndex).getMatchingStacks().length > 0 ? this.pattern.get(centerPieceIndex).getMatchingStacks()[0].getItem() : null;
		//	centerPieceIndex = this.pattern.indexOf(centerPiece);
		//		System.out.println("centerpiece index: " + centerPieceIndex);
		//				return new ShapedRecipes(group, pattern[0].length(), pattern.length, input, result);

		//////////////////

		// There's not much point specifying the classes of the numbers here because the json getter methods just
		// perform conversion to the requested type anyway. It therefore makes very little difference whether the
		// conversion is done during JSON parsing or when we actually use the value - and at least in the latter case,
		// individual subclasses have control over how it is converted.

		// My case in point: summoning 2.5 spiders is obviously nonsense, but what happens when we cast that with a
		// modifier of 2? Should we round the base value down to 2 and then apply the x2 modifier to get 4 spiders?
		// Should we round it up instead? Or should we apply the modifier first and then do the rounding, so with no
		// modifier we still get 2 spiders but with the x2 modifier we get 5?
		// The most pragmatic solution is to let the spell class decide for itself.
		// (Of course, we can only hope that the users aren't jerks and don't try to summon 2 and a half spiders...)

		JsonObject baseValueObject = JsonUtils.getJsonObject(json, "base_properties");

		// If the code requests more values than the JSON file contains, that will cause a JsonSyntaxException here anyway.
		// If there are redundant values in the JSON file, chances are that a user has misunderstood the system and tried
		// to add properties that aren't implemented. However, redundant values will also be found if a programmer has
		// forgotten to call addProperties in their spell constructor (I know I have!), potentially causing a crash at
		// some random point in the future. Since redundant values aren't a problem by themselves, we shouldn't throw an
		// exception, but a warning is appropriate.

		int redundantKeys = baseValueObject.size() - baseValueNames.length;
		if (redundantKeys > 0)
			AncientSpellcraft.logger.warn("Ritual " + ritual.getRegistryName() + " has " + redundantKeys +
					" redundant ritual property key(s) defined in its JSON file. Extra values will have no effect! (Modders:" +
					" make sure you have called addProperties(...) during spell construction)");

		if (baseValueNames.length > 0) {

			for (String baseValueName : baseValueNames) {
				baseValues.put(baseValueName, JsonUtils.getFloat(baseValueObject, baseValueName));
			}
		}

	}

	/**
	 * Constructs a new SpellProperties object for the given spell, reading its values from the given ByteBuf.
	 */
	public RitualProperties(Spell spell, ByteBuf buf) {

		enabledContexts = new EnumMap<>(Context.class);
		baseValues = new HashMap<>();

		for (Context context : Context.values()) {
			// Enum maps have a guaranteed iteration order so this works fine
			enabledContexts.put(context, buf.readBoolean());
		}

		tier = RitualTier.values()[buf.readShort()];
		element = Element.values()[buf.readShort()];
		type = SpellType.values()[buf.readShort()];

		cost = buf.readInt();
		chargeup = buf.readInt();
		cooldown = buf.readInt();
		lifetime = buf.readInt();
		size = buf.readInt();

		List<String> keys = Arrays.asList(spell.getPropertyKeys());
		Collections.sort(keys); // Should be the same list of keys in the same order they were written to the ByteBuf

		for (String key : keys) {
			baseValues.put(key, buf.readFloat());
		}
	}

	/**
	 * Writes this SpellProperties object to the given ByteBuf so it can be sent via packets.
	 */
	public void write(ByteBuf buf) {

		for (Context context : Context.values()) {
			// Enum maps have a guaranteed iteration order so this works fine
			buf.writeBoolean(enabledContexts.get(context));
		}

		buf.writeShort(tier.ordinal());
		buf.writeShort(element.ordinal());
		buf.writeShort(type.ordinal());

		buf.writeInt(cost);
		buf.writeInt(chargeup);
		buf.writeInt(cooldown);
		buf.writeInt(lifetime);
		buf.writeInt(size);

		List<String> keys = new ArrayList<>(baseValues.keySet());
		Collections.sort(keys); // Sort alphabetically (as long as the order is consistent it doesn't matter)

		for (String key : keys) {
			buf.writeFloat(baseValues.get(key).floatValue());
		}
	}

	/**
	 * Returns whether the spell is enabled in any of the given contexts.
	 *
	 * @param contexts The context in which to check if the spell is enabled.
	 * @return True if the spell is enabled in any of the given contexts, false if not.
	 */
	public boolean isEnabled(Context... contexts) {
		return enabledContexts.entrySet().stream().anyMatch(e -> e.getValue() && Arrays.asList(contexts).contains(e.getKey()));
	}

	/**
	 * Returns whether a base value was defined with the given identifier.
	 *
	 * @param identifier The string identifier to check for.
	 * @return True if a base value was defined with the given identifier, false otherwise.
	 */
	public boolean hasBaseValue(String identifier) {
		return baseValues.containsKey(identifier);
	}

	/**
	 * Returns the base value for this spell that corresponds to the given identifier. To check whether an identifier
	 * exists, use {@link RitualProperties#hasBaseValue(String)}.
	 *
	 * @param identifier The string identifier to fetch the base value for.
	 * @return The base value, as a {@code Number}.
	 * @throws IllegalArgumentException if no base value was defined with the given identifier.
	 */
	// Better to throw an exception than make this nullable because the vast majority of uses are for retrieving
	// specific spells' properties that are known to exist, and IntelliJ would scream at us for not checking
	public Number getBaseValue(String identifier) {
		if (!baseValues.containsKey(identifier)) {
			throw new IllegalArgumentException("Base value with identifier '" + identifier + "' is not defined.");
		}
		return baseValues.get(identifier);
	}

	/**
	 * Called from preInit() in the main mod class to initialise the spell property system.
	 */
	// For some reason I had this called from a method in CommonProxy which was overridden to do nothing in
	// ClientProxy, but that method was never called and instead this one was called directly from the main mod class.
	// I *think* I decided against the proxy thing and just forgot to delete the methods (they're gone now), but if
	// things don't work as expected then that may be why - pretty sure it's fine though since the properties get
	// wiped client-side on each login anyway.
	public static void init() {

		// Collecting to a set should give us one of each mod ID
		Set<String> modIDs = Ritual.getAllRituals().stream().map(s -> s.getRegistryName().getNamespace()).collect(Collectors.toSet());

		boolean flag = loadConfigRitualProperties();

		for (String modID : modIDs) {
			flag &= loadBuiltInRitualProperties(modID); // Don't short-circuit, or mods later on won't get loaded!
		}

		if (!flag)
			AncientSpellcraft.logger.warn("Some ritual property files did not load correctly; this will likely cause problems later!");
	}

	// There are three 'layers' of ritual properties - in order of priority, these are:
	// 1. World-specific properties, stored in saves/[world]/data/rituals
	// 2. Global overrides, stored in config/ebwizardry/rituals
	// 3. Built-in properties, stored in mods/[mod jar]/assets/rituals
	// There's a method for loading each of these below

	public static void loadWorldSpecificSpellProperties(World world) {

		AncientSpellcraft.logger.info("Loading custom ritual properties for world {}", world.getWorldInfo().getWorldName());

		File spellJSONDir = new File(new File(world.getSaveHandler().getWorldDirectory(), "data"), "rituals");

		if (spellJSONDir.mkdirs())
			return; // If it just got created it can't possibly have anything inside

		if (!loadRitualPropertiesFromDir(spellJSONDir))
			AncientSpellcraft.logger.warn("Some ritual property files did not load correctly; this will likely cause problems later!");
	}

	private static boolean loadConfigRitualProperties() {

		AncientSpellcraft.logger.info("Loading ritual properties from config folder");

		File spellJSONDir = new File(Wizardry.configDirectory, "rituals");

		if (!spellJSONDir.exists())
			return true; // If there's no global ritual properties folder, do nothing

		return loadRitualPropertiesFromDir(spellJSONDir);
	}

	// For crafting recipes, Forge does some stuff behind the scenes to load recipe JSON files from mods' namespaces.
	// This leverages the same methods.

	private static boolean loadBuiltInRitualProperties(String modID) {

		// Yes, I know you're not supposed to do orElse(null). But... meh.
		ModContainer mod = Loader.instance().getModList().stream().filter(m -> m.getModId().equals(modID)).findFirst().orElse(null);

		if (mod == null) {
			AncientSpellcraft.logger.warn("Tried to load built-in ritual properties for mod with ID '" + modID + "', but no such mod was loaded");
			return false; // Failed!
		}

		// Rituals will be removed from this list as their properties are set
		// If everything works properly, it should be empty by the end
		List<Ritual> rituals = Ritual.getRituals(s -> s.getRegistryName().getNamespace().equals(modID));
		if (modID.equals(Wizardry.MODID))
			rituals.add(Rituals.none); // In this particular case we do need the none ritual

		AncientSpellcraft.logger.info("Loading built-in ritual properties for " + rituals.size() + " rituals in mod " + modID);

		// This method is used by Forge to load mod recipes and advancements, so it's a fair bet it's the right one
		// In the absence of Javadoc, here's what the non-obvious parameters do:
		// - preprocessor is called once with just the root directory, allowing any global index files to be processed
		// - processor is called once for each file in the directory so processing can be done
		// - defaultUnfoundRoot is the default value to return if the root specified isn't found
		// - visitAllFiles determines whether the method short-circuits; in other words, if the processor returns false
		// at any point and visitAllFiles is false, the method returns immediately.
		boolean success = CraftingHelper.findFiles(mod, "assets/" + modID + "/rituals", null,

				(root, file) -> {

					String relative = root.relativize(file).toString();
					if (!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.startsWith("_"))
						return true; // True or it'll look like it failed just because it found a non-JSON file

					String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/");
					ResourceLocation key = new ResourceLocation(modID, name);

					Ritual ritual = Ritual.registry.getValue(key);

					// If no ritual matches a particular file, log it and just ignore the file
					if (ritual == null) {
						AncientSpellcraft.logger.info("Ritual properties file " + name + ".json does not match any registered rituals; ensure the filename is spelled correctly.");
						return true;
					}

					// We want to do this regardless of whether the JSON file got read properly, because that prints its
					// own separate warning
					if (!rituals.remove(ritual))
						AncientSpellcraft.logger.warn("What's going on?!");

					// Ignore rituals overridden in the config folder
					// This needs to be done AFTER the above line or it'll think there are missing ritual properties files
					if (ritual.arePropertiesInitialised())
						return true;

					BufferedReader reader = null;

					try {

						reader = Files.newBufferedReader(file);

						JsonObject json = JsonUtils.fromJson(gson, reader, JsonObject.class);
						RitualProperties properties = new RitualProperties(json, ritual);
						ritual.setProperties(properties);

					}
					catch (JsonParseException jsonparseexception) {
						AncientSpellcraft.logger.error("Parsing error loading ritual property file for " + key, jsonparseexception);
						return false;
					}
					catch (IOException ioexception) {
						AncientSpellcraft.logger.error("Couldn't read ritual property file for " + key, ioexception);
						return false;
					}
					finally {
						IOUtils.closeQuietly(reader);
					}

					return true;

				},
				true, true);

		// If a ritual is missing its file, log an error
		if (!rituals.isEmpty()) {
			if (rituals.size() <= 15) {
				rituals.forEach(s -> AncientSpellcraft.logger.error("Ritual " + s.getRegistryName() + " is missing a properties file!"));
			} else {
				// If there are more than 15 don't bother logging them all, chances are they're all missing
				AncientSpellcraft.logger.error("Mod " + modID + " has " + rituals.size() + " rituals that are missing properties files!");
			}
		}

		return success;
	}

	private static boolean loadRitualPropertiesFromDir(File dir) {

		boolean success = true;

		for (File file : FileUtils.listFiles(dir, new String[] {"json"}, true)) {

			// The structure in world and config folders is subtly different in that the "rituals" and mod id directories
			// are in the opposite order, i.e. it's rituals/modid/whatever.json instead of modid/rituals/whatever.json
			String relative = dir.toPath().relativize(file.toPath()).toString(); // modid\whatever.json
			String nameAndModID = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/"); // modid/whatever
			String modID = nameAndModID.split("/")[0]; // modid
			String name = nameAndModID.substring(nameAndModID.indexOf('/') + 1); // whatever

			ResourceLocation key = new ResourceLocation(modID, name);

			Ritual ritual = Ritual.registry.getValue(key);

			// If no ritual matches a particular file, log it and just ignore the file
			if (ritual == null) {
				AncientSpellcraft.logger.info("Ritual properties file " + nameAndModID + ".json does not match any registered rituals; ensure the filename is spelled correctly.");
				continue;
			}

			BufferedReader reader = null;

			try {

				reader = Files.newBufferedReader(file.toPath());

				JsonObject json = JsonUtils.fromJson(gson, reader, JsonObject.class);
				RitualProperties properties = new RitualProperties(json, ritual);
				ritual.setProperties(properties);

			}
			catch (JsonParseException jsonparseexception) {
				AncientSpellcraft.logger.error("Parsing error loading ritual property file for " + key, jsonparseexception);
				success = false;
			}
			catch (IOException ioexception) {
				AncientSpellcraft.logger.error("Couldn't read ritual property file for " + key, ioexception);
				success = false;
			}
			finally {
				IOUtils.closeQuietly(reader);
			}
		}

		return success;
	}

	protected static String[] shrink(String... strings) {
		int i = Integer.MAX_VALUE;
		int j = 0;
		int k = 0;
		int l = 0;

		for (int i1 = 0; i1 < strings.length; ++i1) {
			String s = strings[i1];
			i = Math.min(i, firstNonSpace(s));
			int j1 = lastNonSpace(s);
			j = Math.max(j, j1);

			if (j1 < 0) {
				if (k == i1) {
					++k;
				}

				++l;
			} else {
				l = 0;
			}
		}

		if (strings.length == l) {
			return new String[0];
		} else {
			String[] astring = new String[strings.length - l - k];

			for (int k1 = 0; k1 < astring.length; ++k1) {
				astring[k1] = strings[k1 + k].substring(i, j + 1);
			}

			return astring;
		}
	}

	private static int firstNonSpace(String str) {
		int i;

		for (i = 0; i < str.length() && str.charAt(i) == ' '; ++i) {
			;
		}

		return i;
	}

	private static int lastNonSpace(String str) {
		int i;

		for (i = str.length() - 1; i >= 0 && str.charAt(i) == ' '; --i) {
			;
		}

		return i;
	}

}

// We probably could have used the attribute system for all of this, but I am reluctant to do so for a number of
// reasons:
// - It's a mess.
// - Unlike entities and itemstacks, rituals don't have a separate instance for each time they are cast, which might
// prove problematic.
// - I'm loading my base properties once and not touching them again, so they're more like block materials than anything
// else.
// - Some of the properties aren't numerical, and some of them can't have modifiers applied. In fact, most of them can't!
// So even if we were to use attributes, we'd still need this class.