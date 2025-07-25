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
    Config config;
    /** Dependency manager for handling project dependencies */
    Dependency dependency;

    /**
     * Creates a new PackageCommand instance.
     * 
     * @param config Configuration manager instance
     * @param dependency Dependency manager instance
     * @throws Exception If initialization fails
     */
    public PackageCommand(Config config, Dependency dependency)
        throws Exception {
        this.config = config;
        this.dependency = dependency;
    }

    /**
     * Handles the package command execution.
     * Performs the following steps:
     * 1. Initializes configuration if not ready
     * 2. Extracts dependencies from JAR files
     * 3. Creates the final executable JAR
     *
     * @param command The command to handle
     * @throws Exception If packaging process fails
     */
    @Override
    public void handleCommand(Command command) throws Exception {
        if (!this.config.ready()) this.config.initConfig();
        new BuildCommand(config, dependency).handleCommand(new Command(""));

        System.out.println("Packaging project ...");
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
        //unzip every jar file in the dep.path to dep.path/classes and ignore META-INF folder, also write into eixsting folders
        File depClassesPath = new File(config.get("dep.path") + "/classes/");
        if (!depClassesPath.exists()) {
            depClassesPath.mkdirs(); // Ensure the target classes directory exists
        }

        Path resDir = Paths.get("./res");
        Path classesDir = Paths.get(config.get("build.builds"));

        Files.walk(resDir)
            .filter(Files::isRegularFile)
            .forEach(src -> {
                try {
                    Path dest = classesDir.resolve(resDir.relativize(src));
                    Files.createDirectories(dest.getParent());
                    Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });


        for (File file : dependency.listAll()) {
            if (file.getName().endsWith(".jar")) { // Check if the file is a .jar
                try (
                    ZipInputStream zis = new ZipInputStream(
                        new FileInputStream(file)
                    )
                ) {
                    ZipEntry entry;
                    while ((entry = zis.getNextEntry()) != null) {
                        if (!entry.getName().startsWith("META-INF/")) { // Ignore META-INF
                            // Create the File object, considering whether it's a directory or not
                            File newFile = new File(
                                depClassesPath,
                                entry.getName()
                            ).getCanonicalFile();

                            // Handle directory entries
                            if (entry.isDirectory()) {
                                // Create directories for entries that are directories
                                if (!newFile.exists()) {
                                    newFile.mkdirs();
                                }
                            } else {
                                // Create parent directories for files if they don't exist
                                newFile.getParentFile().mkdirs();

                                // Write the file content
                                try (
                                    BufferedOutputStream bos =
                                        new BufferedOutputStream(
                                            new FileOutputStream(newFile)
                                        )
                                ) {
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
        }

        System.out.println("Running the command: " + shellCommand);
        boolean exitedGood = Runner.runCommand(shellCommand);
        if (exitedGood) System.out.println("Packing exited successfully!");
        else System.out.println("Packing probably failed : (");

        System.out.println("Cleaning up " + config.get("build.builds") + " ...");
        this.deleteDirectoryContents(classesDir);
    }

    private void deleteDirectoryContents(Path dir) throws IOException {
        if (!Files.exists(dir)) return;
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
            "  - Builds all source files" +
            "  - Creates a JAR file in the ./dist directory\n" +
            "  - Usage: package\n"
        );
    }
}
