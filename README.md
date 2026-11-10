# gradle-archi-plugin

![Conformance](https://img.shields.io/badge/Conformance-Check--All%20Passing-brightgreen)

[![Build](https://github.com/flowdigger/gradle-archi-plugin/actions/workflows/ci.yml/badge.svg)](https://github.com/flowdigger/gradle-archi-plugin/actions/workflows/ci.yml)
[![Release](https://github.com/flowdigger/gradle-archi-plugin/actions/workflows/release.yml/badge.svg)](https://github.com/flowdigger/gradle-archi-plugin/actions/workflows/release.yml)
[![Coverage CI](https://github.com/flowdigger/gradle-archi-plugin/actions/workflows/coverage.yml/badge.svg)](https://github.com/flowdigger/gradle-archi-plugin/actions/workflows/coverage.yml)
[![CodeQL](https://github.com/flowdigger/gradle-archi-plugin/actions/workflows/codeql.yml/badge.svg)](https://github.com/flowdigger/gradle-archi-plugin/actions/workflows/codeql.yml)
[![Dependency Check](https://github.com/flowdigger/gradle-archi-plugin/actions/workflows/dependency-check.yml/badge.svg)](https://github.com/flowdigger/gradle-archi-plugin/actions/workflows/dependency-check.yml)
[![SpotBugs Security](https://github.com/flowdigger/gradle-archi-plugin/actions/workflows/spotbugs-security.yml/badge.svg)](https://github.com/flowdigger/gradle-archi-plugin/actions/workflows/spotbugs-security.yml)
[![Dependabot](https://img.shields.io/badge/dependabot-enabled-025E8C?logo=dependabot)](https://github.com/flowdigger/gradle-archi-plugin/security/dependabot)
[![Coverage](https://codecov.io/gh/flowdigger/gradle-archi-plugin/graph/badge.svg?branch=main)](https://codecov.io/gh/flowdigger/gradle-archi-plugin?branch=main)
[![Maven Central](https://img.shields.io/maven-central/v/org.flowdigger.gradle/gradle-archi-plugin.svg)](https://search.maven.org/artifact/org.flowdigger.gradle/gradle-archi-plugin)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/java-21+-green.svg)](https://www.oracle.com/java/)
[![Gradle](https://img.shields.io/badge/gradle-9.5+-blue.svg)](https://gradle.org/)

Gradle plugin to run Archi export processing as a **regular Gradle task**.

The plugin contributes one task named `archi` (type `org.flowdigger.gradle.archi.ArchiTask`) and does not use a custom DSL extension model anymore.

## What It Does

- Registers a configurable `archi` task.
- Runs bundled launcher script from plugin resources to execute Archi export pipeline.
- Accepts additional CLI args and environment variables.
- Writes one output file for each task execution.

## Plugin Coordinates

- Plugin id: `org.flowdigger.gradle.archi`
- Implementation class: `org.flowdigger.gradle.archi.ArchiGradlePlugin`
- Main task type: `org.flowdigger.gradle.archi.ArchiTask`

## Requirements

- Java 21+ (project currently builds and tests on Java 21)
- Gradle 9.x recommended (wrapper is included)

## Quick Start

Add the plugin and configure the `archi` task in `build.gradle`:

```groovy
plugins {
    id 'org.flowdigger.gradle.archi' version '1.0.0'
}

archi {
    input file('simple-helix.archimate')
    output file('build/simple-helix.export.xml')
}
```

Run:

```bash
./gradlew archi
```

## Using This Plugin Before Publishing

If this repository is local and not published yet, use a composite build from your consumer project:

`settings.gradle`

```groovy
pluginManagement {
    includeBuild('/absolute/path/to/gradle-archi-plugin')
}
```

Then in consumer `build.gradle`:

```groovy
plugins {
    id 'org.flowdigger.gradle.archi'
}
```

## Task Configuration

`ArchiTask` supports the following configuration methods/properties:

- `input(Object)` **required**: input model file.
- `output(Object)` **required**: export output file.
- `script(Object)` optional: adds `--script.runScript <value>`.
- `excel(Object)` optional: adds `--excel.export <value>`.
- `arg(String)` optional, repeatable: appends raw CLI args.
- `env(String, Object)` optional, repeatable: sets process environment vars.

If `input` or `output` is missing, task execution fails with:

- `archi.input is required`
- `archi.output is required`

## Examples

Ready-to-run sample consumer projects are available in:

- `samples/`

### 1) Bundled Launcher Mode

```groovy
archi {
    input file('src/main/archi/model.archimate')
    output file('build/model.export.xml')
    script file('scripts/export-assets.ajs')
    arg '--verbose'
    env 'PACKAGE_NAME', 'simple-helix'
}
```

### 2) Register Multiple Archi Tasks

```groovy
tasks.register('archiCatalog', org.flowdigger.gradle.archi.ArchiTask) {
    input file('catalog/catalog.archimate')
    output file('build/catalog.export.xml')
}

tasks.register('archiLandscape', org.flowdigger.gradle.archi.ArchiTask) {
    input file('landscape/landscape.archimate')
    output file('build/landscape.export.xml')
}
```

## Runtime Behavior

The plugin extracts bundled runtime resources under:

- `build/archi-runtime/`

and executes:

- `scripts/archi-launcher.sh`

with mapped environment values including `ARCHI_FILE`, `EXPORT_DIR`, `PACKAGE_NAME`, `HELIX_HOME`, and `ARCHI_RUNTIME`.

Before launching Archi, the launcher installs all bundled `.archiplugin` files with:

- `bin/install-archiplugin.sh`

Bundled plugin set is part of published plugin artifact (`archi/plugins/*.archiplugin`) and is treated as compatibility requirement for CI usage.

On CI agents (`GITHUB_ACTIONS=true` or `TF_BUILD=true`), launcher can auto-provision Archi when missing.
Defaults:

- install location: `$HOME/.local/archi`
- release tag/version source: `conf/archi-release.env`

Override knobs:

- `ARCHI_AUTO_INSTALL=false` disable auto-provision
- `ARCHI_HOME=/custom/path` use custom install/runtime location
- `ARCHI_RELEASE_TAG`, `ARCHI_VERSION` override default download coordinates
- `ARCHI_DOWNLOAD_URL` fully override download URL

The default script for exports is:

- `ajs/export-assets.ajs`

It loads the model from `ARCHI_FILE` and exports PDF/XML/XLSX artifacts into `EXPORT_DIR`.

## Scenarios

### Local Host Archi (macOS/Linux)

```bash
./gradlew archi
```

Set `ARCHI_HOME` if Archi is not in a default location:

```bash
ARCHI_HOME="$HOME/Applications/Archi.app" ./gradlew archi
```

### Containerized Archi

```bash
docker compose build archi
docker compose run --rm archi
```

This uses `xvfb-run` for headless Linux execution and writes exports under `build/archi-export`.

### CI (GitHub Actions / Azure DevOps)

If Archi executable is not present and CI environment is detected, launcher auto-installs Archi and then installs bundled plugins before running export script.

### Local Docker Run + Verification

Use the helper script to run a full container export and validate XML/XLSX/PDF outputs:

```bash
./scripts/docker-local-test.sh /absolute/path/to/model.archimate
```

Example:

```bash
./scripts/docker-local-test.sh /Users/cs79en/Developer/Projects/lineage/P01744-vortex-archi-lineage-processing-model/model.archimate
```

If Docker daemon is not running, start Docker Desktop first.

## GitHub Actions Docker Test

Workflow file:

- `.github/workflows/docker-test.yml`

What it does:

- Downloads a known-good sample model (`Archisurance.archimate`).
- Builds the Docker image on `linux/amd64`.
- Reuses Docker Buildx cache with `cache-from/cache-to: type=gha`.
- Runs the same local script (`scripts/docker-local-test.sh`) with `SKIP_DOCKER_BUILD=1`.
- Uploads `build/archi-export/` as an artifact.

## Development

Build and test plugin:

```bash
./gradlew clean test
```

Run static compile checks:

```bash
./gradlew compileJava
```

## Project Structure (Current)

- `src/main/java/org/flowdigger/gradle/archi/ArchiGradlePlugin.java`
- `src/main/java/org/flowdigger/gradle/archi/ArchiTask.java`
- `src/main/java/org/flowdigger/gradle/archi/ArchiBackend.java`
- `src/main/java/org/flowdigger/gradle/archi/CliArchiBackend.java`
- `src/main/java/org/flowdigger/gradle/archi/ArchimateRunner.java`

## Testing

Main test suites:

- `src/test/java/org/flowdigger/gradle/archi/ArchiTaskPluginTest.java`
- `src/test/java/org/flowdigger/gradle/archi/ArchimateRunnerTest.java`

Run:

```bash
./gradlew test
```

## Troubleshooting

- **Task not found (`archi`)**: ensure plugin id `org.flowdigger.gradle.archi` is applied.
- **Missing input/output error**: configure both `input` and `output` in task configuration.
- **Path problems**: prefer `file('relative/path')` over raw strings for portability.

## License

This project is licensed under the **MIT License** – see the [LICENSE](./LICENSE) file for full details.

## Contributing

Contributions are welcome! Feel free to open issues and pull requests on [GitHub](https://github.com/flowdigger/gradle-archi-plugin).

## Release to Maven Central

Set credentials and signing material:

```bash
export MAVEN_CENTRAL_USERNAME=...
export MAVEN_CENTRAL_PASSWORD=...
export SIGNING_KEY=...
export SIGNING_PASSWORD=...
```

Run local target (default):

```bash
./gradlew publish
```

Run Central target:

```bash
./gradlew publish -Pflowdigger.publish.target=central
```

Release workflow (`.github/workflows/release.yml`) calls shared reusable workflow and triggers on `v*` tags or manual dispatch.
