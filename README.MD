# Java Simple Build Tool (JSB)

Java Simple Build Tool (JSB) is a straightforward build tool for Java projects. It was created as an alternative to Maven and Gradle, which can be overly complex for simple projects. JSB relies on `java`, `jar`, and `javac` existing on the system.

## Table of Contents
- [Java Simple Build Tool (JSB)](#java-simple-build-tool-jsb)
  - [Table of Contents](#table-of-contents)
  - [Installation](#installation)
    - [Pre-Built](#pre-built)
    - [Building](#building)
  - [Usage](#usage)
    - [Initialize](#initialize)
    - [Running](#running)
    - [Packaging](#packaging)
    - [Dependencies](#dependencies)
    - [Configuration](#configuration)
      - [Build Settings](#build-settings)
      - [Java Runtime Settings](#java-runtime-settings)
      - [Package Settings](#package-settings)
      - [Dependency Settings](#dependency-settings)
      - [System Settings](#system-settings)
  - [Contributing](#contributing)
  - [License](#license)


## Installation
### Pre-Built
1. Download the latest `jsb.jar` release [here](https://github.com/KUKHUA/JSB/releases).
3. Make sure you have Java (and the devlopment stuff) installed.
2. Add this to your path.
    * On Windows:
        1.  Place `jsb.jar` in a permanent location, such as `C:\jsb\jsb.jar`.

        2.  Open the Start menu and search for **Environment Variables**.

        3.  Click **Edit the system environment variables**.

        4.  In the **System Properties** window, click **Environment Variables**.

        5.  Under **System Variables**, find the **Path** variable and select Edit.

        6. Add a path for "jsb" to "java -jar C:\jsb\jsb.jar".

        7.  Click **OK** on all winbdows and restart your terminal or PC.
    * On Linux:
        1.  Move `jsb.jar` to a desired directory, (e.g.,`/usr/local/lib/jsb.jar`).

        2.  Open a terminal and edit your shell configuration file:
            * For **Bash**: `vim ~/.bashrc`
            * For **Zsh** (MacOS): `vim ~/.zshrc`

        3. Add a alias for `jsb`
            ```bash
            alias jsb="java -jar /usr/local/lib/jsb.jar"
            ```

        4. Relaunch your terminal, or reload your `rc` file:
            * For **Bash**: `source ~/.bashrc `
            * For **Zsh** (MacOS): `source ~/.zshrc` 
4. Your done.

### Building
```bash
    git clone https://github.com/KUKHUA/JSB.git
    cd JSB
    find ./src -name "*.java" > sources.txt
    javac -d ./classes @sources.txt  # Use the list of found files for compilation
    jar cfe jsb.jar Main *.class
    mv jsb.jar ..
    cd ..
    echo "JSB built to jsb.jar"
````

## Usage
### Initialize
To initialize a new project:

Create a new folder for your JSB project:
```bash
mkdir project
cd project
jsb init
```
This will create a src/Main.java file and a build.properties configuration file.

### Running
To run your application, execute: `jsb run`
Note: This command must be run from the directory containing your build.properties file. You can also use `jsb build` to only build classes.

### Packaging
To package your application into a JAR:
```bash
jsb package
```
Note: During packaging, all JAR files in the lib directory will be extracted to classes to ensure proper classpath integration.

### Dependencies
To manage dependencies, use the Gradle-style coordinate notation:
```bash
jsb add org.json:json:20250107    # Add a dependency
jsb remove org.json:json:20250107 # Remove a dependency
```
Note: Dependencies are downloaded automatically during the build process, not when adding them to the project.

### Configuration
All configuration is stored in `build.properties`. Here are the available settings:

#### Build Settings
- **build.cmd**: Java compiler command (default: `javac`)
- **build.builds**: Directory for compiled `.class` files (default: `./classes`)
- **build.verbose**: Enable verbose compilation output (default: `true`)
- **code.path**: Source code directory (default: `./src`)

#### Java Runtime Settings  
- **java.path**: Java runtime command (default: `java`)
- **java.class**: Main class to execute (default: `Main`)

#### Package Settings
- **package.cmd**: JAR creation command (default: `jar`)
- **package.path**: Output directory for packaged JARs (default: `./dist`) 
- **package.name**: Name of output JAR without extension (default: `MainPackage`)

#### Dependency Settings
- **dep.path**: Directory for downloaded dependencies (default: `./lib`)
- **repo.url**: Maven repository URL (default: `https://repo1.maven.org/maven2/`)

#### System Settings
These are automatically set based on your OS:
- **system.sep**: Path separator (`;` on Windows, `:` on Unix)
- **system.shell**: Shell command (`cmd` on Windows, `sh` on Unix) 
- **system.shell.parm**: Shell parameter (`/c` on Windows, `-c` on Unix)

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request.

## License

This project is licensed under the GPLv3 License. See the [LICENSE](#) file for details.
