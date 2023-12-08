plugins {
    id("java")
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("org.jetbrains:annotations:24.0.0")
    implementation("net.kyori:adventure-api:4.13.0")
}