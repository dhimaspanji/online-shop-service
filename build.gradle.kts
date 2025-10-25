plugins {
	java
	id("org.springframework.boot") version "3.5.7"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "co.id.project.dhimas"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.apache.commons:commons-lang3")

    runtimeOnly("com.h2database:h2")

    compileOnly("org.projectlombok:lombok")
    compileOnly("io.soabase.record-builder:record-builder-core:49")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("io.soabase.record-builder:record-builder-processor:49")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testCompileOnly("org.projectlombok:lombok")
    testCompileOnly("io.soabase.record-builder:record-builder-core:49")
    testAnnotationProcessor("org.projectlombok:lombok")
    testAnnotationProcessor("io.soabase.record-builder:record-builder-processor:49")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
