/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.7.10"
}

repositories {
    mavenCentral()
    maven(url = "https://libraries.minecraft.net/") {
        name = "Minecraft"
        metadataSources {
            artifact()
        }
    }
}

dependencies {
    implementation(project(":mc-image-lib", configuration = "graal"))
    implementation(project(":mc-image-lib", configuration = "minecraftLibraries"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8") {
        exclude(module = "kotlin-stdlib-common")
    }
    implementation("org.jetbrains:annotations:23.0.0")
    implementation("com.squareup:javapoet:1.13.0")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.0")
    implementation("org.ow2.asm:asm-commons:9.3")
    runtimeOnly("org.glassfish.jaxb:jaxb-runtime:4.0.0")
    runtimeOnly("org.glassfish.jaxb:jaxb-xjc:4.0.0")
}
