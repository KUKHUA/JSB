/*
 * Java Simple Build (JSB) - A straightforward build tool for Java projects
 * Copyright (C) 2025 KUKHUA
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package JSBCommands.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Configuration manager that handles loading, saving and accessing build properties.
 * Uses a properties file to store configuration settings for build, Java, packaging
 * and dependency management.
 */
public class Config {

    /** Properties object storing the configuration key-value pairs */
    private Properties properties;
    
    /** Flag indicating if the configuration has been successfully loaded */
    private boolean isReady = false;


    /**
     * Checks if the configuration has been loaded successfully
     * @return true if configuration is ready, false otherwise
     */
    public boolean ready() {
        return isReady;
    }

    /**
     * Initializes the configuration by loading existing properties file
     * or creating default properties if file doesn't exist
     */
    public void initConfig() {
        File buildPropFile = new File("build.properties");
        this.properties = new Properties();
        if (buildPropFile.exists()) {
            loadProperties();
        }
        setDefaultProperties();
    }

    /**
     * Saves current properties to the build.properties file
     */
    private void saveProperties() {
        try {
            properties.store(new FileOutputStream("build.properties"), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads properties from the build.properties file
     */
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

    /**
     * Sets default configuration properties for build, Java, packaging and system settings.
     * Properties include build paths, commands, Java runtime settings, and system-specific values.
     */
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

    /**
     * Retrieves a property value by its key
     * @param key The property key to look up
     * @return The value associated with the key, or null if not found
     */
    public String get(String key) {
        return properties.getProperty(key);
    }

    /**
     * Sets a property value and saves it to the properties file
     * @param key The property key to set
     * @param value The value to associate with the key
     */
    public void set(String key, String value) {
        properties.setProperty(key, value);
        saveProperties();
    }
}
