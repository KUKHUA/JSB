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
 * Handler class for a command.
 *
 * @author pascal
 * @version 1
 */
public abstract class Handler {

    /**
     * What your command actually does.
     * Parse all user input here.
     */
    public abstract void handleCommand(Command command) throws Exception;

    /**
     * Help information about your command.
     */
    public abstract String getHelpInfo();
}
