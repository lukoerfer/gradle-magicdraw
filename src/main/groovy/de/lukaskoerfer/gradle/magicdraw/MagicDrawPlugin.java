package de.lukaskoerfer.gradle.magicdraw;

import de.lukaskoerfer.gradle.magicdraw.descriptor.Descriptor;
import de.lukaskoerfer.gradle.magicdraw.extensions.MagicDrawExtension;
import de.lukaskoerfer.gradle.magicdraw.tasks.AssembleMagicDrawPlugin;
import de.lukaskoerfer.gradle.magicdraw.tasks.LaunchMagicDraw;
import groovy.lang.GroovyCallable;
import org.gradle.api.InvalidUserCodeException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.distribution.DistributionContainer;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.Delete;
import org.gradle.jvm.tasks.Jar;

import java.util.stream.Stream;

import static de.lukaskoerfer.gradle.magicdraw.util.FileUtil.file;

/**
 * The MagicDraw plugin
 */
@SuppressWarnings("unused")
public class MagicDrawPlugin implements Plugin<Project> {
    
    @Override
    public void apply(Project project) {
        if (!project.getPluginManager().hasPlugin("java")) {
            throw new InvalidUserCodeException("The MagicDraw plugin requires the Java plugin!");
        }
        createConfigurations(project);
        createTasks(project);
        registerExtensions(project);
        configureAssembleTask(project);
        configureInstallationTasks(project);
        setupDistributionPlugin(project);
    }
    
    private void createTasks(Project project) {
        AssembleMagicDrawPlugin assemblePlugin = project.getTasks()
            .create("assemblePlugin", AssembleMagicDrawPlugin.class);
        assemblePlugin.setDescription("Assembles all MagicDraw plugin components in this project");
        Copy installPlugin = project.getTasks()
            .create("installPlugin", Copy.class);
        installPlugin.setDescription("Installs this MagicDraw plugin into the local instance");
        Delete uninstallPlugin = project.getTasks()
            .create("uninstallPlugin", Delete.class);
        uninstallPlugin.setDescription("Removes this MagicDraw plugin from the local instance");
        LaunchMagicDraw launch = project.getTasks()
            .create("launch", LaunchMagicDraw.class);
        launch.setDescription("Launches MagicDraw with this plugin");
        Stream.of(assemblePlugin, installPlugin, uninstallPlugin, launch)
            .forEach(task -> task.setGroup("MagicDraw"));
    }
    
    private void createConfigurations(Project project) {
        Configuration mdCompile = project.getConfigurations().create("mdCompile");
        project.getConfigurations().getAt("compile").extendsFrom(mdCompile);
        Configuration mdRuntime = project.getConfigurations().create("mdRuntime");
        project.getConfigurations().getAt("runtime").extendsFrom(mdRuntime);
    }
    
    private void registerExtensions(Project project) {
        project.getExtensions().create("magicDraw", MagicDrawExtension.class, project);
        AssembleMagicDrawPlugin assemblePlugin = project.getTasks()
            .withType(AssembleMagicDrawPlugin.class).getAt("assemblePlugin");
        project.getConvention().add("mdDescriptor", assemblePlugin.getDescriptor());
    }
    
    private void configureAssembleTask(Project project) {
        AssembleMagicDrawPlugin assemblePlugin = project.getTasks()
            .withType(AssembleMagicDrawPlugin.class).getAt("assemblePlugin");
        Descriptor descriptor = assemblePlugin.getDescriptor();
        Configuration mdCompile = project.getConfigurations().getAt("mdCompile");
        Configuration mdRuntime = project.getConfigurations().getAt("mdRuntime");
        Jar jar = project.getTasks().withType(Jar.class).getAt("jar");
        GroovyCallable assembleTarget = () ->
            file(project.getBuildDir(), "magicDraw", descriptor.getPlugin().get("id"));
        assemblePlugin.from(jar);
        assemblePlugin.exclude(element -> mdCompile.contains(element.getFile()));
        assemblePlugin.exclude(element -> mdRuntime.contains(element.getFile()));
        assemblePlugin.into(assembleTarget);
    }
    
    private void configureInstallationTasks(Project project) {
        MagicDrawExtension magicDraw = project.getExtensions().getByType(MagicDrawExtension.class);
        AssembleMagicDrawPlugin assemblePlugin = project.getTasks()
            .withType(AssembleMagicDrawPlugin.class).getAt("assemblePlugin");
        Descriptor descriptor = assemblePlugin.getDescriptor();
        Copy installPlugin = project.getTasks()
            .withType(Copy.class).getAt("installPlugin");
        Delete uninstallPlugin = project.getTasks()
            .withType(Delete.class).getAt("uninstallPlugin");
        GroovyCallable installationTarget = () ->
            file(magicDraw.getInstallDir(), "plugins", descriptor.getPlugin().get("id"));
        installPlugin.from(assemblePlugin);
        installPlugin.into(installationTarget);
        uninstallPlugin.delete(installationTarget);
    }
    
    private void setupDistributionPlugin(Project project) {
        AssembleMagicDrawPlugin assemblePlugin = project.getTasks()
            .withType(AssembleMagicDrawPlugin.class).getAt("assemblePlugin");
        project.getPluginManager().withPlugin("distribution", $ ->
            project.getExtensions().getByType(DistributionContainer.class)
                .getByName("main").getContents().from(assemblePlugin));
    }

}
