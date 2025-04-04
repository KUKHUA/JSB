package JSBCommands;

import Command.Command;
import Command.Handler;
import JSBCommands.Util.Config;
import JSBCommands.Util.Dependency;
import JSBCommands.Util.Runner;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Handles packaging of Java projects into executable JAR files.
 * This command extracts dependencies, combines class files, and creates a runnable JAR.
 */
public class PackageCommand extends Handler {

    /** Configuration manager for the build process */
    Config config;
    /** Dependency manager for handling project dependencies */
    Dependency dependency;

    /**
     * Creates a new PackageCommand instance.
     * 
     * @param config Configuration manager instance
     * @param dependency Dependency manager instance
     * @throws Exception If initialization fails
     */
    public PackageCommand(Config config, Dependency dependency)
        throws Exception {
        this.config = config;
        this.dependency = dependency;
    }

    /**
     * Handles the package command execution.
     * Performs the following steps:
     * 1. Initializes configuration if not ready
     * 2. Extracts dependencies from JAR files
     * 3. Creates the final executable JAR
     *
     * @param command The command to handle
     * @throws Exception If packaging process fails
     */
    @Override
    public void handleCommand(Command command) throws Exception {
        if (!this.config.ready()) this.config.initConfig();
        new BuildCommand(config, dependency).handleCommand(new Command(""));

        System.out.println("Packaging project ...");
        ArrayList<String> shellCommand = new ArrayList<>();
        shellCommand.add(config.get("system.shell"));
        shellCommand.add(config.get("system.shell.parm"));
        ArrayList<String> jarCommand = new ArrayList<>();
        jarCommand.add(config.get("package.cmd"));
        jarCommand.add("--create");
        jarCommand.add(
            "--file=" +
            config.get("package.path") +
            "/" +
            config.get("package.name") +
            ".jar"
        );
        jarCommand.add("-e");
        jarCommand.add(config.get("java.class"));
        jarCommand.add("-C");
        jarCommand.add(config.get("build.builds") + "/");
        jarCommand.add(".");
        jarCommand.add("-C");
        jarCommand.add(config.get("dep.path") + "/classes/");
        jarCommand.add(".");

        shellCommand.add(String.join(" ", jarCommand));
        //unzip every jar file in the dep.path to dep.path/classes and ignore META-INF folder, also write into eixsting folders
        File depClassesPath = new File(config.get("dep.path") + "/classes/");
        if (!depClassesPath.exists()) {
            depClassesPath.mkdirs(); // Ensure the target classes directory exists
        }

        for (File file : dependency.listAll()) {
            if (file.getName().endsWith(".jar")) { // Check if the file is a .jar
                try (
                    ZipInputStream zis = new ZipInputStream(
                        new FileInputStream(file)
                    )
                ) {
                    ZipEntry entry;
                    while ((entry = zis.getNextEntry()) != null) {
                        if (!entry.getName().startsWith("META-INF/")) { // Ignore META-INF
                            // Create the File object, considering whether it's a directory or not
                            File newFile = new File(
                                depClassesPath,
                                entry.getName()
                            ).getCanonicalFile();

                            // Ensure the new file path is within the intended directory
                            if (!newFile.toPath().startsWith(depClassesPath.toPath())) {
                                throw new Exception("Bad zip entry: " + entry.getName());
                            }

                            // Handle directory entries
                            if (entry.isDirectory()) {
                                // Create directories for entries that are directories
                                if (!newFile.exists()) {
                                    newFile.mkdirs();
                                }
                            } else {
                                // Create parent directories for files if they don't exist
                                newFile.getParentFile().mkdirs();

                                // Write the file content
                                try (
                                    BufferedOutputStream bos =
                                        new BufferedOutputStream(
                                            new FileOutputStream(newFile)
                                        )
                                ) {
                                    byte[] buffer = new byte[1024];
                                    int length;
                                    while ((length = zis.read(buffer)) != -1) {
                                        bos.write(buffer, 0, length);
                                    }
                                }
                            }
                        }
                        zis.closeEntry();
                    }
                }
            }
        }

        System.out.println("Running the command: " + shellCommand);
        boolean exitedGood = Runner.runCommand(shellCommand);
        if (exitedGood) System.out.println("Packing exited successfully!");
        else System.out.println("Packing probably failed : (");
    }

    /**
     * Returns help information about the package command.
     *
     * @return String containing usage information and command description
     */
    @Override
    public String getHelpInfo() {
        return (
            "Packages Java source files into a runnable JAR file\n" +
            "  - Builds all source files" +
            "  - Creates a JAR file in the ./dist directory\n" +
            "  - Usage: build\n"
        );
    }
}
