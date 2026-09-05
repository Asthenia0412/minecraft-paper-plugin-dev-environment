# Paper Plugin Development Environment Design

## Goal

Create a public GitHub repository that provides a reproducible, offline-mode
Minecraft testing environment with three top-level areas: `Server/`, `Client/`,
and `Plugins/`. Java plugins are built with Gradle and deployed into the local
Paper server's `plugins` directory. Every build and integration run produces
machine-readable feedback that can guide subsequent Agent iterations.

## Scope and assumptions

- The initial target is Paper 1.21.11 with Java 21. The implementation must
  select one exact stable Paper build from the official Paper downloads API and
  record its build number, download URL, and SHA-256 in a tracked lock file;
  neither local setup nor CI may resolve a floating `latest` build. The
  version is centralized so it can be changed without restructuring the
  project.
- The implementation also commits a Gradle Wrapper version and verifies the
  Paper JAR checksum before use. The official Paper repository/API is the only
  download source in the first version.
- The build uses the Temurin Java 21 distribution. Gradle plugins and project
  dependencies are version-pinned and dependency locking is enabled; GitHub
  Actions references are pinned to commit SHAs. These pins define the
  reproducibility boundary for the first release.
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
  runtime/              # generated Paper server runtime; the only deploy target
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

The server bootstrap script ensures the locked Paper version is available,
creates `Server/runtime`, installs tracked configuration templates, and starts
Paper with the required Java version. `Server/runtime/plugins` is the only
plugin deployment target; no second `Server/plugins` directory or alias is
used. The bootstrap writes a PID file, waits for the exact server-ready log
marker, and fails on timeout. The stop routine sends a graceful stop, waits a
bounded time, then reports failure if the owned PID remains alive; it never
kills unrelated Java processes.

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
-> stop server -> collect final feedback -> write summary
```

Each run receives a timestamp and source commit identifier. For a clean tree,
`commit` is `HEAD`; for a dirty local tree, the summary records `HEAD` plus a
hash of the uncommitted diff and `dirty: true`, so results cannot be confused
with a committed revision. Feedback includes
build status, test status, deployed artifact checksum, server exit status,
plugin enable/disable status, and selected error/warning lines. Raw logs stay
local or are uploaded as CI artifacts; committed feedback is a redacted,
bounded summary in JSON and Markdown. The JSON contract is versioned as
`feedback_schema_version: 1` and requires `run_id`, `run_kind`, `commit`,
`started_at`, `finished_at`, `status`, `steps`, `artifact`, `server`, and
`errors`. `run_kind` is `build` or `integration`. Each step has `name`,
`status`, `started_at`, `finished_at`, and `exit_code`. Step status is one of
`passed`, `failed`, or `skipped`; skipped steps have a JSON `null` exit code.
For `build` runs, `artifact` is required and `server` is `null`; success means
the build and unit tests pass. For `integration` runs, both `artifact` and
`server` are required; success means build, deployment, server readiness,
plugin enablement, smoke checks, and cleanup all pass. A failed step stops
downstream checks, while cleanup and the final feedback phase run regardless.
Raw logs are truncated to a configured maximum and secrets are redacted.

The server listens on `127.0.0.1:25565`. Readiness is the Paper log line
matching `Done ([^)]+)! For help, type "help"`. The example plugin exposes
`/devkit status`, which returns a deterministic success response and is the
headless smoke check used by local and CI integration runs. Timestamps use
RFC 3339 UTC; dirty-tree fingerprints and artifact checksums use SHA-256.
Redaction removes values matching common token/password/key environment
patterns before summaries are written.

## GitHub workflow

- `build-plugin.yml` runs on pushes and pull requests, checks Java/Gradle,
  compiles the plugin, runs unit tests, generates the schema-versioned feedback
  summary, and publishes the JAR plus summary as workflow artifacts.
- `integration-test.yml` runs the same build-and-deploy flow in a clean Linux
  runner, starts Paper in offline mode, runs smoke checks, and uploads raw logs
  plus the normalized feedback summary.
- Local runs copy the normalized summary into `feedback/history/<run_id>/` for
  an explicit developer/Agent commit; `feedback/latest/` is regenerated on
  every local run and is ignored unless deliberately selected for review. CI
  treats the summary and raw logs as
  workflow artifacts and does not push commits; this keeps CI credentials out
  of the repository while preserving every accepted iteration in Git history.
- Before deployment, the pipeline removes only the previously generated JAR
  for this plugin's artifact name, then copies the new versioned JAR. It never
  deletes unrelated plugins.
- Local commits are the source of truth for changes. Generated runtime files
  and credentials are excluded by `.gitignore`; no GitHub token is stored in
  the repository.

## Verification criteria

The first implementation is complete when:

1. The three requested top-level directories exist and are documented.
2. `Plugins/ExamplePlugin` compiles and its unit test passes with the pinned
   Java/Gradle toolchain.
3. A local build copies exactly one versioned plugin JAR into
   `Server/runtime/plugins/`, the sole deployment directory.
4. The server starts in offline mode, loads the plugin, and can be stopped by
   the pipeline without leaving a stale process.
5. A successful and a failing run both produce schema-versioned feedback
   summaries with a commit identifier and actionable status fields.
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
