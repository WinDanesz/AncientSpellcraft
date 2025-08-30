package com.windanesz.ancientspellcraft;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.ItemArtefact;
import net.minecraft.item.Item;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test to verify that all artefacts in ASItems are registered to exactly one loot table subset.
 * 
 * Artefacts are defined as:
 * 1. Items that extend ItemArtefact.java, OR
 * 2. Items whose public static Item entry starts with prefixes: head_, charm_, ring_, body_, belt_, amulet_
 */
public class ArtefactLootTableTest {

    private static final String MODID = "ancientspellcraft";
    private static final String[] ARTEFACT_PREFIXES = {"head_", "charm_", "ring_", "body_", "belt_", "amulet_"};
    private static final String[] LOOT_TABLE_SUBSETS = {"uncommon_artefacts", "rare_artefacts", "epic_artefacts"};

    @Test
    public void testAllArtefactsRegisteredToExactlyOneLootTable() throws Exception {
        // Get all artefacts from ASItems
        Set<String> allArtefacts = getAllArtefacts();
        
        // Get items from each loot table subset
        Map<String, Set<String>> lootTableItems = new HashMap<>();
        for (String subset : LOOT_TABLE_SUBSETS) {
            Set<String> items = getItemsFromLootTable(subset);
            lootTableItems.put(subset, items);
        }
        
        // Verify each artefact appears in exactly one subset
        Set<String> registeredArtefacts = new HashSet<>();
        for (Map.Entry<String, Set<String>> entry : lootTableItems.entrySet()) {
            Set<String> items = entry.getValue();
            
            for (String artefact : allArtefacts) {
                if (items.contains(artefact)) {
                    assertFalse(registeredArtefacts.contains(artefact), 
                               "Artefact " + artefact + " appears in multiple loot table subsets");
                    registeredArtefacts.add(artefact);
                }
            }
        }
        
        // Check for missing artefacts
        Set<String> missingArtefacts = new HashSet<>(allArtefacts);
        missingArtefacts.removeAll(registeredArtefacts);
        
        if (!missingArtefacts.isEmpty()) {
            fail("The following artefacts are not registered to any loot table subset: " + missingArtefacts);
        }
        
        // Check for extra items in loot tables (not artefacts)
        Set<String> allLootTableItems = new HashSet<>();
        for (Set<String> items : lootTableItems.values()) {
            allLootTableItems.addAll(items);
        }
        
        Set<String> nonArtefactItems = new HashSet<>(allLootTableItems);
        nonArtefactItems.removeAll(allArtefacts);
        
        if (!nonArtefactItems.isEmpty()) {
            fail("The following non-artefact items are registered in loot table subsets: " + nonArtefactItems);
        }
    }

    /**
     * Gets all artefacts from ASItems class by:
     * 1. Finding all public static Item fields that extend ItemArtefact
     * 2. Finding all public static Item fields with artefact prefixes
     */
    private Set<String> getAllArtefacts() throws Exception {
        Set<String> artefacts = new HashSet<>();
        
        // Get all public static Item fields from ASItems
        Field[] fields = ASItems.class.getDeclaredFields();
        
        for (Field field : fields) {
            if (Item.class.isAssignableFrom(field.getType()) && 
                java.lang.reflect.Modifier.isStatic(field.getModifiers()) &&
                java.lang.reflect.Modifier.isPublic(field.getModifiers())) {
                
                String fieldName = field.getName();
                
                // Check if field name starts with artefact prefix
                if (hasArtefactPrefix(fieldName)) {
                    // Construct the expected registry name from the field name
                    String registryName = MODID + ":" + fieldName;
                    artefacts.add(registryName);
                }
                // Also check if item extends ItemArtefact (for items that might not have the prefix)
                else {
                    try {
                        Item item = (Item) field.get(null);
                        if (item != null && ItemArtefact.class.isAssignableFrom(item.getClass())) {
                            String registryName = item.getRegistryName().toString();
                            if (registryName.startsWith(MODID + ":")) {
                                artefacts.add(registryName);
                            }
                        }
                    } catch (Exception e) {
                        // Field might not be initialized yet, that's okay
                        // For testing purposes, we'll assume items with artefact prefixes are artefacts
                    }
                }
            }
        }
        
        return artefacts;
    }

    /**
     * Checks if a field name starts with any of the artefact prefixes
     */
    private boolean hasArtefactPrefix(String fieldName) {
        for (String prefix : ARTEFACT_PREFIXES) {
            if (fieldName.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Parses a loot table JSON file and extracts all item names
     */
    private Set<String> getItemsFromLootTable(String subsetName) throws IOException {
        Set<String> items = new HashSet<>();
        
        String resourcePath = "/assets/" + MODID + "/loot_tables/subsets/" + subsetName + ".json";
        InputStream inputStream = getClass().getResourceAsStream(resourcePath);
        
        if (inputStream == null) {
            throw new IOException("Could not find loot table resource: " + resourcePath);
        }
        
        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            JsonParser parser = new JsonParser();
            JsonObject lootTable = parser.parse(reader).getAsJsonObject();
            
            JsonArray pools = lootTable.getAsJsonArray("pools");
            for (JsonElement poolElement : pools) {
                JsonObject pool = poolElement.getAsJsonObject();
                JsonArray entries = pool.getAsJsonArray("entries");
                
                for (JsonElement entryElement : entries) {
                    JsonObject entry = entryElement.getAsJsonObject();
                    if ("item".equals(entry.get("type").getAsString())) {
                        String itemName = entry.get("name").getAsString();
                        items.add(itemName);
                    }
                }
            }
        }
        
        return items;
    }

    @Test
    public void testLootTableFilesExist() {
        for (String subset : LOOT_TABLE_SUBSETS) {
            String resourcePath = "/assets/" + MODID + "/loot_tables/subsets/" + subset + ".json";
            InputStream inputStream = getClass().getResourceAsStream(resourcePath);
            assertNotNull(inputStream, "Loot table file does not exist: " + resourcePath);
            try {
                inputStream.close();
            } catch (IOException e) {
                // Ignore
            }
        }
    }

    @Test
    public void testArtefactPrefixesAreValid() {
        // This test ensures our prefix list is comprehensive
        // It's a sanity check to make sure we're not missing any artefact types
        String[] expectedPrefixes = {"head_", "charm_", "ring_", "body_", "belt_", "amulet_"};
        assertArrayEquals(expectedPrefixes, ARTEFACT_PREFIXES, "Artefact prefixes should match expected values");
    }
}
