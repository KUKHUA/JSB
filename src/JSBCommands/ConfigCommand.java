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

/**
 * Command handler for managing project configuration settings.
 * Allows users to set configuration values through the command line interface.
 */
public class ConfigCommand implements IHandler {
    /** Configuration manager instance */
    private Config config;

    /**
     * Creates a new ConfigCommand instance.
     * 
     * @param config The configuration manager to use for setting values
     */
    public ConfigCommand(Config config) {
        this.config = config;
    }

    /**
     * Handles the config command execution.
     * Currently supports only the 'set' action for setting configuration values.
     * 
     * @param command The command object containing the action, key, and value
     * @throws Exception If the command arguments are invalid or the action is unsupported
     */
    @Override
    public void handleCommand(Command command) throws Exception {
        if (!this.config.ready()) {
            this.config.initConfig();
        }
        
        if (command.get(1).isBlank() || command.get(2).isBlank()) {
            throw argErr("Please provide a key and a value.");
        }

        if (command.get(0).equals("set")) {
            config.set(command.get(1), command.get(2));
        } else {
            throw argErr("Invalid action. Use 'set' to set a configuration.");
        }

        System.out.println("Successfully set " + command.get(1) + " to " + command.get(2));
    }

    /**
     * Returns help information about the config command.
     * 
     * @return String containing usage instructions and example
     */
    @Override
    public String getHelpInfo() {
        return "Config command to manage project configuration.\n"
            + "Usage: config <action> <key> <value>\n"
            + "Example: config set java.class tld.name.project.Main\n";
    }

    /**
     * Creates a formatted IllegalArgumentException with the given message.
     * 
     * @param message The error message to include in the exception
     * @return IllegalArgumentException with the provided message
     */
    public IllegalArgumentException argErr(String message) {
        return new IllegalArgumentException(message);
    }
    
}
