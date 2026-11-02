package org.flowdigger.gradle.archi;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArchiTaskAndPluginTest {

    @TempDir
    Path tempDir;

    @Test
    void pluginRegistersArchiTaskWithDirectoryConventions() {
        Project project = ProjectBuilder.builder().withProjectDir(tempDir.toFile()).build();

        // Cover explicit constructor and apply path.
        ArchiGradlePlugin plugin = new ArchiGradlePlugin();
        plugin.apply(project);

        ArchiTask task = (ArchiTask) project.getTasks().getByName("archi");

        assertEquals(project.getProjectDir(), task.getProjectDir().get().getAsFile());
        assertEquals(project.getBuildDir(), task.getBuildDir().get().getAsFile());
    }

    @Test
    void archiTaskExecutesCliBackendWithConfiguredArgsAndEnv() throws Exception {
        Project project = ProjectBuilder.builder().withProjectDir(tempDir.toFile()).build();
        project.getPlugins().apply(ArchiGradlePlugin.class);

        File input = tempDir.resolve("input.archimate").toFile();
        Files.writeString(input.toPath(), "<model/>");
        File output = tempDir.resolve("build/output/export.xml").toFile();

        ArchiTask task = (ArchiTask) project.getTasks().getByName("archi");
        task.input(input);
        task.output(output);
        task.script("custom/export.ajs");
        task.excel("custom.xlsx");
        task.arg("--verbose");
        task.arg("--trace");
        task.env("ARCHI_USE_MOCK", "true");
        task.env("PACKAGE_NAME", "test-package");
        task.env("EXTRA_ENV", 42);

        task.runArchi();

        assertTrue(output.exists());
        String xml = Files.readString(output.toPath());
        assertTrue(xml.contains("http://www.opengroup.org/xsd/archimate/3.0/"));

        List<String> args = task.getArgs().get();
        assertEquals(List.of("--verbose", "--trace"), args);

        Map<String, String> envs = task.getEnvs().get();
        assertEquals("true", envs.get("ARCHI_USE_MOCK"));
        assertEquals("test-package", envs.get("PACKAGE_NAME"));
        assertEquals("42", envs.get("EXTRA_ENV"));
    }
}
