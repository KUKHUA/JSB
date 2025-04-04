package JSBCommands;

import Command.Command;
import Command.Handler;
import JSBCommands.Util.Config;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class InitCommand extends Handler {

    Config config;
    final String exampleMainClass =
        "public class Main {\n    public static void main(String[] args) {\n        System.out.println(\"Hello, World!\");\n    }\n}";

    /**
     * Constructor for the init command.
     * It creates src/Main.java with a example class if it does not exist.
     * @param config - the global config, {@link Config}
     */
    public InitCommand(Config config) {
        this.config = config;
    }

    /**
     * The class that handles the command.
     */
    @Override
    public void handleCommand(Command command) throws IOException {
        if (!this.config.ready()) this.config.initConfig();

        this.folderCreate("src");

        this.fileCreate("src/Main.java", exampleMainClass);
        System.out.println("Initialized project.");
    }

    /**
     * Creates a folder if it doesn't already exist.
     * @param name  - name of the folder
     */
    private void folderCreate(String name) throws IOException {
        File theFile = new File(name);
        if (!theFile.exists()) {
            theFile.mkdir();
        }
    }

    /**
     * Creates a file if it doesn't alrealdy exist.
     * @param name  - name of the file
     * @param content - what to write to the file
     */
    private void fileCreate(String name, String content) throws IOException {
        File theFile = new File(name);
        if (!theFile.exists()) {
            theFile.createNewFile();
            if (content.isBlank()) return;
            theFile.setWritable(true);
            FileOutputStream outputStream = new FileOutputStream(theFile);
            outputStream.write(content.getBytes());
            outputStream.close();
        }
    }

    @Override
    public String getHelpInfo() {
        return (
            "Initializes a new Java project\n" +
            "  - Creates basic directory structure\n" +
            "  - Generates src/Main.java as a starting point\n" +
            "  - Usage: init\n"
        );
    }
}
