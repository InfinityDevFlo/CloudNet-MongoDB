plugins {
    kotlin("jvm") version "1.5.10"
}

repositories {
    mavenCentral()
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "16"
    }

    withType<JavaCompile> {
        this.options.encoding = "UTF-8"
    }
}
