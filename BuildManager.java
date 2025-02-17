import java.io.File;

public class BuildManager {

    Config config;
    Dependency dep;

    public BuildManager(Config config, Dependency dep) {
        this.config = config;
        this.dep = dep;
    }

    public void build() {
        if (config.get("deps") != null) {
            String[] deps = config.get("deps").split(",");
            dep.loadDeps(deps, config.get("dep.path"));
        }
        System.out.println("Building project ...");

        File libFile = new File(config.get("dep.path"));
        String buildCommandString = "";
        if (libFile.exists()) {
            buildCommandString = String.format(
                "%s -verbose -cp \"%s\" -d %s %s/%s.java",
                config.get("build.cmd"),
                config.get("dep.path"),
                config.get("build.builds"),
                config.get("build.path"),
                config.get("run.class")
            );
        } else {
            buildCommandString = String.format(
                "%s -verbose -d %s %s/%s.java",
                config.get("build.cmd"),
                config.get("build.builds"),
                config.get("build.path"),
                config.get("run.class")
            );
        }
        System.out.println("Running the command ..." + buildCommandString);
        try {
            ProcessBuilder builder = new ProcessBuilder(
                buildCommandString.split(" ")
            );
            builder.redirectErrorStream(true);
            builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            Process process = builder.start();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        build();
        System.out.println("Running project ...");
        String runCommandString = String.format(
            "%s -cp %s %s",
            config.get("run.cmd"),
            config.get("build.builds"),
            config.get("run.class")
        );
        System.out.println("Running the command ..." + runCommandString);
        try {
            ProcessBuilder builder = new ProcessBuilder(
                runCommandString.split(" ")
            );
            builder.redirectErrorStream(true);
            builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            Process process = builder.start();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void jar() {
        build();
        System.out.println("Packaging project ...");
        String packageCommandString = String.format(
            "%s -cvfe %s/%s.jar %s -C %s .",
            config.get("package.cmd"),
            config.get("package.path"),
            config.get("package.name"),
            config.get("run.class"),
            config.get("build.builds")
        );
        System.out.println("Running the command ..." + packageCommandString);
        try {
            ProcessBuilder builder = new ProcessBuilder(
                packageCommandString.split(" ")
            );
            builder.redirectErrorStream(true);
            builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            Process process = builder.start();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
