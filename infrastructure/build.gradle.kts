plugins {
    kotlin("plugin.allopen")
    kotlin("plugin.jpa")
    id("io.spring.dependency-management")
    id("org.springframework.boot")
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
    annotation("org.springframework.stereotype.Repository")
    annotation("org.springframework.stereotype.Service")
    annotation("org.springframework.stereotype.Component")
}

dependencies {
    implementation(project(":domain"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")

    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    implementation("org.jetbrains.kotlin:kotlin-reflect")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
