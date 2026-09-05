plugins { java }

repositories {
    maven("https://repo.opencollab.dev/main/") { name = "opencollab" }
    mavenCentral()
}

dependencies {
    implementation("org.geysermc.mcprotocollib:protocol:${providers.gradleProperty("mc_protocol_lib_version").get()}")
    implementation("net.kyori:adventure-text-serializer-plain:4.26.1")
}

java { toolchain.languageVersion.set(JavaLanguageVersion.of(21)) }

tasks.register<JavaExec>("run") {
    group = "verification"
    description = "Connect an offline headless player and run the configured command."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("dev.minecraft.headless.HeadlessClient")
}
