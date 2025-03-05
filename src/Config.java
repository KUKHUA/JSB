import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {

    private Properties properties;
    private boolean isReady = false;

    public Config() {
        // If the file build.properties exists, load it
        // Else, create it
        File buildPropFile = new File("build.properties");
        this.properties = new Properties();
        if (buildPropFile.exists()) {
            loadProperties();
        }
    }

    public boolean ready() {
        return isReady;
    }

    public void initConfig() {
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
        setDefaultProperties();
    }

    private void saveProperties() {
        try {
            properties.store(new FileOutputStream("build.properties"), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadProperties() {
        try {
            FileInputStream in = new FileInputStream("build.properties");
            properties.load(in);
            in.close();
            isReady = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setDefaultProperties() {
        properties.setProperty("build.cmd", "javac");
        properties.setProperty("build.path", "./src");
        properties.setProperty("build.builds", "./classes");

        properties.setProperty("run.cmd", "java");
        properties.setProperty("run.class", "Main");

        properties.setProperty("package.cmd", "jar");
        properties.setProperty("package.path", "./dist");
        properties.setProperty("package.name", "MainPackage");

        properties.setProperty("dep.path", "./lib");
        properties.setProperty("repo.url", "https://repo1.maven.org/maven2/");
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            properties.setProperty("build.sep", ";");
            properties.setProperty("build.shell", "cmd");
            properties.setProperty("build.shell.parm", "/c");
        } else {
            properties.setProperty("build.sep", ":");
            properties.setProperty("build.shell", "sh");
            properties.setProperty("build.shell.parm", "-c");
        }

        saveProperties();
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public void set(String key, String value) {
        properties.setProperty(key, value);
        saveProperties();
    }
}
