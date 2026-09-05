# Paper Plugin Development Environment Design

## Goal

Create a public GitHub repository that provides a reproducible, offline-mode
Minecraft testing environment with three top-level areas: `Server/`, `Client/`,
and `Plugins/`. Java plugins are built with Gradle and deployed into the local
Paper server's `plugins` directory. Every build and integration run produces
machine-readable feedback that can guide subsequent Agent iterations.

## Scope and assumptions

- The initial target is Paper 1.21.11 with Java 21, pinned in repository
  configuration. The version is intentionally centralized so it can be changed
  without restructuring the project.
- The server is for local testing only and uses `online-mode=false`.
- Bukkit-compatible plugins are the default API surface; Paper-specific APIs
  may be added by an individual plugin when needed.
- Client and server binaries, downloaded libraries, world saves, and raw logs
  are runtime artifacts. They are not committed to GitHub.
- Source code, scripts, configuration templates, test reports, and compact
  feedback summaries are committed.

## Repository layout

```text
Server/
  runtime/              # generated Paper server runtime
  plugins/              # deployed plugin JARs, generated at build time
  config/               # tracked server configuration templates
  scripts/              # server lifecycle helpers
Client/
  launcher/             # local launcher configuration and scripts
  mods/                 # optional client-side testing additions
Plugins/
  ExamplePlugin/        # Gradle Kotlin DSL Java plugin project
tools/
  build-and-deploy.sh   # build, copy JAR, and emit build metadata
  run-test.sh           # start server and execute integration checks
  collect-feedback.sh   # normalize logs and test results
feedback/
  latest/               # current run summary
  history/              # committed historical summaries
.github/workflows/
  build-plugin.yml
  integration-test.yml
docs/
  superpowers/specs/
```

## Components and responsibilities

### Plugin project

`Plugins/ExamplePlugin` is a normal Java Gradle project. Its build produces a
JAR containing `plugin.yml` and writes the artifact to `build/libs`. Unit tests
run before deployment. The plugin's API and server version are derived from
the root version configuration.

### Server runtime

The server bootstrap script ensures the configured Paper version is available,
creates the runtime directory, installs tracked configuration templates, and
starts Paper with the required Java version. Runtime state is isolated below
`Server/runtime`; the deploy target is `Server/runtime/plugins` (with a tracked
`Server/plugins` compatibility link or documented alias only if needed by the
chosen local layout).

The server configuration must set `online-mode=false`, disable accidental
production-facing behavior, and expose a deterministic test port. The EULA
acceptance is explicit and documented rather than silently inferred.

### Client

`Client` contains launch configuration and helper scripts, not a redistributed
Minecraft binary. The client is configured for the same protocol/version and
connects to the local server. Client automation is optional in CI because
headless integration tests can validate the plugin without a graphical client.

### Pipeline and feedback

The canonical local command is:

```text
build plugin -> run unit tests -> deploy JAR -> start Paper -> run checks
-> collect feedback -> stop server -> write summary
```

Each run receives a timestamp and source commit identifier. Feedback includes
build status, test status, deployed artifact checksum, server exit status,
plugin enable/disable status, and selected error/warning lines. Raw logs stay
local or are uploaded as CI artifacts; committed feedback is a redacted,
bounded summary in JSON and Markdown. A failed step stops downstream steps,
while cleanup and feedback collection run regardless.

## GitHub workflow

- `build-plugin.yml` runs on pushes and pull requests, checks Java/Gradle,
  compiles the plugin, runs unit tests, and publishes the JAR as a workflow
  artifact.
- `integration-test.yml` runs the same build-and-deploy flow in a clean Linux
  runner, starts Paper in offline mode, runs smoke checks, and uploads raw logs
  plus the normalized feedback summary.
- Local commits are the source of truth for changes. Generated runtime files
  and credentials are excluded by `.gitignore`; no GitHub token is stored in
  the repository.

## Verification criteria

The first implementation is complete when:

1. The three requested top-level directories exist and are documented.
2. `Plugins/ExamplePlugin` compiles and its unit test passes with the pinned
   Java/Gradle toolchain.
3. A local build copies exactly one versioned plugin JAR into the Paper server
   plugin directory.
4. The server starts in offline mode, loads the plugin, and can be stopped by
   the pipeline without leaving a stale process.
5. A successful and a failing run both produce feedback summaries with a
   commit identifier and actionable status fields.
6. GitHub Actions can reproduce plugin build and headless integration testing.
7. `git status` remains clean after a successful run except for intentionally
   generated feedback files selected for commit.

## Risks and non-goals

- The Minecraft client binary is not provisioned or redistributed by this
  repository; users must provide a legally obtained local installation.
- Paper and Minecraft versions change their Java requirements. The pinned
  version and toolchain must be updated together.
- Automatic Agent commits are intentionally not performed by CI. The pipeline
  emits evidence; an authorized Agent or developer decides what change to make
  and commits it, keeping human-readable Git history.
- This first stage does not include a web dashboard, remote telemetry service,
  multiplayer authentication, or production deployment.
