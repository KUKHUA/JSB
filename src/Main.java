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

import Command.Manager;
import JSBCommands.BuildCommand;
import JSBCommands.DependencyCommand;
import JSBCommands.InitCommand;
import JSBCommands.PackageCommand;
import JSBCommands.RunCommand;
import JSBCommands.ConfigCommand;
import JSBCommands.Util.Config;
import JSBCommands.Util.Dependency;

public class Main {

    public static void main(String[] args) throws Exception {
        Manager commandManager = new Manager(
            "Java Simple Build (JSB) -",
            "0.0.5",
            "\"Write Java, not XML.\""
        );
        Config config = new Config();
        Dependency dependency = new Dependency(config);

        commandManager.register("init", new InitCommand(config));

        commandManager.register("build", new BuildCommand(config, dependency));

        commandManager.register("run", new RunCommand(config, dependency));

        commandManager.register(
            "dep",
            new DependencyCommand(config, dependency)
        );

        commandManager.register(
            "package",
            new PackageCommand(config, dependency)
        );
        
        commandManager.register("config", new ConfigCommand(config));

        commandManager.execute(args);

    }
}
