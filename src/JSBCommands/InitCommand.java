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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Command handler for initializing a new Java project.
 * Creates the basic project structure and example Main class.
 * 
 * @extends Handler
 */
public class InitCommand implements IHandler {

    /** Configuration instance for the project */
    private Config config;
    
    /** Template for the example Main class */
    private final String exampleMainClass =
        "public class Main {\n    public static void main(String[] args) {\n        System.out.println(\"Hello, World!\");\n    }\n}";

    /**
     * Creates a new InitCommand instance.
     * 
     * @param {Config} config - The global configuration object
     */
    public InitCommand(Config config) {
        this.config = config;
    }

    /**
     * Handles the init command execution.
     * Initializes project configuration and creates basic project structure.
     * 
     * @param {Command} command - The command object containing execution parameters
     * @throws {IOException} If there are issues creating directories or files
     */
    @Override
    public void handleCommand(Command command) throws IOException {
        if (!this.config.ready()) this.config.initConfig();

        this.folderCreate("src");
        this.folderCreate("res");

        this.fileCreate("src/Main.java", exampleMainClass);
        System.out.println("Initialized project.");
    }

    /**
     * Creates a new directory if it doesn't exist.
     * 
     * @param {String} name - The name/path of the directory to create
     * @throws {IOException} If directory creation fails
     */
    private void folderCreate(String name) throws IOException {
        File theFile = new File(name);
        if (!theFile.exists()) {
            theFile.mkdir();
        }
    }

    /**
     * Creates a new file with specified content if it doesn't exist.
     * 
     * @param {String} name - The name/path of the file to create
     * @param {String} content - The content to write to the file
     * @throws {IOException} If file creation or writing fails
     */
    private void fileCreate(String name, String content) throws IOException {
        File theFile = new File(name);
        if (!theFile.exists()) {
            theFile.createNewFile();
            if (content.isBlank()) return;
            theFile.setWritable(true);
            FileOutputStream outputStream = new FileOutputStream(theFile);
            outputStream.write(content.getBytes());
            outputStream.close();
        }
    }

    /**
     * Returns help information about the init command.
     * 
     * @return {String} Formatted help text explaining command usage
     */
    @Override
    public String getHelpInfo() {
        return (
            "Initializes a new Java project\n" +
            "  - Creates basic directory structure\n" +
            "  - Generates src/Main.java as a starting point\n" +
            "  - Usage: init\n"
        );
    }
}
