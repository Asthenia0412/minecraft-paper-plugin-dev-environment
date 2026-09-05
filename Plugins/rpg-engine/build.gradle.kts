plugins {
    java
}

allprojects {
    group = providers.gradleProperty("plugin_group").get()
    version = providers.gradleProperty("plugin_version").get()
}

subprojects {
    apply(plugin = "java")

    repositories {
        mavenCentral()
        maven {
            name = "papermc"
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(providers.gradleProperty("java_version").get().toInt()))
    }

    dependencies {
        add("testImplementation", platform("org.junit:junit-bom:5.11.4"))
        add("testImplementation", "org.junit.jupiter:junit-jupiter")
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}

