import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val jacksonVersion = "2.16.1"
val logbackEncoderVersion = "7.4"
val azureAdClient = "0.0.7"
val wiremockVersion = "3.0.1"
val mockitoKotlinVersion = "5.2.1"
val navTokenSupportVersion = "4.1.0"
val hibernateValidatorVersion = "8.0.1.Final"
val testcontainersVersion = "1.19.3"
val postgresqlVersion = "42.7.2"
val flywayCoreVersion = "10.8.1"

plugins {
    val kotlinVersion = "1.9.22"
    id("org.jetbrains.kotlin.jvm") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
    id("org.springframework.boot") version "3.2.1"
}

apply(plugin = "io.spring.dependency-management")

group = "no.nav.pensjon.opptjening"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://maven.pkg.github.com/navikt/maven-release") {
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("no.nav.pensjonopptjening:pensjon-opptjening-azure-ad-client:$azureAdClient")
    implementation("no.nav.security:token-validation-spring:$navTokenSupportVersion")
    implementation("org.hibernate.validator:hibernate-validator:$hibernateValidatorVersion")
    implementation("org.postgresql:postgresql:$postgresqlVersion")
    implementation("org.flywaydb:flyway-core:$flywayCoreVersion")
    implementation("org.flywaydb:flyway-database-postgresql:$flywayCoreVersion")

    // Log and metric
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("net.logstash.logback:logstash-logback-encoder:$logbackEncoderVersion")
    implementation("ch.qos.logback:logback-access:1.4.14")

    testImplementation(kotlin("test"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:postgresql:$testcontainersVersion")
    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")
    testImplementation("com.github.tomakehurst:wiremock-jre8-standalone:$wiremockVersion")
    testImplementation("no.nav.security:token-validation-spring-test:$navTokenSupportVersion")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events(
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
        )
    }
}
