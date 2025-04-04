package JSBCommands.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {

    private Properties properties;
    private boolean isReady = false;

    public Config() {}

    public boolean ready() {
        return isReady;
    }

    public void initConfig() {
        File buildPropFile = new File("build.properties");
        this.properties = new Properties();
        if (buildPropFile.exists()) {
            loadProperties();
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
        properties.setProperty("code.path", "./src");
        properties.setProperty("build.builds", "./classes");
        properties.setProperty("build.verbose", "true");

        properties.setProperty("java.path", "java");
        properties.setProperty("java.class", "Main");

        properties.setProperty("package.cmd", "jar");
        properties.setProperty("package.path", "./dist");
        properties.setProperty("package.name", "MainPackage");

        properties.setProperty("dep.path", "./lib");
        properties.setProperty("repo.url", "https://repo1.maven.org/maven2/");

        String os = System.getProperty("os.name").toLowerCase();
        boolean isWindows = os.contains("win");

        properties.setProperty("system.sep", isWindows ? ";" : ":");
        properties.setProperty("system.shell", isWindows ? "cmd" : "sh");
        properties.setProperty("system.shell.parm", isWindows ? "/c" : "-c");

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
