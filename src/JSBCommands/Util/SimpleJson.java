/*
 * Java Simple Build (JSB) - A straightforward build tool for Java projects
 * Copyright (C) 2025 KUKHUA
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package JSBCommands.Util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple JSON reader/writer for hungh.json config file.
 * No external dependencies - minimal JSON handling for our use case.
 */
public class SimpleJson {
    
    /**
     * Reads a simple JSON config file with mainClass and libraries array
     */
    public static HunghConfig readConfig(String filePath) throws IOException {
        HunghConfig config = new HunghConfig();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean inLibrariesArray = false;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                if (line.contains("\"mainClass\"")) {
                    String[] parts = line.split(":");
                    if (parts.length >= 2) {
                        String mainClass = parts[1].trim();
                        mainClass = mainClass.replace("\"", "").replace(",", "");
                        config.mainClass = mainClass;
                    }
                } else if (line.contains("\"libraries\"")) {
                    inLibrariesArray = true;
                } else if (inLibrariesArray && line.contains("\"")) {
                    String lib = line.replace("\"", "").replace(",", "").trim();
                    if (!lib.isEmpty() && !lib.equals("[") && !lib.equals("]")) {
                        config.libraries.add(lib);
                    }
                } else if (line.contains("]")) {
                    inLibrariesArray = false;
                }
            }
        }
        
        return config;
    }
    
    /**
     * Writes a simple JSON config file
     */
    public static void writeConfig(String filePath, HunghConfig config) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("{");
            writer.println("  \"mainClass\": \"" + config.mainClass + "\",");
            writer.println("  \"libraries\": [");
            
            for (int i = 0; i < config.libraries.size(); i++) {
                String lib = config.libraries.get(i);
                if (i == config.libraries.size() - 1) {
                    writer.println("    \"" + lib + "\"");
                } else {
                    writer.println("    \"" + lib + "\",");
                }
            }
            
            writer.println("  ]");
            writer.println("}");
        }
    }
    
    /**
     * Simple config structure for hungh.json
     */
    public static class HunghConfig {
        public String mainClass = "Main";
        public List<String> libraries = new ArrayList<>();
    }
}