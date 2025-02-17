# Java Simple Build Tool (JSB)

Java Simple Build Tool (JSB) is a straightforward build tool for Java projects. It was created as an alternative to Maven and Gradle, which can be overly complex for simple projects. JSB relies on `java`, `jar`, and `javac`, which are typically bundled together.

## Features

- **Simple Dependency Management**: Add dependencies with `jsb add` and remove them with `jsb remove`.
- **Easy to Use**: Build classes with `jsb build`, run your application with `jsb run`, and package your project with `jsb jar`.
- **Cross-Platform**: Available for both Windows and Linux.

## Installation

### Windows

1. Download the latest release from the [JSB Releases](#) page.
2. Extract the downloaded ZIP file to a directory of your choice.
3. Add the JSB directory to your system PATH:
    - Open the Start Menu, search for "Environment Variables", and select "Edit the system environment variables".
    - Click on "Environment Variables".
    - Under "System variables", find the `Path` variable and click "Edit".
    - Click "New" and add the path to the JSB directory.
    - Click "OK" to close all dialogs.

### Linux

1. Download the latest release from the [JSB Releases](#) page.
2. Extract the downloaded TAR file to a directory of your choice.
3. Add the JSB directory to your PATH by editing your shell profile:
    - Open your terminal.
    - Edit your profile file (e.g., `~/.bashrc`, `~/.zshrc`):
      ```sh
      nano ~/.bashrc
      ```
    - Add the following line to the end of the file:
      ```sh
      export PATH=$PATH:/path/to/jsb
      ```
    - Save the file and reload your profile:
      ```sh
      source ~/.bashrc
      ```

## Usage

To get started with JSB, follow these steps:

### Initializing a Project

Use the `jsb init` command to create a basic project structure with `src/Main.java`:
```sh
jsb init
```
Edit `src/Main.java` to your liking.

### Managing Dependencies

#### Adding a Dependency

To add a dependency, use the `jsb add` command followed by the dependency in Gradle short format:
```sh
jsb add group:name:version
```

#### Removing a Dependency

To remove a dependency, use the `jsb remove` command followed by the dependency in Gradle short format:
```sh
jsb remove group:name:version
```

### Building, Running, and Packaging

#### Building

To compile your Java classes, use the `jsb build` command:
```sh
jsb build
```

#### Running

To run your application, use the `jsb run` command:
```sh
jsb run
```

#### Packaging

To package your project into a JAR file, use the `jsb jar` command:
```sh
jsb jar
```


## Releases

JSB has builds available for both Windows and Linux. You can find the latest releases on the [JSB Releases](#) page.

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request.

## License

This project is licensed under the GPLv3 License. See the [LICENSE](#) file for details.