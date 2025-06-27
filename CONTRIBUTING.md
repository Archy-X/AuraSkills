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

AuraSkills contains a Gradle task to quickly build the plugin and start up a test server:

```
./gradlew clean :bukkit:runServer
```

Windows

```
.\gradlew.bat clean :bukkit:runServer
```

A Paper server will be automatically downloaded and run in the `bukkit/run` directory. The first time you start the
server, you may need to agree to the EULA in `eula.txt`. You can then join the server on the `localhost` IP address.

The Minecraft version of this test server can be viewed and changed in `bukkit/build.gradle.kts`
(find "minecraftVersion" in the file). You can
also [add additional plugins](https://github.com/jpenilla/run-task/wiki/Basic-Usage) to automatically download into the
test server. However, make sure you do not commit changes to the runServer task when submitting a PR.

In IntelliJ, you can find this task through the Gradle window under
`AuraSkills -> bukkit -> Tasks -> run paper -> runServer`.
Clicking this button will save this to a run configuration on the top right of the IDE, which can be run by selecting
the
`[runServer]` task in the drop-down and clicking the green run button.

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
