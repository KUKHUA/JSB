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
import JSBCommands.Util.Runner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DocCommand handles the generation of JavaDoc documentation for the project.
 * It generates HTML documentation from Java source files and outputs them to a ./docs directory.
 */
public class DocCommand implements IHandler {

    /** Configuration manager for build settings */
    private Config config;

    /**
     * Constructs a new DocCommand with the specified configuration.
     * 
     * @param config Configuration manager containing build settings
     */
    public DocCommand(Config config) {
        this.config = config;
    }

    /**
     * Handles the documentation command execution by generating JavaDoc documentation.
     * The method performs the following steps:
     * 1. Initializes configuration if not ready
     * 2. Creates the docs directory if it doesn't exist
     * 3. Finds all Java source files in the code path
     * 4. Constructs and executes the javadoc command
     *
     * @param command The command object containing documentation parameters
     * @throws Exception If there's an error during the documentation generation process
     */
    @Override
    public void handleCommand(Command command) throws Exception {
        if (!this.config.ready()) {
            this.config.initConfig();
        }

        System.out.println("Generating JavaDoc documentation ...");
        
        // Create docs directory
        File docsDir = new File("./docs");
        if (!docsDir.exists()) {
            docsDir.mkdirs();
        }

        // Find all Java source files
        List<String> javaFiles = Files.walk(Paths.get(config.get("code.path")))
            .filter(p -> p.toString().endsWith(".java"))
            .map(Path::toString)
            .collect(Collectors.toList());

        if (javaFiles.isEmpty()) {
            System.out.println("No Java files found in " + config.get("code.path"));
            return;
        }

        // Build javadoc command
        ArrayList<String> shellCommand = new ArrayList<>();
        shellCommand.add(config.get("system.shell")); // sh or cmd
        shellCommand.add(config.get("system.shell.parm")); // -c or /c

        ArrayList<String> docCommand = new ArrayList<>();
        docCommand.add("javadoc"); // javadoc command
        docCommand.add("-d"); // destination directory
        docCommand.add("./docs"); // output to docs directory
        docCommand.add("-sourcepath"); // source path
        docCommand.add(config.get("code.path")); // ./src
        docCommand.add("-private"); // include private members
        docCommand.add("-author"); // include @author tags
        docCommand.add("-version"); // include @version tags
        docCommand.add("-use"); // create class use pages
        docCommand.add("-splitindex"); // split index into multiple files
        docCommand.add("-windowtitle"); // window title
        docCommand.add("\"JSB Documentation\"");
        docCommand.add("-doctitle"); // documentation title
        docCommand.add("\"<h1>Java Simple Build (JSB) Documentation</h1>\"");
        
        // Add all Java files
        docCommand.addAll(javaFiles);
        
        shellCommand.add(String.join(" ", docCommand));

        System.out.println("Running the command: " + shellCommand);
        boolean exitedGood = Runner.runCommand(shellCommand);
        if (exitedGood) {
            System.out.println("Documentation generation completed successfully!");
            System.out.println("Documentation is available in ./docs/index.html");
        } else {
            System.out.println("Documentation generation probably failed :(");
        }
    }

    /**
     * Returns help information about the documentation command.
     * 
     * @return String containing usage instructions and command description
     */
    @Override
    public String getHelpInfo() {
        return (
            "Generates JavaDoc documentation for all Java source files\n" +
            "  - Output is saved to the ./docs directory\n" +
            "  - Creates comprehensive HTML documentation with private members\n" +
            "  - Usage: doc\n"
        );
    }
}