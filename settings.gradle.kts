pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.neoforged.net/releases")
        maven("https://maven.architectury.dev")
        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.kikugie.dev/snapshots")
        maven("https://maven.parchmentmc.org")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.kikugie.stonecutter") version "0.9.2"
}

stonecutter {
    create(rootProject) {
        fun mc(version: String, vararg loaders: String) =
            loaders.forEach { version("$version-$it", version).buildscript = script(it, version) }

        mc("1.21.11", "fabric")
        mc("26.1.2", "fabric")
        mc("26.2", "fabric")

        vcsVersion = "1.21.11-fabric"
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs")
    }
}

rootProject.name = "frrktagger"

fun script(loader: String, version: String): String =
    if (version.startsWith("1.")) "build.gradle.kts" else "build.$loader.gradle.kts"
