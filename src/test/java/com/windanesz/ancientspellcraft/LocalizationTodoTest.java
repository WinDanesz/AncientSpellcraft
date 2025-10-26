package com.windanesz.ancientspellcraft;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test to verify that the en_us.lang file has no incomplete TODO entries.
 * 
 * Any localization entry ending with "=TODO" should be completed before release.
 */
public class LocalizationTodoTest {

    private static final String MODID = "ancientspellcraft";
    private static final String LANG_FILE_PATH = "/assets/" + MODID + "/lang/en_us.lang";

    @Test
    public void testNoTodoEntriesInLangFile() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream(LANG_FILE_PATH);
        assertNotNull(inputStream, "Language file does not exist: " + LANG_FILE_PATH);
        
        List<TodoEntry> todoEntries = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            String line;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                
                // Skip comments and empty lines
                if (line.startsWith("#") || line.isEmpty()) {
                    continue;
                }
                
                // Check if line ends with =TODO
                if (line.contains("=TODO")) {
                    String key = line.substring(0, line.indexOf('=')).trim();
                    todoEntries.add(new TodoEntry(lineNumber, key, line));
                }
            }
        }
        
        if (!todoEntries.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Found ").append(todoEntries.size())
                       .append(" incomplete TODO entries in en_us.lang:\n\n");
            
            for (TodoEntry entry : todoEntries) {
                errorMessage.append("  Line ").append(entry.lineNumber)
                           .append(": ").append(entry.key)
                           .append("\n    ").append(entry.fullLine)
                           .append("\n");
            }
            
            errorMessage.append("\nPlease complete these localization entries before release.");
            
            fail(errorMessage.toString());
        }
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

    /**
     * Helper class to store TODO entry information
     */
    private static class TodoEntry {
        final int lineNumber;
        final String key;
        final String fullLine;

        TodoEntry(int lineNumber, String key, String fullLine) {
            this.lineNumber = lineNumber;
            this.key = key;
            this.fullLine = fullLine;
        }
    }
}
