plugins {
    id("java")
}

allprojects.forEach {
    it.group = "com.github.ynverxe.ideal-gui"
    it.version = "0.0.1-beta"
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.jetbrains:annotations:24.0.0")
}

tasks.test {
    useJUnitPlatform()
}