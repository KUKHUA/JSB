package Command;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Parses the user's input in mutiple ways.
 */
public class Command {

    private String userInput;
    private String commandName;
    private ArrayList<String> argsList;

    /**
     * Constructor for the Command class, initializes the user input and splits it into a list of arguments.
     *
     * @param userInput The user input, which is a string.
     */
    public Command(String userInput) {
        this.userInput = userInput;
        this.argsList = new ArrayList<>(Arrays.asList(userInput.split(" ")));
        this.commandName = this.argsList.get(0);
    }

    /**
     * Constructor for the Command class, initializes the user input and splits it into a list of arguments.
     *
     * @param userInput The user input, which is an array of strings.
     */
    public Command(String[] userInput) {
        this.userInput = String.join(" ", userInput);
        this.argsList = new ArrayList<>(Arrays.asList(userInput));
        this.commandName = this.argsList.get(0);
    }

    /**
     * Removes the first argument from the list of arguments.
     */
    public void trim() {
        this.argsList.remove(0);
        this.userInput = String.join(" ", this.argsList);
    }

    /**
     * Returns the argument at the specified index.
     *
     * @param argIndex The index of the argument.
     * @return The argument at the specified index.
     */
    public String get(int argIndex) {
        try {
            if (
                argIndex < 0 || argIndex >= this.argsList.size()
            ) throw new IndexOutOfBoundsException("Index out of bounds");

            return this.argsList.get(argIndex);
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }

    /**
     * Gives you the number of argumnets.
     * @return The number of arguments.
     */
    public int size() {
        return this.argsList.size();
    }

    /**
     * Returns a array list of arguments.
     * @return The list of arguments.
     * @see #get(int)
     * @see #size()
     * @see #raw()
     */
    public ArrayList<String> getList() {
        return this.argsList;
    }

    /**
     * Returns the entire user input. Duplicate of {@link raw}
     *
     * @return The entire user input as a string.
     */
    public String get() {
        return this.userInput;
    }

    /**
     * Returns the entire user input. Duplicate of {@link get}
     *
     * @return The entire user input as a string.
     */
    public String raw() {
        return this.userInput;
    }

    /**
     * Returns the original command name.
     *
     * @return The original command name as a string.
     */
    public String prefix() {
        return this.commandName;
    }
}
