/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest
import java.util.*

plugins {
    java
}

val gson = Gson()

val minecraftVersion = "1.17.1"

repositories {
    ivy(url = "https://piston-meta.mojang.com/mc/game") {
        metadataSources {
            artifact()
        }
        content {
            includeModule("com.mojang.minecraft", "version_manifest_v2")
        }
        patternLayout {
            artifact("[artifact].[ext]")
        }
    }
}

val manifestFile =
    configurations.detachedConfiguration(dependencies.create("com.mojang.minecraft:version_manifest_v2@json").apply {
        (this as ExternalModuleDependency).isChanging = true
    }).files.single()
lateinit var versionUrl: URI
for (version in gson.fromJson(
    manifestFile.readText(StandardCharsets.UTF_8),
    JsonObject::class.java
)["versions"].asJsonArray) {
    val versionObj = version.asJsonObject
    if (versionObj["id"].asString == minecraftVersion) {
        versionUrl = URI(versionObj["url"].asString)
    }
}

fun fileRepositoryUrl(url: URI, filename: String): URI {
    val repositoryUrl: URI = url.resolve(".")
    check(repositoryUrl.relativize(url).toString() == filename)
    return repositoryUrl
}

val libRepositoryUrl = "https://libraries.minecraft.net/"

repositories {
    ivy(url = fileRepositoryUrl(versionUrl, "$minecraftVersion.json")) {
        metadataSources {
            artifact()
        }
        content {
            includeVersion("com.mojang", "minecraft", minecraftVersion)
        }
        patternLayout {
            artifact("[revision].[ext]")
        }
    }
    maven(url = "https://maven.quiltmc.org/repository/release") {
        name = "QuiltMC"
    }
    maven(url = "https://maven.fabricmc.net") {
        name = "FabricMC"
    }
    mavenCentral()
    maven(url = libRepositoryUrl) {
        name = "Minecraft"
        metadataSources {
            artifact()
        }
    }
}

var versionFile: File =
    configurations.detachedConfiguration(dependencies.create("com.mojang:minecraft:$minecraftVersion:version@json")).files.single()

val versionJson: JsonObject = gson.fromJson(versionFile.readText(StandardCharsets.UTF_8), JsonObject::class.java)
repositories {
    for (classifier in listOf("client", "server"))
        ivy(
            url = fileRepositoryUrl(
                URI(versionJson["downloads"].asJsonObject[classifier].asJsonObject["url"].asString),
                "$classifier.jar"
            )
        ) {
            metadataSources {
                artifact()
            }
            content {
                includeVersion("com.mojang.minecraft", classifier, minecraftVersion)
            }
            patternLayout {
                artifact("[artifact].[ext]")
            }
        }
}

sourceSets {
    create("optimize") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

val minecraftLibrariesSlf4j: Configuration by configurations.creating
val minecraftLibraries: Configuration by configurations.creating {
    extendsFrom(minecraftLibrariesSlf4j)
    isCanBeConsumed = true
    isCanBeResolved = true
}
val minecraftNatives: Configuration by configurations.creating
val hashedMojmap: Configuration by configurations.creating
val graal: Configuration by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = true
}
val minecraftClient: Configuration by configurations.creating {
    extendsFrom(minecraftLibraries, hashedMojmap)
}
val minecraftServer: Configuration by configurations.creating {
    extendsFrom(hashedMojmap)
}
val substgen: Configuration by configurations.creating


configurations {
    implementation {
        extendsFrom(minecraftClient, graal)
    }
    getByName("optimizeCompileClasspath") {
        extendsFrom(compileClasspath.get())
    }

    getByName("optimizeRuntimeClasspath") {
        extendsFrom(runtimeClasspath.get())
    }
}

data class Library(
    val downloads: Downloads,
    val name: String,
    val natives: Map<String, String>?,
    val rules: List<Rule>?
) {
    data class Rule(val action: String, val os: OsRule?) {
        data class OsRule(val name: String?, val arch: String?, val version: String?)
    }

    data class Downloads(val artifact: Download, val classifiers: Map<String, Download>?) {
        data class Download(val url: String)
    }
}

val libs: List<Library> =
    gson.fromJson(versionJson.getAsJsonArray("libraries"), object : TypeToken<List<Library>>() {}.type)
val slf4jPattern = Regex("^org.slf4j:slf4j-api:(.*)\$")
lateinit var slf4jVersion: String
val operatingSystem: OperatingSystem =
    org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentOperatingSystem()
val versionNativesOsName = when {
    operatingSystem.isLinux -> "linux"
    operatingSystem.isMacOsX -> "macos"
    operatingSystem.isWindows -> "windows"
    else -> TODO()
}
val versionRulesOsName = when {
    operatingSystem.isLinux -> "linux"
    operatingSystem.isMacOsX -> "osx"
    operatingSystem.isWindows -> "windows"
    else -> TODO()
}
if (!operatingSystem.isLinux) TODO("Only Linux is currently supported!")
for (lib in libs) {
    slf4jVersion = (slf4jPattern.matchEntire(lib.name) ?: continue).groupValues[1]
}

