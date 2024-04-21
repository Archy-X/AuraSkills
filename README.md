<h1 style="text-align:center;">AuraSkills</h1>

<p style="text-align:center;">
The ultra-versatile Minecraft RPG skills plugin
</p>

<p style="text-align: center;font-weight: bold;">
  <a href="https://aurelium.dev/auraskills/download">Downloads</a>
  &nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;
  <a href="https://wiki.aurelium.dev/auraskills">Wiki</a>
  &nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;
  <a href="https://discord.gg/Bh2EZfB">Discord</a>
</p>

## About

**AuraSkills** (formerly **Aurelium Skills**) is a Minecraft plugin that adds skills, stats, abilities, and other RPG-related features. The plugin is fully configurable and customizable, enabling use on a wide-range of server types from small SMPs to large, custom MMORPG networks.

## API

AuraSkills has an extensive developer API.

Read the full API documentation on the [wiki](https://wiki.aurelium.dev/auraskills/api), or view the [Javadocs](https://docs.aurelium.dev/auraskills-api-bukkit/).

Release versions are published to the Maven central repository.

### Maven

```xml
<dependency>
    <groupId>dev.aurelium</groupId>
    <artifactId>auraskills-api-bukkit</artifactId>
    <version>2.0.7</version>
</dependency>
```
### Gradle

**Groovy DSL:**
```gradle
repositories {
    mavenCentral()
}

dependencies {
    compileOnly 'dev.aurelium:auraskills-api-bukkit:2.0.7'
}
```
**Kotlin DSL:**
```kotlin
repositories { 
    mavenCentral()
}

dependencies { 
    compileOnly("dev.aurelium:auraskills-api-bukkit:2.0.7")
}
```

## Contributing
Contributions are welcome, just open a pull request.