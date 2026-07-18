rootProject.name = "AuraSkills"

include("api")
include("bukkit")
include("common")
include("api-bukkit")
include("paper")

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("1.0.0")
}
