import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.6.2"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.6.10"
	kotlin("plugin.spring") version "1.6.10"
}

group = "com.gitlab.davinkevin.issues.jooq"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

extra["jooq.version"] = "3.15.5"
dependencies {
	implementation("org.springframework.boot:spring-boot-starter-jooq")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	runtimeOnly("io.r2dbc:r2dbc-postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")

	testImplementation("org.testcontainers:junit-jupiter:1.16.2")
	testImplementation("org.testcontainers:postgresql:1.16.2")
	testImplementation("org.testcontainers:r2dbc:1.16.2")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.test {
	useJUnitPlatform()
//	testLogging {
//		exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
//	}
}
