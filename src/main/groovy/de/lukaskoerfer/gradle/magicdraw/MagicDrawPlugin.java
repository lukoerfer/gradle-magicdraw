package de.lukaskoerfer.gradle.magicdraw;

import de.lukaskoerfer.gradle.magicdraw.extensions.MagicDrawExtension;
import de.lukaskoerfer.gradle.magicdraw.tasks.AssemblePlugin;
import de.lukaskoerfer.gradle.magicdraw.tasks.Launch;
import groovy.lang.GroovyCallable;
import org.gradle.api.InvalidUserCodeException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.distribution.DistributionContainer;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.Delete;
import org.gradle.jvm.tasks.Jar;

import java.util.Map;
import java.util.stream.Stream;

import static de.lukaskoerfer.gradle.magicdraw.util.FileUtil.file;

/**
 * The MagicDraw plugin
 */
@SuppressWarnings("unused")
public class MagicDrawPlugin implements Plugin<Project> {
    
    @Override
    public void apply(Project project) {
        // Plugin checks
        if (!project.getPluginManager().hasPlugin("java")) {
            throw new InvalidUserCodeException("The MagicDraw plugin requires the Java plugin!");
        }
        // Register magic draw extension
        MagicDrawExtension magicDraw = project.getExtensions()
            .create("magicDraw", MagicDrawExtension.class, project);
        // Create configurations
        Configuration mdCompile = project.getConfigurations().create("mdCompile");
        project.getConfigurations().getAt("compile").extendsFrom(mdCompile);
        Configuration mdRuntime = project.getConfigurations().create("mdRuntime");
        project.getConfigurations().getAt("runtime").extendsFrom(mdRuntime);
        // Create tasks
        AssemblePlugin assemblePlugin = project.getTasks()
            .create("assemblePlugin", AssemblePlugin.class);
        assemblePlugin.setDescription("Assembles all MagicDraw plugin components in this project");
        Copy installPlugin = project.getTasks()
            .create("installPlugin", Copy.class);
        installPlugin.setDescription("Installs this MagicDraw plugin into the local instance");
        Delete uninstallPlugin = project.getTasks()
            .create("uninstallPlugin", Delete.class);
        uninstallPlugin.setDescription("Removes this MagicDraw plugin from the local instance");
        Launch launch = project.getTasks()
            .create("launch", Launch.class);
        launch.setDescription("Launches MagicDraw with this plugin");
        // Register plugin map as project extension
        Map plugin = assemblePlugin.getPlugin();
        project.getExtensions().add("plugin", plugin);
        // Add all tasks to group
        Stream.of(assemblePlugin, installPlugin, uninstallPlugin, launch)
            .forEach(task -> task.setGroup("MagicDraw"));
        // Configure assemble task
        Jar jar = project.getTasks().withType(Jar.class).getAt("jar");
        GroovyCallable assembleTarget = () ->
            file(project.getBuildDir(), "magicDraw", plugin.get("id"));
        assemblePlugin.from(jar);
        assemblePlugin.exclude(element -> mdCompile.contains(element.getFile()));
        assemblePlugin.exclude(element -> mdRuntime.contains(element.getFile()));
        assemblePlugin.into(assembleTarget);
        // Configure installation tasks
        GroovyCallable installationTarget = () ->
            file(magicDraw.getInstallDir(), "plugins", plugin.get("id"));
        installPlugin.from(assemblePlugin);
        installPlugin.into(installationTarget);
        uninstallPlugin.delete(installationTarget);
        // Setup distribution plugin
        project.getPluginManager().withPlugin("distribution", appliedPlugin ->
            project.getExtensions().getByType(DistributionContainer.class)
                .getByName("main").getContents().from(assemblePlugin));
    }

}
