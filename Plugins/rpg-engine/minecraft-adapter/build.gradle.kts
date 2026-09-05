dependencies {
    implementation(project(":common"))
    implementation(project(":character-module"))
    implementation(project(":combat-module"))
    compileOnly("io.papermc.paper:paper-api:${providers.gradleProperty("paper_api_version").get()}")
}

