plugins {
    id("net.fabricmc.fabric-loom")
}

val modId = property("mod.id").toString()
val modName = property("mod.name").toString()
val modVersion = property("mod.version").toString()
val modGroup = property("mod.group").toString()
val modDescription = property("mod.description").toString()
val modSource = property("mod.source").toString()
val modIssues = property("mod.issues").toString()
val modLicense = property("mod.license").toString()
val mcVersion = property("mod.mc_version").toString()
val mcDep = property("mod.mc_dep").toString()
val loaderVersion = property("deps.fabric_loader_version").toString()
val apiVersion = property("deps.fabric_api_version").toString()

version = "$modVersion+$mcVersion-fabric"
group = modGroup
base { archivesName.set("$modId-fabric") }

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
}

dependencies {
    minecraft("com.mojang:minecraft:$mcVersion")
    implementation("net.fabricmc:fabric-loader:$loaderVersion")
    implementation("net.fabricmc.fabric-api:fabric-api:$apiVersion+$mcVersion")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks.processResources {
    val props = mapOf(
        "id" to modId,
        "name" to modName,
        "version" to modVersion,
        "description" to modDescription,
        "source" to modSource,
        "issues" to modIssues,
        "license" to modLicense,
        "mcdep" to mcDep,
        "fabric_loader_version" to loaderVersion,
        "java_version" to "25"
    )
    props.forEach(inputs::property)
    filesMatching("fabric.mod.json") { expand(props) }
    exclude("META-INF/neoforge.mods.toml")
}
