dependencies {
    implementation(project(":common"))
    implementation(project(":character-module"))
    implementation(project(":combat-module"))
    implementation(project(":skill-module"))
    implementation(project(":item-module"))
    implementation(project(":economy-module"))
    implementation(project(":infrastructure-module"))
    implementation(project(":minecraft-adapter"))
    compileOnly("io.papermc.paper:paper-api:${providers.gradleProperty("paper_api_version").get()}")
}

tasks.jar {
    archiveBaseName.set("rpg-engine-plugin")
    dependsOn(configurations.runtimeClasspath)
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    filesMatching("plugin.yml") {
        expand("version" to project.version)
    }
}
