package de.lukaskoerfer.gradle.magicdraw;

import de.lukaskoerfer.gradle.magicdraw.extensions.MagicDrawExtension;
import de.lukaskoerfer.gradle.magicdraw.extensions.MdPluginExtension;
import de.lukaskoerfer.gradle.magicdraw.tasks.MdLaunch;
import de.lukaskoerfer.gradle.magicdraw.tasks.MdPluginDescriptor;
import groovy.lang.GroovyCallable;
import org.gradle.api.InvalidUserCodeException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.Delete;
import org.gradle.api.tasks.JavaExec;
import org.gradle.jvm.tasks.Jar;

import java.util.stream.Stream;

import static de.lukaskoerfer.gradle.magicdraw.util.FileUtil.*;

@SuppressWarnings("unused")
public class MagicDrawPlugin implements Plugin<Project> {

    private MagicDrawExtension magicDraw;
    private MdPluginExtension plugin;

    private MdPluginDescriptor pluginDescriptor;
    private Copy assemblePlugin;
    private Copy installPlugin;
    private Delete uninstallPlugin;
    private JavaExec launch;

    @Override
    public void apply(Project project) {
        // Plugin checks
        if (!project.getPluginManager().hasPlugin("java")) {
            throw new InvalidUserCodeException("The MagicDraw plugin requires the Java plugin!");
        }
        // Extend project scope
        magicDraw = project.getExtensions()
            .create("magicDraw", MagicDrawExtension.class, project);
        plugin = project.getExtensions()
            .create("plugin", MdPluginExtension.class, project);
        // Create tasks
        pluginDescriptor = project.getTasks()
            .create("pluginDescriptor", MdPluginDescriptor.class);
        pluginDescriptor.setDescription("Creates a MagicDraw plugin tasks file");
        assemblePlugin = project.getTasks()
            .create("assemblePlugin", Copy.class);
        assemblePlugin.setDescription("Assembles all MagicDraw plugin components of this project");
        installPlugin = project.getTasks()
            .create("installPlugin", Copy.class);
        installPlugin.setDescription("Installs this MagicDraw plugin into the local instance");
        uninstallPlugin = project.getTasks()
            .create("uninstallPlugin", Delete.class);
        uninstallPlugin.setDescription("Removes this MagicDraw plugin from the local instance");
        launch = project.getTasks()
            .create("launch", MdLaunch.class);
        launch.setDescription("Launches MagicDraw with this plugin");
        // Add all tasks to group
        Stream.of(pluginDescriptor, assemblePlugin, installPlugin, uninstallPlugin, launch)
            .forEach(task -> task.setGroup("MagicDraw"));
        // Apply task configuration
        connectTasks(project);
    }

    private void connectTasks(Project project) {
        Jar jar = project.getTasks().withType(Jar.class).getAt("jar");
        // Configure pluginDescriptor
        project.afterEvaluate($ ->
            pluginDescriptor.getLibraries().add(jar.getArchiveName())
        );
        // Configure assemblePlugin task
        assemblePlugin.into(file(project.getBuildDir(), "magicDraw"));
        assemblePlugin.into((GroovyCallable) () -> plugin.getId(), copy ->
            copy.from(pluginDescriptor, jar)
        );
        // Configure installPlugin task
        installPlugin.from(assemblePlugin);
        installPlugin.into((GroovyCallable)() ->
            file(magicDraw.getInstallDir(), "plugins")
        );
        // Configure uninstallPlugin task
        uninstallPlugin.delete((GroovyCallable)() ->
            file(magicDraw.getInstallDir(), "plugins", plugin.getId())
        );
    }

}
