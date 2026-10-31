# Samples

![Conformance](https://img.shields.io/badge/Conformance-Check--All%20Passing-brightgreen)

This directory contains small consumer projects showing how to use the `name.flowdigger.gradle.archi` plugin from this repository via composite build.

## Prerequisites

- Java 21+
- Gradle available (`gradle`) or wrapper from this repository (`../../gradlew`)
- For CLI and Docker examples: Archi runtime and plugins as documented in the root `README.md`

## Samples

- `cli-host-archi/` - Real Archi run on host with script-based export.
- `docker-export/` - Uses plugin config and a helper task to run the same Docker export test locally.

## Run Samples

### 1) Host Archi sample

Set your model path and (optionally) `ARCHI_HOME` first:

```bash
cd samples/cli-host-archi
export MODEL_FILE="/absolute/path/to/model.archimate"
export ARCHI_HOME="$HOME/Applications/Archi.app"
gradle --no-daemon archi
```

### 2) Docker export sample

```bash
cd samples/docker-export
export MODEL_FILE="/absolute/path/to/model.archimate"
gradle --no-daemon dockerArchiExport
```

The Docker sample delegates to `scripts/docker-local-test.sh` and verifies XML/XLSX/PDF outputs.
