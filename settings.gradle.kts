rootProject.name = "AuraSkills"

include("api")
include("bukkit")
include("common")
include("api-bukkit")

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.8.0")
}
