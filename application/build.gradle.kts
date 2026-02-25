plugins {
    kotlin("plugin.allopen")
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
    annotation("org.springframework.stereotype.Service")
    annotation("org.springframework.stereotype.Component")
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":infrastructure"))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-security")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
