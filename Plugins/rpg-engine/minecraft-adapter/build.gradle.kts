dependencies {
    implementation(project(":common"))
    implementation(project(":character-module"))
    implementation(project(":combat-module"))
    implementation(project(":skill-module"))
    compileOnly("io.papermc.paper:paper-api:${providers.gradleProperty("paper_api_version").get()}")
}
