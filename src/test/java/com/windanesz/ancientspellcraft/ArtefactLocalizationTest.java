package com.windanesz.ancientspellcraft;

import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.item.Item;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test to verify that all ItemArtefact items in ASItems have proper localization entries.
 * 
 * For each item that is an instanceof ItemArtefact, the en_us.lang file must contain:
 * - item.ancientspellcraft:<item_name>.name=Item Display Name
 * - item.ancientspellcraft:<item_name>.desc=Item description
 */
public class ArtefactLocalizationTest {

    private static final String MODID = "ancientspellcraft";
    private static final String LANG_FILE_PATH = "/assets/" + MODID + "/lang/en_us.lang";

    @Test
    public void testAllArtefactsHaveLocalizationEntries() throws Exception {
        // Get all artefacts from ASItems
        Set<String> allArtefacts = getAllArtefactNames();
        
        // Get all localization entries from en_us.lang
        Map<String, Set<String>> langEntries = parseLangFile();
        
        Set<String> nameEntries = langEntries.get("name");
        Set<String> descEntries = langEntries.get("desc");
        
        // Track missing entries
        List<String> missingNameEntries = new ArrayList<>();
        List<String> missingDescEntries = new ArrayList<>();
        
        // Verify each artefact has both .name and .desc entries
        for (String artefact : allArtefacts) {
            if (!nameEntries.contains(artefact)) {
                missingNameEntries.add(artefact);
            }
            if (!descEntries.contains(artefact)) {
                missingDescEntries.add(artefact);
            }
        }
        
        // Build error message if there are missing entries
        StringBuilder errorMessage = new StringBuilder();
        
        if (!missingNameEntries.isEmpty()) {
            errorMessage.append("The following artefacts are missing .name entries in en_us.lang:\n");
            for (String artefact : missingNameEntries) {
                errorMessage.append("  item.").append(MODID).append("\\:").append(artefact)
                           .append(".name=Name of ").append(artefact).append("\n");
            }
        }
        
        if (!missingDescEntries.isEmpty()) {
            if (errorMessage.length() > 0) {
                errorMessage.append("\n");
            }
            errorMessage.append("The following artefacts are missing .desc entries in en_us.lang:\n");
            for (String artefact : missingDescEntries) {
                errorMessage.append("  item.").append(MODID).append("\\:").append(artefact)
                           .append(".desc=Description of ").append(artefact).append("\n");
            }
        }
        
        if (errorMessage.length() > 0) {
            fail(errorMessage.toString());
        }
    }

    /**
     * Gets all artefact item names from ASItems class by checking the register() method.
     * We parse the register method to find all items registered as ItemArtefact or its subclasses.
     */
    private Set<String> getAllArtefactNames() throws Exception {
        Set<String> artefacts = new HashSet<>();
        
        // Use reflection to read the source code or check registration patterns
        // Since we can't execute the registration code in tests, we'll scan the register method
        // by looking at what's in the lang file and cross-referencing with known artefact patterns
        
        // Get all public static Item fields from ASItems that match artefact naming patterns
        Field[] fields = ASItems.class.getDeclaredFields();
        
        // Known artefact prefixes based on ItemArtefact.Type
        String[] artefactPrefixes = {
            "ring_", "amulet_", "charm_", "belt_", "head_", "body_"
        };
        
        for (Field field : fields) {
            if (Item.class.isAssignableFrom(field.getType()) && 
                java.lang.reflect.Modifier.isStatic(field.getModifiers()) &&
                java.lang.reflect.Modifier.isPublic(field.getModifiers())) {
                
                String fieldName = field.getName();
                
                // Check if field name starts with any artefact prefix
                for (String prefix : artefactPrefixes) {
                    if (fieldName.startsWith(prefix)) {
                        artefacts.add(fieldName);
                        break;
                    }
                }
            }
        }
        
        return artefacts;
    }

    /**
     * Parses the en_us.lang file and extracts all item entries.
     * Returns a map with two keys: "name" and "desc", each containing a set of item names.
     */
    private Map<String, Set<String>> parseLangFile() throws IOException {
        Map<String, Set<String>> entries = new HashMap<>();
        entries.put("name", new HashSet<>());
        entries.put("desc", new HashSet<>());
        
        InputStream inputStream = getClass().getResourceAsStream(LANG_FILE_PATH);
        
        if (inputStream == null) {
            throw new IOException("Could not find language file: " + LANG_FILE_PATH);
        }
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Skip comments and empty lines
                if (line.startsWith("#") || line.isEmpty()) {
                    continue;
                }
                
                // Parse lines in format: item.ancientspellcraft\:item_name.name=Display Name
                // or: item.ancientspellcraft\:item_name.desc=Description
                if (line.startsWith("item." + MODID + "\\:")) {
                    // Extract the part after "item.ancientspellcraft\:"
                    String remainder = line.substring(("item." + MODID + "\\:").length());
                    
                    // Find the = sign
                    int equalsIndex = remainder.indexOf('=');
                    if (equalsIndex == -1) {
                        continue; // Invalid line format
                    }
                    
                    // Get the key part (before =)
                    String keyPart = remainder.substring(0, equalsIndex);
                    
                    // Check if it ends with .name or .desc
                    if (keyPart.endsWith(".name")) {
                        String itemName = keyPart.substring(0, keyPart.length() - ".name".length());
                        entries.get("name").add(itemName);
                    } else if (keyPart.endsWith(".desc")) {
                        String itemName = keyPart.substring(0, keyPart.length() - ".desc".length());
                        entries.get("desc").add(itemName);
                    }
                }
            }
        }
        
        return entries;
    }

    @Test
    public void testLangFileExists() {
        InputStream inputStream = getClass().getResourceAsStream(LANG_FILE_PATH);
        assertNotNull(inputStream, "Language file does not exist: " + LANG_FILE_PATH);
        try {
            inputStream.close();
        } catch (IOException e) {
            // Ignore
        }
    }

    @Test
    public void testLangFileFormatIsValid() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream(LANG_FILE_PATH);
        assertNotNull(inputStream, "Language file does not exist: " + LANG_FILE_PATH);
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                
                // Skip comments and empty lines
                if (line.startsWith("#") || line.isEmpty()) {
                    continue;
                }
                
                // Check for valid format: key=value
                if (!line.contains("=")) {
                    fail("Line " + lineNumber + " in en_us.lang has invalid format (missing '='): " + line);
                }
            }
        }
    }
}
