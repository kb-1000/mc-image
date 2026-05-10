/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

java {
    registerFeature("optimize") {
        usingSourceSet(sourceSets.create("optimize"))
    }
}

dependencies {
    api(libs.graal)
    "optimizeImplementation"(project(":mc-image-agent"))
}

tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(listOf("--add-modules", "jdk.internal.vm.ci", "--add-exports", "java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED", "--add-exports", "java.base/sun.reflect.generics.reflectiveObjects=ALL-UNNAMED", "--add-exports", "java.base/jdk.internal.org.objectweb.asm.tree=ALL-UNNAMED", "--add-exports", "jdk.internal.vm.ci/jdk.vm.ci.meta=ALL-UNNAMED", "--add-exports", "java.base/jdk.internal.misc=ALL-UNNAMED", "--add-exports", "java.base/jdk.internal.org.objectweb.asm.util=ALL-UNNAMED"))
}

tasks.jar {
    manifest {
        from("src/main/resources/META-INF/MANIFEST.MF")
    }
}

val optimizeJar by tasks.existing(Jar::class) {
    manifest {
        from("src/optimize/resources/META-INF/MANIFEST.MF")
    }
    from(sourceSets.main.get().output)
}

tasks.assemble {
    dependsOn(optimizeJar)
}