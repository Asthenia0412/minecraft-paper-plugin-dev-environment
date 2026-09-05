dependencies {
    implementation(project(":common"))
    implementation(project(":identity-module"))
    implementation(project(":character-module"))
    implementation(project(":combat-module"))
    implementation(project(":skill-module"))
    implementation(project(":item-module"))
    implementation(project(":economy-module"))
    implementation(project(":infrastructure-module"))
    compileOnly("io.papermc.paper:paper-api:${providers.gradleProperty("paper_api_version").get()}")
}
