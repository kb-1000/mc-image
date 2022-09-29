/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":mc-image-lib", configuration = "graal"))
}

sourceSets {
    create("optimize") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}


configurations {
    getByName("optimizeCompileClasspath") {
        extendsFrom(compileClasspath.get())
    }

    getByName("optimizeRuntimeClasspath") {
        extendsFrom(runtimeClasspath.get())
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(listOf("--add-modules", "jdk.internal.vm.ci", "--add-exports", "java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED", "--add-exports", "java.base/sun.reflect.generics.reflectiveObjects=ALL-UNNAMED", "--add-exports", "java.base/jdk.internal.org.objectweb.asm.tree=ALL-UNNAMED", "--add-exports", "jdk.internal.vm.ci/jdk.vm.ci.meta=ALL-UNNAMED", "--add-exports", "java.base/jdk.internal.misc=ALL-UNNAMED", "--add-exports", "java.base/jdk.internal.org.objectweb.asm.util=ALL-UNNAMED"))
}

tasks.jar {
    manifest {
        from("src/main/resources/META-INF/MANIFEST.MF")
    }
}

val optimizeJar: Jar by tasks.creating(Jar::class) {
    manifest {
        from("src/optimize/resources/META-INF/MANIFEST.MF")
    }
    archiveClassifier.set("optimize")
    from(sourceSets.main.get().output, sourceSets["optimize"].output)
}
tasks.assemble {
    dependsOn(optimizeJar)
}