import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

class Main {

    public static void main(String[] args) {
        Config config = new Config();
        final String usage = " Usage: jsb [init|build|run|package|add|remove] ";

        if (args.length == 0) {
            System.out.println("Error: No command provided\n" + usage);
            return;
        }

        if (
            args[0].equals("init") ||
            args[0].equals("initialize") ||
            args[0].equals("create")
        ) {
            System.out.println("Initialized environment.");

            config.initConfig();
            File srcDir = new File("src");
            if (!srcDir.exists()) {
                srcDir.mkdir();
            }

            File mainJavaFile = new File("src/Main.java");
            if (!mainJavaFile.exists()) {
                try {
                    mainJavaFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mainJavaFile.setWritable(true);

            try (FileOutputStream out = new FileOutputStream(mainJavaFile)) {
                out.write(
                    "public class Main {\n    public static void main(String[] args) {\n        System.out.println(\"Hello, World!\");\n    }\n}".getBytes()
                );
            } catch (IOException e) {
                e.printStackTrace();
            }

            return;
        }

        if (!config.ready()) {
            System.out.println(
                "Error: build.properties not found. Run 'jsb init' to initialize the environment."
            );
            return;
        }

        Dependency dep = new Dependency(config);
        BuildManager BuildMan = new BuildManager(config, dep);

        try {
            switch (args[0]) {
                case "build":
                    System.out.println("Building...");
                    BuildMan.build();
                    break;
                case "run":
                    System.out.println("Running...");

                    // Check if there are arguments
                    if (args != null && args.length > 1) {
                        // Remove the first argument
                        String[] runArgs = Arrays.copyOfRange(
                            args,
                            1,
                            args.length
                        );
                        BuildMan.run(runArgs);
                    } else {
                        // If no arguments to pass
                        BuildMan.run(new String[0]); // Pass an empty array if no additional arguments
                    }
                    break;
                case "package":
                case "jar":
                case "pack":
                    System.out.println("Packaging...");
                    BuildMan.jar();
                    break;
                case "add":
                    for (String arg : args) {
                        if (!arg.equals("add") && dep.doesExist(arg)) {
                            dep.add(arg);
                            System.out.println("Added dependency " + arg);
                        } else if (!arg.equals("add")) {
                            System.out.println(
                                "Dependency " + arg + " does not exist."
                            );
                        }
                    }
                    break;
                case "remove":
                    for (String arg : args) {
                        if (
                            !arg.equals("add") &&
                            config.get("deps").contains(arg)
                        ) {
                            dep.remove(arg);
                            System.out.println("Removed dependency: " + arg);
                        } else if (!arg.equals("add")) {
                            System.out.println(
                                "Dependency " + arg + " does not exist."
                            );
                        }
                    }
                    break;
                default:
                    System.out.println("Error: Invalid command\n" + usage);
            }
        } catch (Exception e) {
            System.out.println(
                "Error: Invalid command\n" + usage + e.getMessage()
            );
        }
    }
}
