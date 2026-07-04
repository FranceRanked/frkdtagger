plugins {
    id("net.neoforged.moddev")
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
val neoforgeVersion = property("deps.neoforge_version").toString()

version = "$modVersion+$mcVersion-neoforge"
group = modGroup
base { archivesName.set("$modId-neoforge") }

neoForge {
    version = neoforgeVersion
    runs {
        create("client") {
            client()
            gameDirectory = rootProject.file("run")
        }
    }
    mods {
        create(modId) {
            sourceSet(sourceSets["main"])
        }
    }
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

sourceSets.main {
    java.exclude("**/config/gui/**", "**/render/**", "**/input/**", "**/FrkdClientSetup.java")
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
        "neoforge_version" to neoforgeVersion,
        "java_version" to "25"
    )
    props.forEach(inputs::property)
    filesMatching("META-INF/neoforge.mods.toml") { expand(props) }
    exclude("fabric.mod.json")
}
