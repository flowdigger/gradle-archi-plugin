package org.flowdigger.gradle.archi;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArchimateRunnerTest {
    private static final Logger TEST_LOGGER = Logging.getLogger(ArchimateRunnerTest.class);

    @Test
    void cliShouldProduceOutput() throws Exception {
        File dir = Files.createTempDirectory("archi-cli").toFile();
        File input = new File(dir, "in.archimate");
        Files.writeString(input.toPath(), "<archimate/>");
        File output = new File(dir, "out.xml");

        new ArchimateRunner(new CliArchiBackend()).run(
                dir,
                new File(dir, "build"),
                TEST_LOGGER,
                input,
                output,
                List.of(),
                Map.of("ARCHI_USE_MOCK", "true")
        );

        assertTrue(output.exists());
        assertTrue(Files.readString(output.toPath()).contains("http://www.opengroup.org/xsd/archimate/3.0/"));
    }

    @Test
    void runnerNormalizesNullInputsAndPassesDirectoriesWithoutProject() throws Exception {
        File projectDir = Files.createTempDirectory("archi-project").toFile();
        File buildDir = new File(projectDir, "build");
        RecordingBackend backend = new RecordingBackend();

        new ArchimateRunner(backend).run(projectDir, buildDir, TEST_LOGGER, null, null, null, null);

        assertEquals(projectDir, backend.projectDir);
        assertEquals(buildDir, backend.buildDir);
        assertNotNull(backend.logger);
        assertEquals(0, backend.args.size());
        assertEquals(0, backend.envs.size());
    }

    private static final class RecordingBackend implements ArchiBackend {
        private File projectDir;
        private File buildDir;
        private Logger logger;
        private List<String> args = List.of();
        private Map<String, Object> envs = Map.of();

        @Override
        public void run(File projectDir, File buildDir, Logger logger, File input, File output, List<String> args, Map<String, Object> envs) {
            this.projectDir = projectDir;
            this.buildDir = buildDir;
            this.logger = logger;
            this.args = new ArrayList<>(args);
            this.envs = envs;
        }
    }
}

