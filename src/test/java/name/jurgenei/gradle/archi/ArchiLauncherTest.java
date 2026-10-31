package org.flowdigger.gradle.archi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ArchiLauncherTest {
    @TempDir
    Path tempDir;

    @Test
    void testLauncherScriptExists() throws Exception {
        Path launcherPath = Path.of("src/main/resources/archi/scripts/archi-launcher.sh");
        assertTrue(Files.exists(launcherPath), "Launcher script should exist");
        assertTrue(Files.isReadable(launcherPath), "Launcher script should be readable");

        String content = Files.readString(launcherPath);
        assertTrue(content.contains("resolve_archi_executable"), "Should resolve Archi executable");
        assertTrue(content.contains("install_plugins"), "Should have install_plugins function");
        assertTrue(content.contains("auto_install_archi_on_ci"), "Should support CI auto-install");
        assertTrue(content.contains("ARCHI_AUTO_INSTALL"), "Should support auto-install opt-out");
    }

    @Test
    void testLauncherDetectsOS() throws Exception {
        String content = Files.readString(Path.of("src/main/resources/archi/scripts/archi-launcher.sh"));
        assertTrue(content.contains("detect_os()"), "Should detect OS");
        assertTrue(content.contains("darwin"), "Should support macOS");
        assertTrue(content.contains("linux-gnu"), "Should support Linux");
    }

    @Test
    void testInstallerScriptExists() {
        Path installer = Path.of("src/main/resources/archi/bin/install-archiplugin.sh");
        assertTrue(Files.exists(installer), "Plugin installer should exist");
        assertTrue(Files.isReadable(installer), "Plugin installer should be readable");
    }

    @Test
    void testResourcesStructure() {
        Path resourcesRoot = Path.of("src/main/resources/archi");
        assertTrue(Files.isDirectory(resourcesRoot.resolve("scripts")), "scripts directory should exist");
        assertTrue(Files.isDirectory(resourcesRoot.resolve("plugins")), "plugins directory should exist");
        assertTrue(Files.isDirectory(resourcesRoot.resolve("ajs")), "ajs directory should exist");
        assertTrue(Files.isDirectory(resourcesRoot.resolve("conf")), "conf directory should exist");
    }

    @Test
    void testArchiReleaseDefaultsFileExists() throws Exception {
        Path releaseDefaults = Path.of("src/main/resources/archi/conf/archi-release.env");
        assertTrue(Files.exists(releaseDefaults), "Archi release defaults file should exist");

        String content = Files.readString(releaseDefaults);
        assertTrue(content.contains("ARCHI_DEFAULT_RELEASE_TAG="), "Release tag default should be defined");
        assertTrue(content.contains("ARCHI_DEFAULT_VERSION="), "Release version default should be defined");
    }

    @Test
    void testBundledArchiPluginsArePresent() throws Exception {
        Path pluginsDir = Path.of("src/main/resources/archi/plugins");
        assertTrue(Files.isDirectory(pluginsDir), "Bundled plugins directory should exist");

        List<Path> bundledPlugins;
        try (var stream = Files.list(pluginsDir)) {
            bundledPlugins = stream
                    .filter(path -> path.getFileName().toString().endsWith(".archiplugin"))
                    .toList();
        }

        assertFalse(bundledPlugins.isEmpty(), "At least one .archiplugin must be bundled");
        assertTrue(
                bundledPlugins.stream().anyMatch(path -> path.getFileName().toString().contains("jArchi")),
                "Expected jArchi plugin in bundled set"
        );
        assertTrue(
                bundledPlugins.stream().anyMatch(path -> path.getFileName().toString().contains("coArchi2")),
                "Expected coArchi2 plugin in bundled set"
        );
    }
}
