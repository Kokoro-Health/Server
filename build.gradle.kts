import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.1.0" apply false
    kotlin("plugin.allopen") version "2.1.0" apply false
    kotlin("plugin.jpa") version "2.1.0" apply false
    id("org.springframework.boot") version "3.4.2" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

group = "health.kokoro"
version = "1.0.0-SNAPSHOT"

extra["springBootVersion"] = "3.4.2"

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
            freeCompilerArgs.add("-Xjsr305=strict")
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    plugins.withType<org.springframework.boot.gradle.plugin.SpringBootPlugin> {
        the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
            imports {
                mavenBom("org.springframework.boot:spring-boot-dependencies:${rootProject.extra["springBootVersion"]}")
            }
        }
    }
}
