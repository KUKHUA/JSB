package Command;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Manages the CLI app, registers {@link Handler}s and executes {@link Command}s.
 */
public class Manager {

    private HashMap<String, Handler> commands = new HashMap<>();
    private ArrayList<String> helpInfo = new ArrayList<>();
    private String applicationName;
    private String applicationVersion;
    private String applicationDescription;

    /**
     * Constructor for the Manager, initializes the application name, version and description displayed
     * on the help screen.
     *
     * @param applicationName The name of the application.
     * @param applicationVersion The version of the application.
     * @param applicationDescription The description of the application.
     */
    public Manager(
        String applicationName,
        String applicationVersion,
        String applicationDescription
    ) {
        this.applicationName = applicationName;
        this.applicationVersion = applicationVersion;
        this.applicationDescription = applicationDescription;
        this.helpInfo.add(
                String.format(
                    "%s %s - %s\n\n",
                    this.applicationName,
                    this.applicationVersion,
                    this.applicationDescription
                )
            );
    }

    /**
     * Executes the command based on the user input. If the command is not found, it will display an error message.
     * It will also display help information on error.
     *
     * @param userInput The user input, which is an array of strings.
     */
    public void execute(String[] userInput) {
        try {
            if (userInput[0].isBlank()) {
                throw new IllegalArgumentException("No command entered.");
            }

            Command command = new Command(userInput);
            if (
                command.prefix().equals("help") ||
                command.prefix().equals("--help") ||
                command.prefix().equals("-h") ||
                command.prefix().equals("-help") ||
                command.prefix().equals("--h")
            ) {
                displayHelp();
                return;
            }

            Handler handler = commands.get(command.prefix());
            if (handler != null) {
                command.trim();
                try {
                    handler.handleCommand(command);
                } catch (Exception e) {
                    throw new RuntimeException(
                        String.format(
                            "The command %s failed, %s",
                            command.prefix(),
                            e.getMessage()
                        )
                    );
                }
            } else {
                System.out.println("Command not found.");
                this.displayHelp();
            }
        } catch (Exception e) {
            this.displayHelp();
            System.out.println("---\nFailed to execute command:");
            e.printStackTrace();
            System.out.println("---");
        }
    }

    /**
     * Executes the command based on the user input. If the command is not found, it will display an error message.
     * It will also display help information on error.
     *
     * @param userInput The user input, which is a string.
     */
    public void execute(String userInput) {
        try {
            if (userInput.isBlank()) {
                throw new IllegalArgumentException("No command entered.");
            }

            Command command = new Command(userInput);
            if (
                command.prefix().equals("help") ||
                command.prefix().equals("--help") ||
                command.prefix().equals("-h") ||
                command.prefix().equals("-help") ||
                command.prefix().equals("--h")
            ) {
                displayHelp();
                return;
            }


            Handler handler = commands.get(command.prefix());
            if (handler != null) {
                command.trim();
                try {
                    handler.handleCommand(command);
                } catch (Exception e) {
                    throw new RuntimeException(
                        String.format(
                            "The command %s failed, %s",
                            command.prefix(),
                            e.getMessage()
                        )
                    );
                }
            } else {
                System.out.println("Command not found.");
                this.displayHelp();
            }
        } catch (Exception e) {
            System.out.println("Error processing: " + e.getMessage());
            this.displayHelp();
        }
    }

    /**
     * Displays the help information.
     */
    public void displayHelp() {
        for (String help : this.helpInfo) {
            System.out.println(help);
        }
    }

    /**
     * Registers a command with the {@link Handler} that will handle it.
     *
     * @param commandName The name of the command.
     * @param handler The {@link Handler} that will handle the command.
     */
    public void register(String commandName, Handler handler) {
        commands.put(commandName, handler);
        this.helpInfo.add(
                String.format("%s - %s\n", commandName, handler.getHelpInfo())
            );
    }

    /**
     * Unregisters commands. Note that this isn't very performant because {@link #reloadHelp()}
     */
    public void unregister(String commandName) {
        this.commands.remove(commandName);
        this.reloadHelp();
    }

    public void reloadHelp() {
        this.helpInfo.clear();
        this.helpInfo.add(
                String.format(
                    "%s %s - %s\n\n",
                    this.applicationName,
                    this.applicationVersion,
                    this.applicationDescription
                )
            );

        this.commands.forEach((commandName, handler) -> {
                this.helpInfo.add(
                        String.format(
                            "%s - %s\n",
                            commandName,
                            handler.getHelpInfo()
                        )
                    );
            });
    }
}
