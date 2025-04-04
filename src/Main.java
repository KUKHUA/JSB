import Command.Manager;
import JSBCommands.BuildCommand;
import JSBCommands.DependencyCommand;
import JSBCommands.InitCommand;
import JSBCommands.PackageCommand;
import JSBCommands.RunCommand;
import JSBCommands.Util.Config;
import JSBCommands.Util.Dependency;

public class Main {

    public static void main(String[] args) throws Exception {
        Manager commandManager = new Manager(
            "Java Simple Build (JSB) -",
            "0.0.4-alpha-rework",
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

        commandManager.execute(args);
    }
}
