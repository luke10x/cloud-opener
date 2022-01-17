import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.6.3"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.6.10"
	kotlin("plugin.spring") version "1.6.10"

	kotlin("plugin.jpa") version "1.6.10"

	id("org.openapi.generator") version "5.3.1"
}

group = "dev.luke10x.cloudopener"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
//	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("junit:junit:4.13.1")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	// For OpenAPI generations:
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("io.springfox:springfox-swagger-ui:3.0.0")
	implementation("io.springfox:springfox-swagger2:3.0.0")
	implementation("org.openapitools:jackson-databind-nullable:0.2.2")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

// For OpenAPI generation
openApiGenerate {
	generatorName.set("spring")
	inputSpec.set("$rootDir/src/main/resources/cloudopener-openapi-schema.yaml")
	outputDir.set("$buildDir/generated/")

	configFile.set("$rootDir/src/main/resources/api-config.json")

	globalProperties.set(mapOf(
		Pair("apis", ""), //no value or comma-separated api names
		Pair("models", ""), //no value or comma-separated api names
		Pair("supportingFiles", "ApiUtil.java"),
	))
}

configure<SourceSetContainer> {
	named("main") {
		java.srcDir("$buildDir/generated/src/main/java")
	}
}

tasks.withType<KotlinCompile> {
	dependsOn("openApiGenerate")
	kotlinOptions.jvmTarget = "11"
}
