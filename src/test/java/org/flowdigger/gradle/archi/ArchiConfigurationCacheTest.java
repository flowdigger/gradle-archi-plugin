package org.flowdigger.gradle.archi;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.BuildTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArchiConfigurationCacheTest {

    @TempDir
    Path testProjectDir;

    @Test
    void archiTaskRunsWithConfigurationCache() throws Exception {
        Files.writeString(testProjectDir.resolve("settings.gradle"), "rootProject.name = 'cc-smoke'\n");
        Files.writeString(
                testProjectDir.resolve("build.gradle"),
                "plugins { id 'org.flowdigger.gradle.archi' }\n" +
                        "tasks.named('archi') {\n" +
                        "  input file('in.xml')\n" +
                                        "  output \"$buildDir/out.xml\"\n" +
                        "}\n"
        );
        Files.writeString(testProjectDir.resolve("in.xml"), "<archimate></archimate>\n");

        BuildResult firstRun = GradleRunner.create()
                .withProjectDir(testProjectDir.toFile())
                .withPluginClasspath()
                .withArguments("archi", "--configuration-cache")
                .build();

        BuildResult secondRun = GradleRunner.create()
                .withProjectDir(testProjectDir.toFile())
                .withPluginClasspath()
                .withArguments("archi", "--configuration-cache")
                .build();

        BuildTask firstArchi = firstRun.task(":archi");
        BuildTask secondArchi = secondRun.task(":archi");
        assertNotNull(firstArchi, "First run must execute :archi task");
        assertNotNull(secondArchi, "Second run must execute :archi task");

        assertEquals(SUCCESS, firstArchi.getOutcome());
        assertTrue(
                secondArchi.getOutcome() == SUCCESS
                        || secondArchi.getOutcome() == UP_TO_DATE,
                "Expected second run task outcome to be SUCCESS or UP_TO_DATE"
        );
        assertTrue(
                secondRun.getOutput().contains("Configuration cache entry reused")
                        || secondRun.getOutput().contains("Configuration cache entry stored"),
                "Expected configuration cache activity in second run output"
        );
    }
}
