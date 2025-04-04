package JSBCommands;

import Command.Command;
import Command.Handler;
import JSBCommands.Util.Config;
import JSBCommands.Util.Dependency;
import java.io.File;
import java.util.ArrayList;

public class DependencyCommand extends Handler {

    Config config;
    Dependency dependency;

    public DependencyCommand(Config config, Dependency dependency) {
        this.config = config;
        this.dependency = dependency;
    }

    @Override
    public void handleCommand(Command command) {
        if (!this.config.ready()) this.config.initConfig();
        String action = command.get(0);
        String dep = command.get(1);
        boolean needsDep = action.equals("add") || action.equals("remove");

        if (action.isBlank() || (needsDep && dep.isBlank())) {
            throw new IllegalArgumentException(
                "Invalid arguments. Please check the help information."
            );
        }

        switch (action) {
            case "add":
                ArrayList<String> deps = command.getList();
                deps.remove(0);

                if (deps.isEmpty()) {
                    throw new IllegalArgumentException(
                        "No dependencies provided. Please check the help information."
                    );
                }

                for (String currDep : deps) {
                    if (dependency.doesExist(currDep)) {
                        dependency.add(currDep);
                        System.out.println(
                            "Dependency " + currDep + " added successfully."
                        );
                    } else {
                        System.out.println(
                            "Dependency " + currDep + " does not exist."
                        );
                    }
                }
                break;
            case "remove":
                if (dependency.doesExist(dep)) {
                    dependency.remove(dep);
                    System.out.println(
                        "Dependency " + dep + " removed successfully."
                    );
                } else {
                    System.out.println(
                        "Dependency " + dep + " does not exist."
                    );
                }
                break;
            case "list":
                ArrayList<File> depsList = dependency.listAll();
                if (depsList.isEmpty()) {
                    System.out.println("No dependencies found.");
                } else {
                    System.out.println("Dependencies:");
                    for (File depName : depsList) {
                        System.out.println("  - " + depName.getName());
                    }
                }
                break;
            default:
                throw new IllegalArgumentException(
                    "Invalid action. Please check the help information."
                );
        }
    }

    @Override
    public String getHelpInfo() {
        return (
            "Used to manage dependencies\n" +
            "  - Usage: dep <action> <?dependency>\n" +
            "  - Actions:\n" +
            "    - add <dependency> : Add a new dependency.\n" +
            "    - remove <dependency> : Remove an existing dependency.\n" +
            "    - list : List all dependencies.\n"
        );
    }
}
