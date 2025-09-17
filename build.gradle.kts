plugins {
	java
	id("org.springframework.boot") version "3.5.5"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.jetbrains.kotlin.jvm") version "2.2.10"
	jacoco
	id("org.sonarqube") version "6.3.1.5724"
}

group = "com.josdem.vetlog"
version = "1.0.0"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}

kotlin {
	jvmToolchain(21)
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Core Spring Boot
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")

	//Swagger Dependency
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.13")
	// Kotlin support
	implementation("org.jetbrains.kotlin:kotlin-stdlib")

	// Database
	runtimeOnly("com.mysql:mysql-connector-j")
	testImplementation("com.h2database:h2")

	// Lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// Test dependencies
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test")
	testImplementation(kotlin("test"))
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// cache-memory
	implementation("com.hazelcast:hazelcast-spring")
}

tasks.withType<Test> {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}
tasks.test {
	ignoreFailures = true
}
// JaCoCo Configuration

jacoco {
	toolVersion = "0.8.11"
	reportsDirectory.set(layout.buildDirectory.dir("reports/jacoco"))
}

tasks.jacocoTestReport {
	dependsOn(tasks.test) // tests are required to run before generating the report
	reports {
		xml.required.set(true)
		csv.required.set(false)
		html.required.set(true)
		html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/html"))
	}

	classDirectories.setFrom(
		files(classDirectories.files.map {
			fileTree(it).apply {
				exclude(
					"**/configuration/**",
					"**/exception/**",
					"**/model/**",
					"**/dto/**",
					"**/VetlogApplication**"
				)
			}
		})
	)
}

// SonarQube Configuration
sonar {
	properties {
		property("sonar.projectKey", System.getenv("SONAR_PROJECT_KEY"))
		property("sonar.projectName", System.getenv("SONAR_PROJECT_NAME"))
		property("sonar.host.url", System.getenv("SONAR_URL"))
		property("sonar.token", System.getenv("SONAR_TOKEN"))

		property("sonar.sources", "src/main/java")
		property("sonar.tests", "src/test/java")
		property("sonar.language", "java")
		property("sonar.sourceEncoding", "UTF-8")

		// Test and coverage reports
		property("sonar.junit.reportPaths", "build/test-results/test")
	}
}