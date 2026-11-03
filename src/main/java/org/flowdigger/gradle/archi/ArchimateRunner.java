package org.flowdigger.gradle.archi;

import org.gradle.api.logging.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Coordinates Archi backend execution with normalized arguments and environment.
 */
public class ArchimateRunner {
    private final ArchiBackend backend;

    /**
     * Creates a runner backed by the provided backend implementation.
     *
     * @param backend execution backend.
     */
    public ArchimateRunner(ArchiBackend backend) {
        this.backend = backend;
    }

    /**
    * Runs backend execution with null-safe args/environment maps.
    *
    * @param projectDir current project directory.
    * @param buildDir current build directory.
    * @param logger Gradle logger.
    * @param input source model input file.
    * @param output target output file.
    * @param args optional argument list; null becomes empty.
    * @param envs optional environment map; null becomes empty.
    */
    public void run(
            File projectDir,
            File buildDir,
            Logger logger,
            File input,
            File output,
            List<String> args,
            Map<String, Object> envs
    ) {
        if (args == null) {
            args = new ArrayList<>();
        }
        if (envs == null) {
            envs = new HashMap<>();
        }
        backend.run(projectDir, buildDir, logger, input, output, args, envs);
    }
}


