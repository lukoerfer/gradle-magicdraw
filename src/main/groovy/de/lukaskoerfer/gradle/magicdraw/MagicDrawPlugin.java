package de.lukaskoerfer.gradle.magicdraw;

import de.lukaskoerfer.gradle.magicdraw.descriptor.MagicDrawPluginDescriptor;
import de.lukaskoerfer.gradle.magicdraw.extensions.MagicDrawExtension;
import de.lukaskoerfer.gradle.magicdraw.tasks.AssembleMagicDrawPlugin;
import de.lukaskoerfer.gradle.magicdraw.tasks.LaunchMagicDraw;
import org.gradle.api.NonNullApi;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.Delete;

import java.util.stream.Stream;

/**
 * The Gradle MagicDraw plugin
 */
public class MagicDrawPlugin implements Plugin<Project> {
    
    private Project project;
    
    public static final String MAGICDRAW_GROUP = "MagicDraw";
    
    public static final String ASSEMBLE_PLUGIN_TASK = "assemblePlugin";
    public static final String INSTALL_PLUGIN_TASK = "installPlugin";
    public static final String UNINSTALL_PLUGIN_TASK = "uninstallPlugin";
    public static final String LAUNCH_MAGICDRAW_TASK = "launch";
    
    public static final String MAGICDRAW_CONFIGURATION = "magicDraw";
    
    /**
     * Applies the MagicDraw plugin to a Gradle project
     * @param project A Gradle project
     */
    @Override
    public void apply(Project project) {
        this.project = project;
        setupEnvironment();
        createTasks();
        setupInstallationTasks();
    }
    
    private void setupEnvironment() {
        project.getExtensions().create("magicDraw", MagicDrawExtension.class, project);
        project.getConfigurations().create(MAGICDRAW_CONFIGURATION);
    }
    
    private void createTasks() {
        AssembleMagicDrawPlugin assemblePlugin = project.getTasks()
            .create(ASSEMBLE_PLUGIN_TASK, AssembleMagicDrawPlugin.class);
        assemblePlugin.setDescription("Assembles all MagicDraw plugin components in this project");
        Copy installPlugin = project.getTasks()
            .create(INSTALL_PLUGIN_TASK, Copy.class);
        installPlugin.setDescription("Installs this MagicDraw plugin into the local MagicDraw instance");
        Delete uninstallPlugin = project.getTasks()
            .create(UNINSTALL_PLUGIN_TASK, Delete.class);
        uninstallPlugin.setDescription("Removes this MagicDraw plugin from the local MagicDraw instance");
        LaunchMagicDraw launch = project.getTasks()
            .create(LAUNCH_MAGICDRAW_TASK, LaunchMagicDraw.class);
        launch.setDescription("Launches MagicDraw with this plugin");
        // Apply group
        Stream.of(assemblePlugin, installPlugin, uninstallPlugin, launch).forEach(task ->
            task.setGroup(MAGICDRAW_GROUP)
        );
    }
    
    private void setupInstallationTasks() {
        MagicDrawExtension magicDraw = project.getExtensions().getByType(MagicDrawExtension.class);
        AssembleMagicDrawPlugin assemblePlugin = project.getTasks()
            .withType(AssembleMagicDrawPlugin.class).getAt(ASSEMBLE_PLUGIN_TASK);
        MagicDrawPluginDescriptor descriptor = magicDraw.getPlugins().getMain();
        Copy installPlugin = project.getTasks()
            .withType(Copy.class).getAt(INSTALL_PLUGIN_TASK);
        installPlugin.from(assemblePlugin);
        installPlugin.into(magicDraw.getInstallDir().dir("plugins"));
        Delete uninstallPlugin = project.getTasks()
            .withType(Delete.class).getAt(UNINSTALL_PLUGIN_TASK);
        Provider<String> pluginDir = project.getProviders().provider(() -> "plugins/" + descriptor.getId());
        uninstallPlugin.delete(magicDraw.getInstallDir().dir(pluginDir));
    }

}
