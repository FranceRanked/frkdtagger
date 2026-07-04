@file:OptIn(dev.kikugie.stonecutter.StonecutterExperimentalAPI::class)

plugins {
    id("dev.kikugie.stonecutter")
    id("dev.architectury.loom") version "1.17.487" apply false
    id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT" apply false
    id("net.neoforged.moddev") version "2.0.141" apply false
    id("me.modmuss50.mod-publish-plugin") version "0.8.4" apply false
}

stonecutter active file(".sc_active_version")

stonecutter parameters {
    constants.match(current.project.substringAfterLast('-'), "fabric", "neoforge")
    constants["v262"] = current.project.substringBeforeLast('-') == "26.2"
}

tasks.register("runActiveClient") {
    group = "project"
    dependsOn(stonecutter.current!!.project + ":runClient")
}
