package com.windanesz.ancientspellcraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.windanesz.ancientspellcraft.registry.ASSpells;
import electroblob.wizardry.spell.Spell;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test to verify that all spells in ASSpells have corresponding sound entries in sounds.json.
 * 
 * For each spell in ASSpells, the sounds.json file should contain an entry in the format:
 * "spell.<spell_name>": {"category": "spells", "sounds": ["..."]}
 * 
 * Some spells may have additional sound variants like:
 * "spell.<spell_name>.start"
 * "spell.<spell_name>.loop"
 * "spell.<spell_name>.end"
 */
public class SpellSoundTest {

    private static final String MODID = "ancientspellcraft";
    private static final String SOUNDS_FILE_PATH = "/assets/" + MODID + "/sounds.json";

    @Test
    public void testAllSpellsHaveSoundEntries() throws Exception {
        // Get all spell names from ASSpells
        Set<String> allSpells = getAllSpellNames();
        
        // Get all sound entries from sounds.json
        Set<String> soundEntries = parseSoundsJson();
        
        // Track missing sound entries
        List<String> missingSpellSounds = new ArrayList<>();
        
        // Verify each spell has at least one sound entry
        for (String spellName : allSpells) {
            String spellSoundKey = "spell." + spellName;
            
            // Check if there's any sound entry that starts with "spell.<spell_name>"
            boolean hasSound = soundEntries.stream()
                    .anyMatch(sound -> sound.equals(spellSoundKey) || sound.startsWith(spellSoundKey + "."));
            
            if (!hasSound) {
                missingSpellSounds.add(spellName);
            }
        }
        
        // Build error message if there are missing entries
        if (!missingSpellSounds.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append(missingSpellSounds.size())
                       .append(" spell(s) are missing sound entries in sounds.json:\n\n");
            
            for (String spellName : missingSpellSounds) {
                errorMessage.append("  \"spell.").append(spellName)
                           .append("\": {\"category\": \"spells\", \"sounds\": [\"ancientspellcraft:")
                           .append(spellName).append("\"]},\n");
            }
            
            fail(errorMessage.toString());
        }
    }

    /**
     * Gets all spell names from ASSpells class.
     * A spell is any public static Spell field.
     */
    private Set<String> getAllSpellNames() throws Exception {
        Set<String> spells = new HashSet<>();
        
        // Get all public static Spell fields from ASSpells
        Field[] fields = ASSpells.class.getDeclaredFields();
        
        for (Field field : fields) {
            if (Spell.class.isAssignableFrom(field.getType()) && 
                java.lang.reflect.Modifier.isStatic(field.getModifiers()) &&
                java.lang.reflect.Modifier.isPublic(field.getModifiers())) {
                
                // The field name is the spell's registry name (without the modid prefix)
                String fieldName = field.getName();
                spells.add(fieldName);
            }
        }
        
        return spells;
    }

    /**
     * Parses the sounds.json file and extracts all sound entry keys.
     * Returns a set of all sound keys (e.g., "spell.hellgate", "spell.arcane_beam.start")
     */
    private Set<String> parseSoundsJson() throws IOException {
        Set<String> soundKeys = new HashSet<>();
        
        InputStream inputStream = getClass().getResourceAsStream(SOUNDS_FILE_PATH);
        
        if (inputStream == null) {
            throw new IOException("Could not find sounds file: " + SOUNDS_FILE_PATH);
        }
        
        try (InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8")) {
            JsonParser parser = new JsonParser();
            JsonObject soundsJson = parser.parse(reader).getAsJsonObject();
            
            // Extract all keys from the JSON object
            for (Map.Entry<String, JsonElement> entry : soundsJson.entrySet()) {
                soundKeys.add(entry.getKey());
            }
        }
        
        return soundKeys;
    }

    @Test
    public void testSoundsFileExists() {
        InputStream inputStream = getClass().getResourceAsStream(SOUNDS_FILE_PATH);
        assertNotNull(inputStream, "Sounds file does not exist: " + SOUNDS_FILE_PATH);
        try {
            inputStream.close();
        } catch (IOException e) {
            // Ignore
        }
    }

    @Test
    public void testSoundsFileIsValidJson() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream(SOUNDS_FILE_PATH);
        assertNotNull(inputStream, "Sounds file does not exist: " + SOUNDS_FILE_PATH);
        
        try (InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8")) {
            JsonParser parser = new JsonParser();
            JsonObject soundsJson = parser.parse(reader).getAsJsonObject();
            
            // Verify it's a valid JSON object
            assertNotNull(soundsJson, "sounds.json should be a valid JSON object");
            assertFalse(soundsJson.entrySet().isEmpty(), "sounds.json should not be empty");
        }
    }

    @Test
    public void testAllSpellSoundsAreInCorrectFormat() throws IOException {
        Set<String> soundEntries = parseSoundsJson();
        
        List<String> invalidEntries = new ArrayList<>();
        
        for (String soundKey : soundEntries) {
            // Check if it's a spell sound (starts with "spell.")
            if (soundKey.startsWith("spell.")) {
                // Validate format - should be "spell.<name>" or "spell.<name>.<variant>"
                String[] parts = soundKey.split("\\.");
                
                // Should have at least 2 parts (spell.<name>) and at most 3 (spell.<name>.<variant>)
                if (parts.length < 2 || parts.length > 3) {
                    invalidEntries.add(soundKey);
                }
                
                // If there's a variant, it should be one of the known variants
                if (parts.length == 3) {
                    String variant = parts[2];
                    Set<String> validVariants = new HashSet<>(Arrays.asList(
                            "start", "loop", "end", "charge", "hit", "activate"
                    ));
                    
                    if (!validVariants.contains(variant)) {
                        // This is just a warning, not a failure, as there might be custom variants
                        System.out.println("Warning: Unusual spell sound variant: " + soundKey);
                    }
                }
            }
        }
        
        if (!invalidEntries.isEmpty()) {
            fail("The following spell sound entries have invalid format: " + invalidEntries);
        }
    }

    @Test
    public void testNoOrphanedSpellSounds() throws Exception {
        // Get all spell names from ASSpells
        Set<String> allSpells = getAllSpellNames();
        
        // Get all sound entries from sounds.json
        Set<String> soundEntries = parseSoundsJson();
        
        // Find spell sounds that don't correspond to any actual spell
        // Only check base spell sounds (without .start, .loop, .end suffixes)
        List<String> orphanedSounds = new ArrayList<>();
        
        for (String soundKey : soundEntries) {
            if (soundKey.startsWith("spell.")) {
                // Extract the spell name (without "spell." prefix)
                String spellName = soundKey.substring("spell.".length());
                
                // Skip sounds with variant suffixes (.start, .loop, .end, etc.)
                // These are validated by the base spell name
                int dotIndex = spellName.indexOf('.');
                if (dotIndex > 0) {
                    continue; // Skip variant sounds, they're checked via their base spell
                }
                
                // Check if this spell exists in ASSpells
                if (!allSpells.contains(spellName)) {
                    orphanedSounds.add(soundKey);
                }
            }
        }
        
        if (!orphanedSounds.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append(orphanedSounds.size())
                       .append(" spell sound entries in sounds.json don't correspond to any spell in ASSpells:\n\n");
            
            for (String orphaned : orphanedSounds) {
                errorMessage.append("  ").append(orphaned).append("\n");
            }
            
            errorMessage.append("\nThese may be from Wizardry base mod or typos. Please verify.");
            
            fail(errorMessage.toString());
        }
    }
}
