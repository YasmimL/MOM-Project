plugins {
    id("com.github.johnrengelman.shadow").version("8.1.1")
    id("java")
    application
}

application {
    mainClass.set("br.com.ifce.Main")
}

tasks.jar {
    manifest {
        attributes(mapOf("Main-Class" to application.mainClass))
    }
}

group = "br.com.ifce"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.activemq:activemq-all:5.16.3")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}