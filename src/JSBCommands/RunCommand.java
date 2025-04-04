package JSBCommands;

import Command.Command;
import Command.Handler;
import JSBCommands.Util.Config;
import JSBCommands.Util.Dependency;
import JSBCommands.Util.Runner;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Handles the execution of Java programs by managing build and run operations.
 * This command handler builds the project and executes the compiled Java program
 * with specified classpath and runtime arguments.
 */
public class RunCommand extends Handler {

    Config config;
    Dependency dependency;

    /**
     * Constructs a new RunCommand instance.
     * 
     * @param config     The configuration object containing system and build settings
     * @param dependency The dependency manager for the project
     * @throws IOException If there's an error initializing the command handler
     */
    public RunCommand(Config config, Dependency dependency) throws IOException {
        this.config = config;
        this.dependency = dependency;
    }

    /**
     * Handles the run command by building and executing the Java program.
     * First builds the project, then executes it with the specified runtime arguments.
     * Uses system-specific shell commands to launch the Java process.
     *
     * @param command The command object containing runtime arguments for the Java program
     * @throws Exception If there's an error during building or execution
     */
    @Override
    public void handleCommand(Command command) throws Exception {
        if (!this.config.ready()) this.config.initConfig();
        new BuildCommand(config, dependency).handleCommand(new Command(""));

        System.out.println("Running project ...");

        ArrayList<String> shellCommand = new ArrayList<>();

        shellCommand.add(config.get("system.shell")); // sh or cmd
        shellCommand.add(config.get("system.shell.parm")); // -c or /c

        ArrayList<String> runCommand = new ArrayList<>();

        runCommand.add(config.get("java.path")); // java
        runCommand.add("-cp"); // set classpath
        runCommand.add(
            // ./classes:./lib:./src:*"
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

        runCommand.add(config.get("java.class"));

        if (!command.raw().isBlank()) runCommand.addAll(command.getList());

        shellCommand.add(String.join(" ", runCommand));
        System.out.println("Running the command: " + shellCommand);
        boolean exitedGood = Runner.runCommand(shellCommand);
        if (exitedGood) System.out.println("Running exited successfully!");
        else System.out.println("Running probably failed : (");
    }

    /**
     * Returns the help information for the run command.
     * 
     * @return A string containing usage instructions and description of the run command
     */
    @Override
    public String getHelpInfo() {
        return (
            "Builds and runs your Java program\n" +
            "  - Compiles source code before executing\n" +
            "  - Usage: run\n"
        );
    }
}
