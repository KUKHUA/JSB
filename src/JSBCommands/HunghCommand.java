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

package JSBCommands;

import Command.Command;
import Command.IHandler;
import JSBCommands.Util.SimpleJson;
import JSBCommands.Util.SimpleJson.HunghConfig;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Hungh's No-Dependency Builder Agent
 * Handles library bundling with subcommands: add-lib, list-libs, build
 */
public class HunghCommand implements IHandler {

    private static final String HUNGH_DIR = ".hungh";
    private static final String CONFIG_FILE = "hungh.json";
    
    /**
     * Handles the hungh command execution with subcommands
     *
     * @param command The command object containing subcommand and parameters
     * @throws Exception If there's an error during command execution
     */
    @Override
    public void handleCommand(Command command) throws Exception {
        if (command.size() == 0) {
            throw new IllegalArgumentException("No subcommand provided. Use: add-lib, list-libs, or build");
        }
        
        String subcommand = command.get(0);
        
        switch (subcommand) {
            case "add-lib":
                handleAddLib(command);
                break;
            case "list-libs":
                handleListLibs();
                break;
            case "build":
                handleBuild();
                break;
            default:
                throw new IllegalArgumentException("Unknown subcommand: " + subcommand + ". Use: add-lib, list-libs, or build");
        }
    }
    
