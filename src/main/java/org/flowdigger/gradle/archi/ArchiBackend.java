package org.flowdigger.gradle.archi;

import org.gradle.api.logging.Logger;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Execution backend for Archi model processing.
 */
public interface ArchiBackend {
    /**
     * Executes an Archi processing run.
     *
     * @param projectDir current project directory.
     * @param buildDir current build directory.
     * @param logger Gradle logger for backend output.
     * @param input source model input file.
     * @param output target output file.
     * @param args command-line style arguments for the backend.
     * @param envs environment variables/options passed to the backend process.
     */
    void run(
            File projectDir,
            File buildDir,
            Logger logger,
            File input,
            File output,
            List<String> args,
            Map<String, Object> envs
    );
}


