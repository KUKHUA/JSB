import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class BuildManager {

    Config config;
    Dependency dep;

    public BuildManager(Config config, Dependency dep) {
        this.config = config;
        this.dep = dep;
    }

    private void runCommand(ArrayList<String> command) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.inheritIO();
            pb.redirectErrorStream(true);
            pb.environment().putAll(System.getenv());
            Process p = pb.start();
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void build() {
        if (config.get("deps") != null) {
            String[] deps = config.get("deps").split(",");
            dep.loadDeps(deps, config.get("dep.path"));
        }
        System.out.println("Building project ...");

        ArrayList<String> shellCommand = new ArrayList<>();
        shellCommand.add(config.get("build.shell"));
        shellCommand.add(config.get("build.shell.parm"));

        ArrayList<String> buildCommand = new ArrayList<>();
        buildCommand.add(config.get("build.cmd"));
        buildCommand.add("-d");
        buildCommand.add(config.get("build.builds"));
        buildCommand.add("-cp");
        buildCommand.add(
            String.format(
                "\"%s/*.java%s%s/*\"",
                config.get("build.path"),
                config.get("build.sep"),
                config.get("dep.path")
            )
        );

        if(config.get("build.verbose").equals("true")) {
            buildCommand.add("-verbose");
        }

        buildCommand.add(config.get("build.path") + "/*.java");
        shellCommand.add(String.join(" ", buildCommand));
        System.out.println("Running the command ..." + shellCommand);
        runCommand(shellCommand);
    }

    public void run() {
        build();
        System.out.println("Running project ...");
        ArrayList<String> shellCommand = new ArrayList<>();
        shellCommand.add(config.get("build.shell"));
        shellCommand.add(config.get("build.shell.parm"));

        ArrayList<String> runCommand = new ArrayList<>();
        runCommand.add(config.get("run.cmd"));
        runCommand.add("-cp");
        runCommand.add(
            String.format(
                "\"%s%s%s/*\"",
                config.get("build.builds"),
                config.get("build.sep"),
                config.get("dep.path")
            )
        );
        runCommand.add(config.get("run.class"));

        shellCommand.add(String.join(" ", runCommand));

        System.out.println("Running the command ..." + shellCommand);
        runCommand(shellCommand);
    }

    public void jar() {
        build();
        System.out.println("Packaging project ...");
        ArrayList<String> shellCommand = new ArrayList<>();
        shellCommand.add(config.get("build.shell"));
        shellCommand.add(config.get("build.shell.parm"));
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
        jarCommand.add(config.get("run.class"));
        jarCommand.add("-C");
        jarCommand.add(config.get("build.builds") + "/");
        jarCommand.add(".");
        jarCommand.add("-C");
        jarCommand.add(config.get("dep.path") + "/classes/");
        jarCommand.add(".");

        shellCommand.add(String.join(" ", jarCommand));
        System.out.println("Running the command ..." + shellCommand);

        //unzip every jar file in the dep.path to dep.path/classes and ignore META-INF folder, also write into eixsting folders
        File depClassesPath = new File(config.get("dep.path") + "/classes/");
        if (!depClassesPath.exists()) {
            depClassesPath.mkdirs(); // Ensure the target classes directory exists
        }

        for (File file : dep.listAll()) {
            if (file.getName().endsWith(".jar")) { // Check if the file is a .jar
                try (
                    ZipInputStream zis = new ZipInputStream(
                        new FileInputStream(file)
                    )
                ) {
                    ZipEntry entry;
                    while ((entry = zis.getNextEntry()) != null) {
                        if (!entry.getName().startsWith("META-INF/")) { // Ignore META-INF
                            File newFile = new File(
                                depClassesPath,
                                entry.getName()
                            ).getCanonicalFile();

                            // Ensure the new file path is within the intended directory
                            if (!newFile.toPath().startsWith(depClassesPath.toPath())) {
                                throw new IOException("Bad zip entry: " + entry.getName());
                            }

                            // Ensure parent directories exist
                            if (entry.isDirectory()) {
                                newFile.mkdirs(); // Create directories for entries that are directories
                            } else {
                                newFile.getParentFile().mkdirs(); // Create parent directories if they don't exist

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
                } catch (IOException e) {
                    e.printStackTrace(); // Handle errors properly, or log them as needed
                }
            }
        }

        runCommand(shellCommand);
    }
}
