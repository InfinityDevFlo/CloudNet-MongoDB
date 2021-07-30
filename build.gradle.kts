import Properties.group

buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    dependencies {
        classpath("com.github.jengelman.gradle.plugins:shadow:6.1.0")
    }
}

//Define Plugins
plugins {
    id("java")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "6.1.0"
    kotlin("jvm") version "1.5.10"
}


//Define Repositorys
repositories {
    for (field in Repositories::class.java.declaredFields) {
        if (field.name != "INSTANCE") {
            println("Added Repository: " + field.get(null))
            maven(field.get(null))
        }
    }
}

//Define Version and Group
group = Properties.group
version = Properties.version


//Define Dependencies for all Modules
dependencies {
    implementation(getDependency("kotlin", "stdlib"))
    implementation(getDependency("kotlinx", "coroutines-core"))
    compileOnly(getDependency("cloudnet", "cloudnet"))
    implementation(getDependency("database", "mongo"))
}

if (System.getProperty("publishName") != null && System.getProperty("publishPassword") != null) {
    publishing {
        (components["java"] as AdhocComponentWithVariants).withVariantsFromConfiguration(configurations["shadowRuntimeElements"]) {
            skip()
        }
        publications {
            create<MavenPublication>(project.name) {
               groupId = Properties.group
                artifactId = project.name
               version = Properties.version
                from(components.findByName("java"))
                pom {
                    name.set(project.name)
                    url.set("https://github.com/VironLab/Vextension")
                    properties.put("inceptionYear", "2021")
                    licenses {
                        license {
                            name.set("General Public License (GPL v3.0)")
                            url.set("https://www.gnu.org/licenses/gpl-3.0.txt")
                            distribution.set("repo")
                        }
                    }
                    developers {
                        developer {
                            id.set("Infinity_dev")
                            name.set("Florin Dornig")
                            email.set("infinitydev@vironlab.eu")
                        }
                    }
                }
            }
            repositories {
                maven("https://repo.vironlab.eu/repository/maven-snapshot/") {
                    this.name = "vironlab-snapshot"
                    credentials {
                        this.password = System.getProperty("publishPassword")
                        this.username = System.getProperty("publishName")
                    }
                }
            }
        }
    }
}
tasks {

    test {
        useJUnitPlatform()
    }

    //Set the Name of the Sources Jar
    kotlinSourcesJar {
        archiveFileName.set("${project.name}-${Properties.version}-${getCommitHash()}-sources.jar")
        doFirst {
            //Set Manifest
            manifest {
                attributes["Implementation-Title"] = project.name
                attributes["Implementation-Version"] = findProperty("version").toString()
                attributes["Specification-Version"] = findProperty("version").toString()
                attributes["Implementation-Vendor"] = "VironLab.eu"
                attributes["Built-By"] = System.getProperty("user.name")
                attributes["Build-Jdk"] = System.getProperty("java.version")
                attributes["Created-By"] = "Gradle ${gradle.gradleVersion}"
                attributes["VironLab-AppId"] = "cloudnet_mongodb"
                attributes["Commit-Hash"] = getCommitHash()
            }
        }
    }

    shadowJar {
        //Set the Name of the Output File
        archiveFileName.set("${project.name}-${Properties.version}-${getCommitHash()}-full.jar")

        exclude("META-INF/**")

        doFirst {
            //Set Manifest
            manifest {
                attributes["Implementation-Title"] = project.name
                attributes["Implementation-Version"] = findProperty("version").toString()
                attributes["Specification-Version"] = findProperty("version").toString()
                attributes["Implementation-Vendor"] = "VironLab.eu"
                attributes["Built-By"] = System.getProperty("user.name")
                attributes["Build-Jdk"] = System.getProperty("java.version")
                attributes["Created-By"] = "Gradle ${gradle.gradleVersion}"
                attributes["VironLab-AppId"] = "cloudnet_mongodb"
                attributes["Commit-Hash"] = getCommitHash()
            }
        }
    }

    jar {
        archiveFileName.set("${project.name}-${Properties.version}-${getCommitHash()}.jar")
        doFirst {
            //Set Manifest
            manifest {
                attributes["Implementation-Title"] = project.name
                attributes["Implementation-Version"] = findProperty("version").toString()
                attributes["Specification-Version"] = findProperty("version").toString()
                attributes["Implementation-Vendor"] = "VironLab.eu"
                attributes["Built-By"] = System.getProperty("user.name")
                attributes["Build-Jdk"] = System.getProperty("java.version")
                attributes["Created-By"] = "Gradle ${gradle.gradleVersion}"
                attributes["VironLab-AppId"] = "cloudnet_mongodb"
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