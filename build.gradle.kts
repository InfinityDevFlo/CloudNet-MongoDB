buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    dependencies {
        classpath("com.github.jengelman.gradle.plugins:shadow:6.1.0")
    }
}

fun getCommitHash(): String = try {
    val runtime = Runtime.getRuntime()
    val process = runtime.exec("git rev-parse --short HEAD")
    val out = process.inputStream
    out.bufferedReader().readText().trim()
} catch (ignored: Exception) {
    "unknown"
}

//Define Plugins
plugins {
    id("java")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "6.1.0"
    kotlin("jvm") version "1.6.10"
}


//Define Repositorys
repositories {
    mavenCentral()
    maven("https://repo.cloudnetservice.eu/repository/releases/")
}

//Define Version and Group
group = "me.infinity.cloudnetmongodb"
version = "2.1.0"


//Define Dependencies for all Modules
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("org.mongodb:mongodb-driver-sync:4.4.1")
    compileOnly("de.dytanic.cloudnet:cloudnet:3.4.2-RELEASE")
}

tasks {

    //Set the Name of the Sources Jar
    kotlinSourcesJar {
        archiveFileName.set("${project.name}-${project.version}-${getCommitHash()}-sources.jar")
        doFirst {
            //Set Manifest
            manifest {
                attributes["Implementation-Title"] = project.name
                attributes["Implementation-Version"] = findProperty("version").toString()
                attributes["Specification-Version"] = findProperty("version").toString()
                attributes["Implementation-Vendor"] = "Florin Dornig"
                attributes["Built-By"] = System.getProperty("user.name")
                attributes["Build-Jdk"] = System.getProperty("java.version")
                attributes["Created-By"] = "Gradle ${gradle.gradleVersion}"
                attributes["Commit-Hash"] = getCommitHash()
            }
        }
    }

    shadowJar {
        //Set the Name of the Output File
        archiveFileName.set("${project.name}-${project.version}-${getCommitHash()}-full.jar")

        exclude("META-INF/**")

        doFirst {
            //Set Manifest
            manifest {
                attributes["Implementation-Title"] = project.name
                attributes["Implementation-Version"] = findProperty("version").toString()
                attributes["Specification-Version"] = findProperty("version").toString()
                attributes["Implementation-Vendor"] = "Florin Dornig"
                attributes["Built-By"] = System.getProperty("user.name")
                attributes["Build-Jdk"] = System.getProperty("java.version")
                attributes["Created-By"] = "Gradle ${gradle.gradleVersion}"
                attributes["Commit-Hash"] = getCommitHash()
            }
        }
    }

    jar {
        archiveFileName.set("${project.name}-${project.version}-${getCommitHash()}.jar")
        doFirst {
            //Set Manifest
            manifest {
                attributes["Implementation-Title"] = project.name
                attributes["Implementation-Version"] = findProperty("version").toString()
                attributes["Specification-Version"] = findProperty("version").toString()
                attributes["Implementation-Vendor"] = "Florin Dornig"
                attributes["Built-By"] = System.getProperty("user.name")
                attributes["Build-Jdk"] = System.getProperty("java.version")
                attributes["Created-By"] = "Gradle ${gradle.gradleVersion}"
                attributes["Commit-Hash"] = getCommitHash()
            }
        }
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }

    withType<JavaCompile> {
        this.options.encoding = "UTF-8"
    }
}