    /**
     * Handles the add-lib subcommand
     */
    private void handleAddLib(Command command) throws Exception {
        if (command.size() < 2) {
            throw new IllegalArgumentException("Usage: hungh add-lib <path-to-jar>");
        }
        
        String jarPath = command.get(1);
        File jarFile = new File(jarPath);
        
        if (!jarFile.exists()) {
            throw new FileNotFoundException("JAR file not found: " + jarPath);
        }
        
        if (!jarFile.getName().toLowerCase().endsWith(".jar")) {
            throw new IllegalArgumentException("File must be a .jar file: " + jarPath);
        }
        
        // Create .hungh directory if it doesn't exist
        File hunghDir = new File(HUNGH_DIR);
        if (!hunghDir.exists()) {
            hunghDir.mkdirs();
        }
        
        // Copy JAR to .hungh directory
        String fileName = jarFile.getName();
        File destFile = new File(hunghDir, fileName);
        
        if (destFile.exists()) {
            System.out.println("WARNING: Library " + fileName + " already exists, replacing it.");
        }
        
        Files.copy(jarFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        
        // Update config
        HunghConfig config = loadConfig();
        String relativePath = HUNGH_DIR + "/" + fileName;
        if (!config.libraries.contains(relativePath)) {
            config.libraries.add(relativePath);
            saveConfig(config);
        }
        
        System.out.println("Added library: " + fileName + " to .hungh/ folder");
    }
    
    /**
     * Handles the list-libs subcommand
     */
    private void handleListLibs() throws Exception {
        HunghConfig config = loadConfig();
        
        if (config.libraries.isEmpty()) {
            System.out.println("NO LIBRARIES ADDED YET! HUNGH IS READY TO BUNDLE BUT NEEDS SOME JARS TO CHOMP ON!");
            return;
        }
        
        System.out.println("Currently added libraries:");
        for (String lib : config.libraries) {
            File libFile = new File(lib);
            if (libFile.exists()) {
                System.out.println("  - " + lib);
            } else {
                System.out.println("  - " + lib + " (MISSING!)");
            }
        }
    }
    
    /**
     * Handles the build subcommand
     */
    private void handleBuild() throws Exception {
        HunghConfig config = loadConfig();
        
        if (config.libraries.isEmpty()) {
            System.out.println("NO LIBRARIES TO BUNDLE! HUNGH NEEDS SOME JARS TO WORK WITH!");
            return;
        }
        
        System.out.println("HUNGH IS BUILDING YOUR FAT JAR WITH " + config.libraries.size() + " LIBRARIES!");
        
        // Create output directories
        File distDir = new File("./dist");
        if (!distDir.exists()) {
            distDir.mkdirs();
        }
        
        File classesDir = new File("./classes");
        if (!classesDir.exists()) {
            classesDir.mkdirs();
        }
        
        // Extract all libraries and detect conflicts
        Set<String> allEntries = new HashSet<>();
        Map<String, List<String>> conflicts = new HashMap<>();
        
        File tempExtractDir = new File("./hungh-temp-extract");
        if (tempExtractDir.exists()) {
            deleteDirectory(tempExtractDir);
        }
        tempExtractDir.mkdirs();
        
        for (String libPath : config.libraries) {
            File libFile = new File(libPath);
            if (!libFile.exists()) {
                System.out.println("WARNING: Library not found: " + libPath);
                continue;
            }
            
            System.out.println("Extracting: " + libFile.getName());
            extractJarWithConflictDetection(libFile, tempExtractDir, allEntries, conflicts, libFile.getName());
        }
        
        // Print conflicts in ALL CAPS
        if (!conflicts.isEmpty()) {
            System.out.println();
            System.out.println("⚠️  HUNGH DETECTED CONFLICTS! ⚠️");
            for (Map.Entry<String, List<String>> conflict : conflicts.entrySet()) {
                System.out.println("CONFLICT: " + conflict.getKey().toUpperCase() + 
                                 " EXISTS IN MULTIPLE JARS: " + 
                                 String.join(", ", conflict.getValue()).toUpperCase());
            }
            System.out.println("HUNGH WILL USE THE LAST VERSION OF EACH CONFLICTING FILE!");
            System.out.println();
        }
        
        // Create final JAR
        String outputJar = "./dist/" + config.mainClass + "-hungh-bundle.jar";
        createFinalJar(tempExtractDir, classesDir, outputJar, config.mainClass);
        
        // Cleanup
        deleteDirectory(tempExtractDir);
        
        System.out.println("HUNGH SUCCESSFULLY CREATED: " + outputJar);
        System.out.println("RUN WITH: java -jar " + outputJar);
    }
    
    /**
     * Extracts a JAR file and detects conflicts
     */
    private void extractJarWithConflictDetection(File jarFile, File extractDir, Set<String> allEntries, 
                                               Map<String, List<String>> conflicts, String jarName) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(jarFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String entryName = entry.getName();
                
                // Skip META-INF
                if (entryName.startsWith("META-INF/")) {
                    continue;
                }
                
                // Detect conflicts
                if (allEntries.contains(entryName)) {
                    conflicts.computeIfAbsent(entryName, k -> new ArrayList<>()).add(jarName);
                } else {
                    allEntries.add(entryName);
                }
                
                File outputFile = new File(extractDir, entryName);
                
                if (entry.isDirectory()) {
                    outputFile.mkdirs();
                } else {
                    outputFile.getParentFile().mkdirs();
                    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile))) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = zis.read(buffer)) != -1) {
                            bos.write(buffer, 0, length);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }
    
    /**
     * Creates the final bundled JAR
     */
    private void createFinalJar(File extractDir, File classesDir, String outputJar, String mainClass) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputJar))) {
            // Add extracted library files
            addDirectoryToZip(zos, extractDir, "");
            
            // Add compiled classes if they exist
            if (classesDir.exists()) {
                addDirectoryToZip(zos, classesDir, "");
            }
            
            // Add manifest
            ZipEntry manifestEntry = new ZipEntry("META-INF/MANIFEST.MF");
            zos.putNextEntry(manifestEntry);
            
            String manifest = "Manifest-Version: 1.0\n" +
                            "Main-Class: " + mainClass + "\n" +
                            "Created-By: Hungh's No-Dependency Builder Agent\n";
            zos.write(manifest.getBytes());
            zos.closeEntry();
        }
    }
    
    /**
     * Adds directory contents to ZIP
     */
    private void addDirectoryToZip(ZipOutputStream zos, File dir, String prefix) throws IOException {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                String entryName = prefix + file.getName();
                
                if (file.isDirectory()) {
                    addDirectoryToZip(zos, file, entryName + "/");
                } else {
                    ZipEntry entry = new ZipEntry(entryName);
                    zos.putNextEntry(entry);
                    
                    try (FileInputStream fis = new FileInputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = fis.read(buffer)) != -1) {
                            zos.write(buffer, 0, length);
                        }
                    }
                    zos.closeEntry();
                }
            }
        }
    }
    
    /**
     * Deletes a directory recursively
     */
    private void deleteDirectory(File dir) throws IOException {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            dir.delete();
        }
    }
    
    /**
     * Loads hungh configuration from hungh.json
     */
    private HunghConfig loadConfig() throws IOException {
        File configFile = new File(CONFIG_FILE);
        if (!configFile.exists()) {
            return new HunghConfig();
        }
        return SimpleJson.readConfig(CONFIG_FILE);
    }
    
    /**
     * Saves hungh configuration to hungh.json
     */
    private void saveConfig(HunghConfig config) throws IOException {
        SimpleJson.writeConfig(CONFIG_FILE, config);
    }
    
    /**
     * Returns help information about the hungh command
     *
     * @return String containing usage information and command description
     */
    @Override
    public String getHelpInfo() {
        return (
            "Hungh's No-Dependency Builder Agent - bundles JAR libraries into a single executable\n" +
            "  - add-lib <path-to-jar> : Adds a library to be bundled into .hungh/ folder\n" +
            "  - list-libs : Shows all currently added libraries\n" +
            "  - build : Creates a fat JAR with all libraries bundled\n" +
            "  - Usage: hungh <subcommand> [arguments]\n" +
            "  - Config is stored in hungh.json\n"
        );
    }
}