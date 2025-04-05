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
import Command.Handler;
import JSBCommands.Util.Config;
import JSBCommands.Util.Dependency;
import JSBCommands.Util.Runner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * BuildCommand handles the compilation of Java source files in the project.
 * It manages dependency resolution and builds Java files into class files.
 */
public class BuildCommand extends Handler {

    /** Configuration manager for build settings */
    Config config;
    /** Dependency manager for handling project dependencies */
    Dependency dependency;

    /**
     * Constructs a new BuildCommand with the specified configuration and dependency manager.
     * 
     * @param config Configuration manager containing build settings
     * @param dependency Dependency manager for handling project dependencies
     * @throws IOException If there's an error accessing configuration files
     */
    public BuildCommand(Config config, Dependency dependency)
        throws IOException {
        this.config = config;
        this.dependency = dependency;
    }

    /**
     * Handles the build command execution by compiling Java source files.
     * The method performs the following steps:
     * 1. Initializes configuration if not ready
     * 2. Loads dependencies if specified
     * 3. Finds all Java source files in the code path
     * 4. Constructs and executes the build command
     *
     * @param command The command object containing build parameters
     * @throws Exception If there's an error during the build process
     */
    @Override
    public void handleCommand(Command command) throws Exception {
        if (!this.config.ready()) this.config.initConfig();

        if (config.get("deps") != null) {
            String[] deps = config.get("deps").split(",");
            dependency.loadDeps(deps, config.get("dep.path"));
        }
        System.out.println("Building project ...");
        
        File buildDir = new File(config.get("build.builds"));
        if (!buildDir.exists()) {
            buildDir.mkdirs();
        } else {
            try {
                Files.walk(Paths.get(config.get("build.builds")))
                    .filter(p -> p.toString().endsWith(".class"))
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            throw new UncheckedIOException("Failed to delete class file: " + p, e);
                        }
                    });
            } catch (IOException e) {
                throw new IOException("Failed to clean build directory", e);
            }
        }


        List<String> javaFiles = Files.walk(Paths.get(config.get("code.path")))
            .filter(p -> p.toString().endsWith(".java"))
            .map(Path::toString)
            .collect(Collectors.toList());

        if (javaFiles.isEmpty()) {
            throw new FileNotFoundException(
                "No Java files found in " + config.get("code.path")
            );
        }

        ArrayList<String> shellCommand = new ArrayList<>();

        shellCommand.add(config.get("system.shell")); // sh or cmd
        shellCommand.add(config.get("system.shell.parm")); // -c or /c

        ArrayList<String> buildCommand = new ArrayList<>();
        buildCommand.add(config.get("build.cmd")); //javac
        buildCommand.add("-d"); // speficy where to put complied classes
        buildCommand.add(config.get("build.builds")); // ./classes
        buildCommand.add("-cp"); // speficy classpath

        buildCommand.add(
            // ./classes:./lib:./src:*
            String.format(
                "\"%s%s%s%s%s%s%s\"",
                config.get("build.builds"),
                config.get("system.sep"),
                config.get("dep.path"),
                config.get("system.sep"),
                config.get("code.path"),
                config.get("system.sep"),
                "*"
            )
        );

        if (config.get("build.verbose").equals("true")) {
            buildCommand.add("-verbose");
        }

        buildCommand.add(String.join(" ", javaFiles));
        shellCommand.add(String.join(" ", buildCommand));

        System.out.println("Running the command: " + shellCommand);
        boolean exitedGood = Runner.runCommand(shellCommand);
        if (exitedGood) System.out.println("Building exited successfully!");
        else System.out.println("Building probably failed : (");
    }

    /**
     * Returns help information about the build command.
     * 
     * @return String containing usage instructions and command description
     */
    @Override
    public String getHelpInfo() {
        return (
            "Builds all Java source files into class files\n" +
            "  - Output is saved to the ./classes directory\n" +
            "  - Also downloads and installs any pending dependencies\n" +
            "  - Usage: build\n"
        );
    }
}
