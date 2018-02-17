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
import org.gradle.api.distribution.Distribution;
import org.gradle.api.distribution.DistributionContainer;
import org.gradle.api.distribution.plugins.DistributionPlugin;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.Delete;
import org.gradle.jvm.tasks.Jar;

import java.util.stream.Stream;

import static de.lukaskoerfer.gradle.magicdraw.util.FileUtil.file;

/**
 * The MagicDraw plugin
 */
public class MagicDrawPlugin implements Plugin<Project> {
    
    public static final String MAGIC_DRAW_GROUP_NAME = "MagicDraw";
    
    public static final String ASSEMBLE_PLUGIN_TASK_NAME = "assemblePlugin";
    public static final String INSTALL_PLUGIN_TASK_NAME = "installPlugin";
    public static final String UNINSTALL_PLUGIN_TASK_NAME = "uninstallPlugin";
    public static final String LAUNCH_TASK_NAME = "launch";
    
    @Override
    public void apply(Project project) {
        project.getPluginManager().withPlugin("java", appliedPlugin -> {
            createConfigurations(project);
            createTasks(project);
            registerExtensions(project);
            configureAssembleTask(project);
            configureInstallationTasks(project);
            setupDistributionPlugin(project);
        });
    }
    
    private void createTasks(Project project) {
        AssembleMagicDrawPlugin assemblePlugin = project.getTasks()
            .create(ASSEMBLE_PLUGIN_TASK_NAME, AssembleMagicDrawPlugin.class);
        assemblePlugin.setDescription("Assembles all MagicDraw plugin components in this project");
        Copy installPlugin = project.getTasks()
            .create(INSTALL_PLUGIN_TASK_NAME, Copy.class);
        installPlugin.setDescription("Installs this MagicDraw plugin into the local instance");
        Delete uninstallPlugin = project.getTasks()
            .create(UNINSTALL_PLUGIN_TASK_NAME, Delete.class);
        uninstallPlugin.setDescription("Removes this MagicDraw plugin from the local instance");
        LaunchMagicDraw launch = project.getTasks()
            .create(LAUNCH_TASK_NAME, LaunchMagicDraw.class);
        launch.setDescription("Launches MagicDraw with this plugin");
        // Set group to all tasks
        Stream.of(assemblePlugin, installPlugin, uninstallPlugin, launch)
            .forEach(task -> task.setGroup(MAGIC_DRAW_GROUP_NAME));
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
            .withType(AssembleMagicDrawPlugin.class).getAt(ASSEMBLE_PLUGIN_TASK_NAME);
        project.getConvention().add("mdDescriptor", assemblePlugin.getDescriptor());
    }
    
    private void configureAssembleTask(Project project) {
        AssembleMagicDrawPlugin assemblePlugin = project.getTasks()
            .withType(AssembleMagicDrawPlugin.class).getAt(ASSEMBLE_PLUGIN_TASK_NAME);
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
            .withType(AssembleMagicDrawPlugin.class).getAt(ASSEMBLE_PLUGIN_TASK_NAME);
        Descriptor descriptor = assemblePlugin.getDescriptor();
        Copy installPlugin = project.getTasks()
            .withType(Copy.class).getAt(INSTALL_PLUGIN_TASK_NAME);
        Delete uninstallPlugin = project.getTasks()
            .withType(Delete.class).getAt(UNINSTALL_PLUGIN_TASK_NAME);
        GroovyCallable installationTarget = () ->
            file(magicDraw.getInstallDir(), "plugins", descriptor.getPlugin().get("id"));
        installPlugin.from(assemblePlugin);
        installPlugin.into(installationTarget);
        uninstallPlugin.delete(installationTarget);
    }
    
    private void setupDistributionPlugin(Project project) {
        AssembleMagicDrawPlugin assemblePlugin = project.getTasks()
            .withType(AssembleMagicDrawPlugin.class).getAt(ASSEMBLE_PLUGIN_TASK_NAME);
        project.getPluginManager().withPlugin("distribution", appliedPlugin -> {
            Distribution mainDistribution = project.getExtensions()
                .getByType(DistributionContainer.class)
                .getByName(DistributionPlugin.MAIN_DISTRIBUTION_NAME);
            mainDistribution.getContents().from(assemblePlugin);
        });
    }

}
