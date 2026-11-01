package org.flowdigger.gradle.archi;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Registers Archi-related Gradle tasks.
 */
public class ArchiGradlePlugin implements Plugin<Project> {

    /**
     * Creates the plugin instance.
     */
    public ArchiGradlePlugin() {
    }

    /**
     * Registers the {@code archi} task.
     *
     * @param project target Gradle project.
     */
    @Override
    public void apply(Project project) {
        project.getTasks().register("archi", ArchiTask.class, task -> {
            task.getProjectDir().convention(project.getLayout().getProjectDirectory());
            task.getBuildDir().convention(project.getLayout().getBuildDirectory());
        });
    }
}

