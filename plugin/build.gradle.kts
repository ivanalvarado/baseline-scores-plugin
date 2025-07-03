plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    `maven-publish`
}

group = "com.ivanalvarado"
version = "1.0.0"

dependencies {
    implementation(gradleApi())
    implementation(kotlin("stdlib"))
    implementation("org.dom4j:dom4j:2.1.4")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")

    testImplementation("junit:junit:4.13.2")
    testImplementation(kotlin("test"))
}

gradlePlugin {
    plugins {
        create("baselineScoresPlugin") {
            id = "com.ivanalvarado.baseline-scores"
            implementationClass = "com.ivanalvarado.baselinescoresplugin.BaselineScoresPlugin"
            displayName = "Baseline Scores Plugin"
            description = "A Gradle plugin for managing baseline scores"
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