val substgenOutput = buildDir.resolve("generated/sources/substgen/java/main")

val substgenTask = tasks.create<JavaExec>("substgen") {
    val bytecodeOutput = rootProject.file("java.base")
    inputs.files(substgen)
    outputs.dirs(substgenOutput, bytecodeOutput)
    classpath(substgen)
    mainClass.set("de.kb1000.mcimage.substgen.Main")
    args(substgenOutput)
}

sourceSets {
    main {
        java {
            srcDir(substgenOutput)
        }
    }
}

tasks.compileJava {
    dependsOn(substgenTask)
}

tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(
        listOf(
            "--add-modules",
            "jdk.internal.vm.ci",
            "--add-exports",
            "jdk.internal.vm.compiler/org.graalvm.compiler.serviceprovider=ALL-UNNAMED",
            "--add-exports",
            "jdk.internal.vm.compiler/org.graalvm.compiler.nodes.graphbuilderconf=ALL-UNNAMED",
            "--add-exports",
            "jdk.internal.vm.compiler/org.graalvm.compiler.node=ALL-UNNAMED",
            "--add-exports",
            "jdk.internal.vm.ci/jdk.vm.ci.meta=ALL-UNNAMED",
            "--add-exports",
            "jdk.internal.vm.compiler/org.graalvm.compiler.nodes.java=ALL-UNNAMED",
            "--add-exports",
            "jdk.internal.vm.compiler/org.graalvm.compiler.nodes=ALL-UNNAMED",
            "--add-exports",
            "jdk.internal.vm.compiler/org.graalvm.compiler.api.replacements=ALL-UNNAMED",
            "--add-exports",
            "jdk.internal.vm.compiler/org.graalvm.compiler.phases.util=ALL-UNNAMED",
            "--add-exports",
            "java.base/sun.reflect.generics.reflectiveObjects=ALL-UNNAMED",
            "--add-exports",
            "java.base/sun.reflect.generics.repository=ALL-UNNAMED",
            "--add-exports",
            "java.base/sun.reflect.generics.factory=ALL-UNNAMED",
            "--add-exports",
            "java.base/sun.reflect.generics.tree=ALL-UNNAMED",
            "--add-exports",
            "java.base/jdk.internal.misc=ALL-UNNAMED",
            "--add-exports",
            "java.base/com.sun.crypto.provider=ALL-UNNAMED",
            "--add-exports",
            "java.base/jdk.internal.vm.annotation=ALL-UNNAMED",
            "--add-exports",
            "jdk.internal.vm.ci/jdk.vm.ci.runtime=ALL-UNNAMED",
            "--add-exports",
            "org.graalvm.sdk/org.graalvm.nativeimage.impl=ALL-UNNAMED",
            "--add-exports",
            "java.base/jdk.internal.access=ALL-UNNAMED",
            "--add-exports",
            "jdk.internal.vm.compiler/org.graalvm.compiler.core.common.type=ALL-UNNAMED",
            "--add-exports",
            "jdk.internal.vm.compiler/org.graalvm.compiler.phases.tiers=ALL-UNNAMED"
        )
    )
}

dependencies {
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("org.jetbrains:annotations:23.0.0")
    for (lib in libs) {
        if (lib.rules != null) {
            var allow = false
            for (rule in lib.rules) {
                if (rule.os != null) {
                    if (rule.os.arch != null || rule.os.version != null) TODO()
                    if (rule.os.name != versionRulesOsName) {
                        continue
                    }
                }
                allow = when (rule.action) {
                    "allow" -> true
                    "disallow" -> false
                    else -> TODO()
                }
            }
            if (!allow) continue
        }
        check(lib.downloads.artifact.url.startsWith(libRepositoryUrl))
        if (!lib.name.startsWith("commons-logging:commons-logging:"))
            minecraftLibraries(lib.name) {
                isTransitive = false
            }
        else
            minecraftLibrariesSlf4j("org.slf4j:jcl-over-slf4j:$slf4jVersion") {
                isTransitive = false
            }
        val classifier = lib.natives?.get(versionNativesOsName) ?: continue
        val native = lib.downloads.classifiers?.get(classifier)!!
        check(native.url.startsWith(libRepositoryUrl))
        minecraftNatives("${lib.name}:$classifier") {
            isTransitive = false
        }
    }
    minecraftClient("com.mojang.minecraft:client:$minecraftVersion")
    minecraftServer("com.mojang.minecraft:server:$minecraftVersion")
    hashedMojmap("org.quiltmc:hashed:$minecraftVersion")
    hashedMojmap("net.fabricmc:tiny-mappings-parser:0.3.0+build.17")
    graal("org.graalvm.nativeimage:svm:22.2.0")
    substgen(project(":mc-image-substgen"))
}

