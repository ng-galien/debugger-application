plugins {
    kotlin("jvm") version "1.7.10"
    antlr
    idea
}

group = "net.tools"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    antlr("org.antlr:antlr4:4.10.1")
    implementation("org.antlr:antlr4-runtime:4.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.register<Copy>("copyAntlrJavaFiles") {
    from("src/main/antlr/java")
    into("build/generated-src/antlr/main/net/tools/language/postgresql")
    include("**/*.java")
}

tasks.generateGrammarSource {
    dependsOn("copyAntlrJavaFiles")
    outputDirectory = File("build/generated-src/antlr/main/net/tools/language/postgresql")
    maxHeapSize = "64m"
    arguments = arguments + listOf("-package", "net.tools.language.postgresql", "-visitor", "-long-messages")
}

tasks.compileKotlin {
    dependsOn(tasks.generateGrammarSource)
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}



