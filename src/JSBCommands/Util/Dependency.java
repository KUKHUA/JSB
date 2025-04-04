package JSBCommands.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Dependency {

    private Config config;
    private String BASE_URL = "https://repo1.maven.org/maven2/";

    public Dependency(Config config) {
        this.config = config;
        if (config.ready()) BASE_URL = config.get("repo.url");
    }

    public File get(String mavenString) throws IOException, URISyntaxException {
        if (mavenString == null || mavenString.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Maven string cannot be null or empty: " + mavenString
            );
        }

        String[] parts = mavenString.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException(
                "Invalid Maven string format. Expected: groupId:artifactId:version, Got: " +
                mavenString
            );
        }

        String groupID = parts[0].replace(".", "/");
        String artifactID = parts[1];
        String version = parts[2];
        String jarFileName = artifactID + "-" + version + ".jar";
        String path =
            groupID + "/" + artifactID + "/" + version + "/" + jarFileName;

        URL url = new URI(BASE_URL + path).toURL();
        File outputFile = new File(jarFileName);

        // Download and save the file
        try (
            InputStream in = url.openStream();
            FileOutputStream out = new FileOutputStream(outputFile)
        ) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        return outputFile;
    }

    public void loadDeps(String[] listOfDeps, String localPathString)
        throws Exception {
        for (String dep : listOfDeps) {
            if (hasFile(dep)) continue;
            System.out.println("Loading dependency: " + dep);
            File depFile = this.get(dep);
            File localPath = new File(localPathString);
            if (!localPath.exists()) {
                localPath.mkdirs();
            }
            File newFile = new File(localPath, depFile.getName());
            depFile.renameTo(newFile);
            System.out.println("Finished loading dependency: " + dep);
        }
    }

    public void add(String mavenString) {
        List<String> depList = new ArrayList<>();
        if (config.get("deps") != null) {
            String[] currentDepList = config.get("deps").split(",");
            for (String dep : currentDepList) {
                depList.add(dep);
            }
        }

        depList.add(mavenString);
        String newDeps = String.join(",", depList);
        config.set("deps", newDeps);
    }

    public ArrayList<File> listAll() {
        //Should be like this lib/dep1.jar, lib/dep2.jar, lib/dep3.jar
        ArrayList<File> depFiles = new ArrayList<>();
        if (config.get("deps") != null) {
            String[] currentDepList = config.get("deps").split(",");
            for (String dep : currentDepList) {
                String[] parts = dep.split(":");
                if (parts.length == 3) {
                    String artifactID = parts[1];
                    String version = parts[2];
                    String jarFileName = artifactID + "-" + version + ".jar";
                    File depFile = new File(
                        config.get("dep.path") + "/" + jarFileName
                    );
                    if (depFile.exists()) {
                        depFiles.add(depFile);
                    }
                }
            }
        }
        return depFiles;
    }

    public void remove(String mavenString) {
        List<String> depList = new ArrayList<>();
        if (config.get("deps") != null) {
            String[] currentDepList = config.get("deps").split(",");
            for (String dep : currentDepList) {
                if (!dep.equals(mavenString)) {
                    depList.add(dep);
                }
            }
        }

        String newDeps = String.join(",", depList);
        config.set("deps", newDeps);
    }

    public boolean hasFile(String mavenString) {
        String[] parts = mavenString.split(":");
        if (parts.length != 3) {
            return false;
        }
        String artifactID = parts[1];
        String version = parts[2];
        String jarFileName = artifactID + "-" + version + ".jar";
        File depFile = new File(config.get("dep.path") + "/" + jarFileName);
        return depFile.exists();
    }

    public boolean doesExist(String mavenString) {
        String[] parts = mavenString.split(":");
        if (parts.length != 3) {
            return false;
        }
        String artifactID = parts[1];
        String version = parts[2];
        String jarFileName = artifactID + "-" + version + ".jar";
        //Test if http request is successful
        try {
            URL url = new URI(
                BASE_URL +
                parts[0].replace(".", "/") +
                "/" +
                artifactID +
                "/" +
                version +
                "/" +
                jarFileName
            ).toURL();
            url.openStream().close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