val extractNatives by tasks.creating(Copy::class) {
    destinationDir = rootProject.file("natives")
    from(minecraftNatives.map(::zipTree)) {
        exclude("META-INF/", "module-info.class")
    }
}

val optimizeJar by tasks.creating(Jar::class) {
    archiveClassifier.set("optimize")
    from(sourceSets["optimize"].output)
}

val messageDigest: MessageDigest = MessageDigest.getInstance("SHA-1")

fun hashBytes(bytes: ByteArray): String =
    HexFormat.of().formatHex((messageDigest.clone() as MessageDigest).digest(bytes))

fun hashFile(file: Path): String = hashBytes(Files.readAllBytes(file))

data class AssetIndex(val objects: Map<String, AssetObject>) {
    data class AssetObject(val hash: String, val size: Long)
}

val httpClient = HttpClient.newHttpClient()

val downloadAssets: Task by tasks.creating {
    inputs.file(versionFile)
    val assetRoot = rootProject.file("assets").toPath()
    outputs.dir(assetRoot)
    doFirst {
        // TODO: possibly parallelize this?
        fun dlFile(sha1: String, size: Long, uri: URI, target: Path): Path {
            if (Files.exists(target)) {
                if (Files.size(target) == size && hashFile(target) == sha1) {
                    return target
                }
            }
            check(!gradle.startParameter.isOffline) { "$uri not cached, but offline mode enabled!" }
            Files.createDirectories(target.parent)
            httpClient.send(HttpRequest.newBuilder(uri).build(), BodyHandlers.ofFile(target))
            check(Files.size(target) == size && hashFile(target) == sha1) { "File downloaded from $uri broken!" }
            return target
        }

        val assetIndex = gson.fromJson(
            Files.readString(
                dlFile(
                    versionJson["assetIndex"].asJsonObject["sha1"].asString,
                    versionJson["assetIndex"].asJsonObject["size"].asLong,
                    URI(versionJson["assetIndex"].asJsonObject["url"].asString),
                    assetRoot.resolve("indexes")
                        .resolve("${versionJson["assetIndex"].asJsonObject["id"].asString}.json")
                )
            ), AssetIndex::class.java
        )
        for (assetObject in assetIndex.objects.values) {
            val hashPath = "${assetObject.hash.substring(0 until 2)}/${assetObject.hash}"
            dlFile(
                assetObject.hash,
                assetObject.size,
                URI("https://resources.download.minecraft.net/$hashPath"),
                assetRoot.resolve("objects").resolve(hashPath)
            )
        }
    }
}

val downloadDyncall: Task by tasks.creating {
    val libList = rootProject.file("libs.txt").readLines()
    inputs.property("libs", libList)
    libList.forEach {
        val file = rootProject.file(it.split('/').last())
        outputs.file(file)
        doLast {
            httpClient.send(HttpRequest.newBuilder(URI(it)).build(), BodyHandlers.ofFile(file.toPath()))
        }
    }
}

val compileStb by tasks.creating(Exec::class) {
    inputs.files(rootProject.file("stb.c"), rootProject.file("stb.h"))
    inputs.dir(rootProject.file("stb"))
    outputs.file(rootProject.file("stb.o"))
    workingDir = rootProject.projectDir
    commandLine(
        "clang",
        "-Istb",
        "-flto=full",
        "-c",
        "-o",
        "stb.o",
        "-O2",
        "-DNDEBUG",
        "-march=native",
        "-mtune=native",
        "stb.c"
    )
}

val stbAr by tasks.creating(Exec::class) {
    inputs.files(compileStb)
    val libFile = rootProject.file("libstb.a")
    outputs.file(libFile)
    doFirst {
        Files.deleteIfExists(libFile.toPath())
    }
    workingDir = rootProject.projectDir
    commandLine("llvm-ar", "rcs", "libstb.a", "stb.o")
}

val printClasspaths: Task by tasks.creating {
    val classpathClient = sourceSets.main.get().output + minecraftLibraries + minecraftClient
    val classpathServer = sourceSets.main.get().output + minecraftLibrariesSlf4j + minecraftServer
    val classpathOptimize = sourceSets["optimize"].output
    inputs.property("classpathClient", classpathClient)
    inputs.property("classpathServer", classpathServer)
    inputs.property("classpathOptimize", classpathOptimize)
    outputs.file("classpaths.sh")
    doFirst {
        file("classpaths.sh").writeText(
            """
            export MCIMAGE_CLASSPATH_CLIENT="${classpathClient.joinToString(":")}"
            export MCIMAGE_CLASSPATH_SERVER="${classpathServer.joinToString(":")}"
            export MCIMAGE_CLASSPATH_OPTIMIZE="${classpathOptimize.joinToString(":")}"
            
        """.trimIndent()
        )
    }
}

tasks.assemble {
    dependsOn(optimizeJar, extractNatives, downloadAssets, downloadDyncall, stbAr, printClasspaths)
}
