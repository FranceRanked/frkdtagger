plugins {
    id("dev.architectury.loom")
    id("me.modmuss50.mod-publish-plugin")
}

class ModData {
    val id = property("mod.id").toString()
    val name = property("mod.name").toString()
    val version = property("mod.version").toString()
    val group = property("mod.group").toString()
    val description = property("mod.description").toString()
    val source = property("mod.source").toString()
    val issues = property("mod.issues").toString()
    val license = property("mod.license").toString()
}

class Dependencies {
    val fabricLoader = property("deps.fabric_loader_version").toString()
    val fabricApi = property("deps.fabric_api_version").toString()
    val neoforge = property("deps.neoforge_version").toString()
}

class LoaderData {
    val name = loom.platform.get().name.lowercase()
    val isFabric = name == "fabric"
    val isNeoforge = name == "neoforge"
}

class McData {
    val version = property("mod.mc_version").toString()
    val dep = property("mod.mc_dep").toString()
}

val mc = McData()
val mod = ModData()
val deps = Dependencies()
val loader = LoaderData()

val javaVersion = if (stonecutter.eval(mc.version, ">=26")) 25 else 21

val unobfuscated = stonecutter.eval(mc.version, ">=26")

version = "${mod.version}+${mc.version}-${loader.name}"
group = mod.group
base { archivesName.set("${mod.id}-${loader.name}") }

loom {
    silentMojangMappingsLicense()
    runConfigs.all {
        ideConfigGenerated(stonecutter.current.isActive)
        runDir = "../../run"
    }
    runConfigs.remove(runConfigs["server"])
}

repositories {
    mavenCentral()
    maven("https://maven.parchmentmc.org")
    maven("https://maven.neoforged.net/releases")
}

dependencies {
    minecraft("com.mojang:minecraft:${mc.version}")

    if (!unobfuscated) {
        @Suppress("UnstableApiUsage")
        mappings(loom.layered {
            officialMojangMappings()
            optionalProp("deps.parchment_version") {
                parchment("org.parchmentmc.data:parchment-${mc.version}:$it@zip")
            }
        })
    }

    if (loader.isFabric) {
        modImplementation("net.fabricmc:fabric-loader:${deps.fabricLoader}")
        modImplementation("net.fabricmc.fabric-api:fabric-api:${deps.fabricApi}+${mc.version}")
    } else if (loader.isNeoforge) {
        "neoForge"("net.neoforged:neoforge:${deps.neoforge}")
    }
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
}

if (unobfuscated) {
    sourceSets.main {
        java.exclude("**/config/gui/**", "**/render/**", "**/input/**", "**/FrkdClientSetup.java")
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(javaVersion)
}

tasks.processResources {
    val props = mapOf(
        "id" to mod.id,
        "name" to mod.name,
        "version" to mod.version,
        "description" to mod.description,
        "source" to mod.source,
        "issues" to mod.issues,
        "license" to mod.license,
        "mcdep" to mc.dep,
        "fabric_loader_version" to deps.fabricLoader,
        "neoforge_version" to deps.neoforge,
        "java_version" to javaVersion.toString(),
    )
    props.forEach(inputs::property)

    if (loader.isFabric) {
        filesMatching("fabric.mod.json") { expand(props) }
        exclude("META-INF/neoforge.mods.toml")
    }
    if (loader.isNeoforge) {
        filesMatching("META-INF/neoforge.mods.toml") { expand(props) }
        exclude("fabric.mod.json")
    }
}

publishMods {
    file = tasks.remapJar.get().archiveFile
    displayName = "${mod.name} ${mod.version} (${mc.version}-${loader.name})"
    version = mod.version
    type = STABLE
    modLoaders.add(loader.name)

    val modrinthId = findProperty("publish.modrinth")?.toString()?.takeIf { it.isNotBlank() }
    dryRun = modrinthId == null

    if (modrinthId != null) {
        modrinth {
            projectId = modrinthId
            accessToken = findProperty("modrinth.token")?.toString() ?: ""
            minecraftVersions.add(mc.version)
            if (loader.isFabric) requires("fabric-api")
        }
    }
}

if (stonecutter.current.isActive) {
    rootProject.tasks.register("buildActive") {
        group = "project"
        dependsOn(tasks.named("build"))
    }
}

fun <T> optionalProp(property: String, block: (String) -> T?): T? =
    findProperty(property)?.toString()
        ?.takeUnless { it.isBlank() || it == "[VERSIONED]" }
        ?.let(block)
