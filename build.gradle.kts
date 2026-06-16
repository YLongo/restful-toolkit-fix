import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType

plugins {
    java
    id("org.jetbrains.intellij.platform") version "2.6.0"
}

group = "me.jinghong.restful.toolkit"
version = "2.0.11"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaUltimate("2026.1")
        bundledPlugins("com.intellij.java")
    }
    implementation("com.fifesoft:rsyntaxtextarea:3.1.6")
}

intellijPlatform {
    buildSearchableOptions = false
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "261"
            untilBuild = provider { null }
        }
    }
    pluginVerification {
        ides {
            ide(IntelliJPlatformType.IntellijIdeaUltimate, "2026.1.3")
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
