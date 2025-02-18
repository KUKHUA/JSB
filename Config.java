import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {

    private Properties properties;

    public Config() {
        // If the file build.properties exists, load it
        // Else, create it
        File buildPropFile = new File("build.properties");
        this.properties = new Properties();
        if (buildPropFile.exists()) {
            loadProperties();
        } else {
            setDefaultProperties();
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
        }
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
