# Java Simple Build Tool (JSB)

Java Simple Build Tool (JSB) is a straightforward build tool for Java projects. It was created as an alternative to Maven and Gradle, which can be overly complex for simple projects. JSB relies on `java`, `jar`, and `javac`, which are typically bundled together.

## Features

- **Simple Dependency Management**: Add dependencies with `jsb add` and remove them with `jsb remove`.
- **Easy to Use**: Build classes with `jsb build`, run your application with `jsb run`, and package your project with `jsb jar`.

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

        6. Click **New** and add the path where `jsb.jar` is stored (e.g., C:\jsb\).

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
    cd src
    javac *.java
    jar cfe jsb.jar Main *.class
    mv jsb.jar ..
    cd ..
    echo "JSB built to jsb.jar"
````

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request.

## License

This project is licensed under the GPLv3 License. See the [LICENSE](#) file for details.
