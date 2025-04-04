package JSBCommands.Util;

import java.util.ArrayList;

/**
 * Utility class for executing system commands.
 */
public class Runner {

    /**
     * Executes a system command with the given arguments.
     * 
     * @param command An ArrayList of strings where the first element is the command
     *               and subsequent elements are arguments
     * @return boolean Returns true if the command executed successfully (exit code 0),
     *         false otherwise
     * @throws Exception if there's an error executing the command (caught internally)
     * 
     * @example
     * ArrayList<String> cmd = new ArrayList<>();
     * cmd.add("ls");
     * cmd.add("-l");
     * boolean success = Runner.runCommand(cmd);
     */
    public static boolean runCommand(ArrayList<String> command) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.inheritIO();
            pb.redirectErrorStream(true);
            pb.environment().putAll(System.getenv());
            Process p = pb.start();
            int exitCode = p.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }
}
