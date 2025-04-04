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
