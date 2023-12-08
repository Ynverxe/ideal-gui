plugins {
    `ideal-gui-base-conventions`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation("dev.hollowcube:minestom-ce:0c5a177281")
    implementation(project(":minestom-impl"))
    implementation(project(mapOf("path" to ":abstract")))
}