# Contributing guide

Thank you for your interest in contributing to AuraSkills. We want to make it easy to get started and open your first
PR, so please make sure to read this guide to set up a development environment and understand our code standards.

## Getting started

You will need to set up a development environment to build AuraSkills in.
The recommended IDE is [IntelliJ IDEA](https://www.jetbrains.com/idea/), but you
may use any Java IDE of your choice. If you already have this set up, skip to [Code style](#code-style).

### Fork and clone AuraSkills

First, create a fork of AuraSkills on GitHub to your personal GitHub account.
Then, [clone the project](https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository)
to your computer (requires Git to be installed).

### Setting Up the JDK

AuraSkills uses Java 21 for building with Gradle and in source files.
If you are using IntelliJ, you should set the SDK in `File -> Project Structure` to a Java 21 JDK.
We recommend Eclipse Temurin.

### Building AuraSkills

To build the jar file from the source code, use one of the following commands:

Linux / macOS

```
./gradlew clean build
```

Windows

```
.\gradlew.bat clean build
```

The output jar can be found in the `build/libs` directory.

### Starting a server

AuraSkills runs on a standard Paper server, so follow the [steps](https://docs.papermc.io/paper/getting-started/) for
running one if you don't have a server set up already. Once you have a server, simply add the jar from the previous step
to the `plugins` folder
and restart the server.

### Advanced: How to build AuraSkills and deploy a server in one click in IntelliJ

When you are building and testing your changes to AuraSkills, manually running Gradle commands, copying the jar file,
and restarting the server can be tedious. Luckily IntelliJ IDEA allows you to automate this process using run
configurations.

**Install the Ant plugin**

You must install the [Ant](https://plugins.jetbrains.com/plugin/23025-ant) plugin in IntelliJ,
which is needed to create an Ant build file for copying the jar file.

**Create a build.xml file**

Create a build.xml file in the root project directory (e.g. AuraSkills/build.xml) and
copy the following configuration:

```xml

<project name="AuraSkills">
    <property file="gradle.properties"/>
    <target name="Copy Jar">
        <copy file="build/libs/AuraSkills-${projectVersion}.jar" tofile="/PATH/TO/YOUR/SERVER/plugins/AuraSkills.jar"/>
    </target>
</project>
```

Replace the toFile value with the actual path of your Paper server (On macOS/Linux use an absolute path prefixed with /,
on Windows use C:/ or whatever your drive is named).

**Add as Ant build file**

Then right-click your file in the IntelliJ project window and click "Add as Ant Build File". This will make this copy
jar
action available in the following steps.

**Open the Run Configurations menu**

Locate the run configuration menu on the top right of your editor directly to the left of the run triangle. It
should have text saying "Current File" with a dropdown arrow (you might see something other than "Current File", but it
should still look like a dropdown).

<img src="https://i.imgur.com/iGiG0vs.png" width="100" alt="Current File dropdown">

Click the dropdown and then click "Edit Configurations...".

**Add a new run configuration**

Click the plus button on the top left of the menu and select "JAR Application". You can name your configuration
anything,
such as "1.21.5" or whatever server version you are running.

Then enter the following configuration:

- Set Path to JAR to your Paper server's JAR file, which should be in the format paper-*.jar unless you renamed it.
- Set Program arguments to "nogui".
- Set Working directory to the root directory of your Paper server (whatever directory your Paper jar is in).
- In JRE, select or install a Java 21 JDK

In the "Before launch" panel, click the plus button and select "Run Gradle task" to create a new Gradle task to run
before
starting the server. Select `AuraSkills` as the Gradle project and add `clean build` in the Tasks: field. Click Ok.

Click the plus again to create another Before launch configuration and select "Run Ant Target". Select the name of the
Ant target you created in the build.xml file and click Ok.

Your configuration should now look similar to the following (your Path to JAR and Working directory will differ):

<img src="https://i.imgur.com/rs6xxb5.png" width="500" alt="Run configuration window">

Click Ok on the main Run/Debug Configurations menu to save your configuration and close the menu.

**Running the configuration**

To build AuraSkills and run your server, ensure your configuration is selected in the run configuration dropdown on
the top right of the IDE and click the green triangle. To stop the server, use the `stop` command in the server's
console that opens in the IDE or click the red square on the top right.

That's it! This run configuration can save a lot of time when you are testing AuraSkills or any Minecraft plugin.

## Code style

AuraSkills uses the Checkstyle Gradle plugin to enforce a consistent code style. Checkstyle is not configured to run
when
you build the plugin normally, so you must run the following command to manually run checkstyle:

Linux / macOS

```
./gradlew checkstyleMain checkstyleTest
```

Windows

```
.\gradlew.bat checkstyleMain checkstyleTest
```

### Checkstyle IntelliJ plugin

Instead of running checkstyle manually, you can install
the [Checkstyle-IDEA](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea)
plugin if you are using IntelliJ IDEA to show checkstyle violations inside your editor as errors.

Once you install the plugin, open IntelliJ settings and go to Tools -> Checkstyle. Under Configuration File, click the
plus button to add a new configuration. Give the configuration a description and with "Add a local checkstyle file"
selected click the "Browse" button and select the checkstyle file located at `config/checkstyle/checkstyle.xml`.

The configuration should look like this:

<img src="https://i.imgur.com/h2nNzf2.png" width="400" alt="Configuration window">

Click Next and then Finish. Finally, you must **check the checkbox next to the configuration** you just added to set it
as active, then click Ok to close settings.

## Commit message standards

- Separate the subject from the body (if you have one) with a blank line
- **Subject**
    - Use the imperative mood in the subject
        - Correct: "Add ...", "Change...", "Fix ..."
        - Incorrect: "Added ...", "Changes ...", "fix: ..."
    - The subject line cannot be more than 72 characters
    - The subject should be as concise as possible while generally summarizing your changes
    - Capitalize the subject line
    - Do not end the subject with a period
    - Do not link to issues or PRs in the subject, as merging will do this automatically
- **Body**
    - Use a commit body only if the subject line cannot fully explain your changes, such as naming specific config paths
      you added
    - Enclose config paths and references to file names in backticks
        - E.g. \`some.config.path\` or \`abilities.yml\`

## Pull request guidelines

Before you open a pull request, ensure you meet all the following:

- Your changes are pushed on a branch in your fork (not master)
- Your branch is rebased onto the current master branch if needed
- **You have tested your changes on a server to verify that it works**
- The [Checkstyle](#code-style) task succeeds

After opening a pull request, wait for a project committer to review your PR. Address any review comments and click
"Resolve" on a conversation thread after making the necessary changes. You might go through multiple rounds of reviews 
or receive comments to make minor formatting changes. The thorough review process is to ensure the code in the project
is consistent and maintainable. Do not take the presence of many review comments to mean that your code is bad in any
way.
