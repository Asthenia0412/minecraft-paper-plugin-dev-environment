# Paper Plugin Development Environment

An offline-mode Minecraft test environment for Java Paper/Bukkit plugin
development.

## Layout

- `Server/` contains tracked configuration and the generated Paper runtime.
- `Client/` contains local client launch notes and configuration.
- `Plugins/` contains Java plugin projects.
- `tools/` contains the build, deploy, test, and feedback pipeline.
- `feedback/` contains small, reviewable run summaries.

## Requirements

- Java 21 or newer (Java 25 is supported locally).
- A legally obtained Minecraft client installation for graphical testing.
- Internet access on first setup to download the pinned Paper server artifact.

## Quick start

```sh
./gradlew -p Plugins/ExamplePlugin test
./tools/build-and-deploy.sh
./tools/run-test.sh
# Full build, deploy, integration test, and final feedback
EULA_ACCEPTED=true ./tools/pipeline.sh
```

The pipeline runs the plugin tests, deploys the JAR to
`Server/runtime/plugins/`, starts Paper in offline mode, checks
`/devkit status`, stops the owned server process, and writes a summary under
`feedback/latest/` and `feedback/history/`.

The first local run requires accepting the Minecraft EULA by setting
`EULA_ACCEPTED=true` in the environment. This is intentionally explicit.
