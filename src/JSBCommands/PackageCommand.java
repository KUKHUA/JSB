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
import JSBCommands.Util.Config;
import JSBCommands.Util.Dependency;
import JSBCommands.Util.Runner;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.nio.file.*;
import java.io.IOException;

/**
 * Handles packaging of Java projects into executable JAR files.
 * This command extracts dependencies, combines class files, and creates a runnable JAR.
 */
public class PackageCommand implements IHandler {

    /** Configuration manager for the build process */
    private Config config;
    
    /** Dependency manager for handling project dependencies */
    private Dependency dependency;

    /**
     * Creates a new PackageCommand instance.
     * 
     * @param config Configuration manager instance
     * @param dependency Dependency manager instance
     * @throws Exception If initialization fails
     */
    public PackageCommand(Config config, Dependency dependency) throws Exception {
        this.config = config;
        this.dependency = dependency;
    }

    /**
     * Handles the package command execution.
     * Performs the following steps:
     * 1. Initializes configuration if not ready
     * 2. Builds the project
     * 3. Extracts dependencies from JAR files
     * 4. Creates the final executable JAR
     *
     * @param command The command to handle
     * @throws Exception If packaging process fails
     */
    @Override
    public void handleCommand(Command command) throws Exception {
        if (!this.config.ready()) {
            this.config.initConfig();
        }
        new BuildCommand(config, dependency).handleCommand(new Command(""));

        System.out.println("Packaging project ...");
        
        // Prepare directories
        File depClassesPath = new File(config.get("dep.path") + "/classes/");
        if (!depClassesPath.exists()) {
            depClassesPath.mkdirs(); // Ensure the target classes directory exists
        }

        // Copy resource files to classes directory
        Path resDir = Paths.get("./res");
        Path classesDir = Paths.get(config.get("build.builds"));

        if (Files.exists(resDir)) {
            Files.walk(resDir)
                .filter(Files::isRegularFile)
                .forEach(src -> {
                    try {
                        Path dest = classesDir.resolve(resDir.relativize(src));
                        Files.createDirectories(dest.getParent());
                        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to copy resource file: " + src, e);
                    }
                });
        }

        // Extract all JAR dependencies
        for (File file : dependency.listAll()) {
            if (file.getName().endsWith(".jar")) {
                extractJarFile(file, depClassesPath);
            }
        }

        // Create the final JAR
        ArrayList<String> shellCommand = new ArrayList<>();
        shellCommand.add(config.get("system.shell"));
        shellCommand.add(config.get("system.shell.parm"));
        
        ArrayList<String> jarCommand = new ArrayList<>();
        jarCommand.add(config.get("package.cmd"));
        jarCommand.add("--create");
        jarCommand.add(
            "--file=" +
            config.get("package.path") +
            "/" +
            config.get("package.name") +
            ".jar"
        );
        jarCommand.add("-e");
        jarCommand.add(config.get("java.class"));
        jarCommand.add("-C");
        jarCommand.add(config.get("build.builds") + "/");
        jarCommand.add(".");
        jarCommand.add("-C");
        jarCommand.add(config.get("dep.path") + "/classes/");
        jarCommand.add(".");

        shellCommand.add(String.join(" ", jarCommand));

        System.out.println("Running the command: " + shellCommand);
        boolean exitedGood = Runner.runCommand(shellCommand);
        if (exitedGood) {
            System.out.println("Packing exited successfully!");
        } else {
            System.out.println("Packing probably failed :(");
        }

        System.out.println("Cleaning up " + config.get("build.builds") + " ...");
        this.deleteDirectoryContents(classesDir);
    }

    /**
     * Extracts a JAR file to the specified directory, ignoring META-INF folders.
     * 
     * @param jarFile The JAR file to extract
     * @param targetDir The target directory for extraction
     * @throws IOException If there's an error extracting the JAR file
     */
    private void extractJarFile(File jarFile, File targetDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(jarFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.getName().startsWith("META-INF/")) { // Ignore META-INF
                    File newFile = new File(targetDir, entry.getName()).getCanonicalFile();
                    File canonicalTargetDir = targetDir.getCanonicalFile();
                    // Prevent Zip Slip vulnerability: newFile must be under targetDir
                    if (!newFile.getPath().startsWith(canonicalTargetDir.getPath() + File.separator)) {
                        throw new IOException("Bad zip entry: " + entry.getName());
                    }
                    // Handle directory entries
                    if (entry.isDirectory()) {
                        if (!newFile.exists()) {
                            newFile.mkdirs();
                        }
                    } else {
                        // Create parent directories for files if they don't exist
                        newFile.getParentFile().mkdirs();

                        // Write the file content
                        try (BufferedOutputStream bos = new BufferedOutputStream(
                                new FileOutputStream(newFile))) {
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = zis.read(buffer)) != -1) {
                                bos.write(buffer, 0, length);
                            }
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }

    /**
     * Recursively deletes all contents of a directory without deleting the directory itself.
     * 
     * @param dir The directory whose contents should be deleted
     * @throws IOException If there's an error deleting files or directories
     */
    private void deleteDirectoryContents(Path dir) throws IOException {
        if (!Files.exists(dir)) {
            return;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    deleteDirectoryContents(entry); // recurse
                }
                Files.delete(entry);
            }
        }
    }

    /**
     * Returns help information about the package command.
     *
     * @return String containing usage information and command description
     */
    @Override
    public String getHelpInfo() {
        return (
            "Packages Java source files into a runnable JAR file\n" +
            "  - Builds all source files\n" +
            "  - Creates a JAR file in the ./dist directory\n" +
            "  - Usage: package\n"
        );
    }
}
