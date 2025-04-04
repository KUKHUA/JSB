package JSBCommands;

import Command.Command;
import Command.Handler;
import JSBCommands.Util.Config;
import JSBCommands.Util.Dependency;
import JSBCommands.Util.Runner;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BuildCommand extends Handler {

    Config config;
    Dependency dependency;

    public BuildCommand(Config config, Dependency dependency)
        throws IOException {
        this.config = config;
        this.dependency = dependency;
    }

    @Override
    public void handleCommand(Command command) throws Exception {
        if (!this.config.ready()) this.config.initConfig();

        if (config.get("deps") != null) {
            String[] deps = config.get("deps").split(",");
            dependency.loadDeps(deps, config.get("dep.path"));
        }

        System.out.println("Building project ...");
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
