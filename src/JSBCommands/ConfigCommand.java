package JSBCommands;
import Command.Command;
import Command.IHandler;
import JSBCommands.Util.Config;

public class ConfigCommand implements IHandler {
    private Config config;

    public ConfigCommand(Config config) {
        this.config = config;
    }

    @Override
    public void handleCommand(Command command) throws Exception {
        if (!this.config.ready()) this.config.initConfig();
        if (command.get(1).isBlank() || command.get(2).isBlank())
            throw argErr("Please provide a key and a value.");


        if(command.get(0).equals("set"))
            config.set(command.get(1), command.get(2));
        else 
             throw argErr("Invalid action. Use 'set' to set a configuration.");

        System.out.println("Successfully set " + command.get(1) + " to " + command.get(2));

    }

    @Override
    public String getHelpInfo() {
        return "Config command to manage project configuration.\n"
            + "Usage: config <action> <key> <value>\n"
            + "Example: config set java.class tld.name.project.Main\n";
    }

    public IllegalArgumentException argErr(String message) {
        return new IllegalArgumentException(message);
    }
    
}
