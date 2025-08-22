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

package Command;

/**
 * Handler interface for command execution.
 * All command handlers must implement this interface to be registered
 * with the command manager.
 *
 * @author pascal
 * @version 1
 */
public interface IHandler {

    /**
     * Executes the command with the provided arguments.
     * This method contains the core logic for processing the command
     * and should parse all user input appropriately.
     * 
     * @param command The command object containing user arguments
     * @throws Exception If there's an error executing the command
     */
    void handleCommand(Command command) throws Exception;

    /**
     * Returns help information about the command.
     * This information is displayed when the user requests help
     * or when command execution fails.
     * 
     * @return String containing help text and usage information
     */
    String getHelpInfo();
}
