plugins {
    kotlin("plugin.allopen")
    id("io.spring.dependency-management")
    id("org.springframework.boot")
}

allOpen {
    annotation("org.springframework.web.bind.annotation.RestController")
    annotation("org.springframework.stereotype.Controller")
    annotation("org.springframework.stereotype.Component")
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":application"))
    implementation(project(":infrastructure"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")
    implementation("io.jsonwebtoken:jjwt-api:0.13.0")
    implementation("io.jsonwebtoken:jjwt-gson:0.13.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.security:spring-security-test")
    runtimeOnly("org.springframework.boot:spring-boot-docker-compose")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
