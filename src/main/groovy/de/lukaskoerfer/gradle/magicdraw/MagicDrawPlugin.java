package de.lukaskoerfer.gradle.magicdraw;

import de.lukaskoerfer.gradle.magicdraw.descriptor.Descriptor;
import de.lukaskoerfer.gradle.magicdraw.extensions.MagicDrawExtension;
import de.lukaskoerfer.gradle.magicdraw.tasks.AssembleMagicDrawPlugin;
import de.lukaskoerfer.gradle.magicdraw.tasks.LaunchMagicDraw;
import groovy.lang.GroovyCallable;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.distribution.Distribution;
import org.gradle.api.distribution.DistributionContainer;
import org.gradle.api.distribution.plugins.DistributionPlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.Delete;

import java.util.ResourceBundle;
import java.util.stream.Stream;

import static de.lukaskoerfer.gradle.magicdraw.util.FileUtil.file;

/**
 * The MagicDraw plugin
 */
public class MagicDrawPlugin implements Plugin<Project> {
    
    private Project project;
    
    public static final String MAGICDRAW_GROUP_NAME = "MagicDraw";
    
    public static final String ASSEMBLE_PLUGIN_TASK_NAME = "assemblePlugin";
    public static final String INSTALL_PLUGIN_TASK_NAME = "installPlugin";
    public static final String UNINSTALL_PLUGIN_TASK_NAME = "uninstallPlugin";
    public static final String LAUNCH_MAGICDRAW_TASK_NAME = "launch";
    
    public static final String MAGICDRAW_CONFIGURATION = "magicDraw";
    
    /**
     * Applies the MagicDraw plugin to a Gradle project
     * @param project A Gradle project
     */
    @Override
    public void apply(Project project) {
        this.project = project;
        setupConfiguration();
        createTasks();
        registerExtensions();
        configureAssembleTask();
        configureInstallationTasks();
        // Integrate distribution plugin
        project.getPluginManager().withPlugin("distribution", plugin -> {
            setupDistributionPlugin();
        });
    }
    
    private void setupConfiguration() {
        Configuration magicDrawApi = project.getConfigurations()
            .create(MAGICDRAW_CONFIGURATION);
        project.getConfigurations().getAt(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME)
            .extendsFrom(magicDrawApi);
    }
    
    private void createTasks() {
        AssembleMagicDrawPlugin assemblePlugin = project.getTasks()
            .create(ASSEMBLE_PLUGIN_TASK_NAME, AssembleMagicDrawPlugin.class);
        Copy installPlugin = project.getTasks()
            .create(INSTALL_PLUGIN_TASK_NAME, Copy.class);
        Delete uninstallPlugin = project.getTasks()
            .create(UNINSTALL_PLUGIN_TASK_NAME, Delete.class);
        LaunchMagicDraw launch = project.getTasks()
            .create(LAUNCH_MAGICDRAW_TASK_NAME, LaunchMagicDraw.class);
        // Apply group and descriptions
        ResourceBundle descriptions = ResourceBundle.getBundle("descriptions");
        Stream.of(assemblePlugin, installPlugin, uninstallPlugin, launch).forEach(task -> {
            task.setGroup(MAGICDRAW_GROUP_NAME);
            task.setDescription(descriptions.getString(task.getName()));
        });
    }
    
    private void registerExtensions() {
        project.getExtensions().create("magicDraw", MagicDrawExtension.class, project);
        AssembleMagicDrawPlugin assemblePlugin = project.getTasks()
            .withType(AssembleMagicDrawPlugin.class).getAt(ASSEMBLE_PLUGIN_TASK_NAME);
        project.getConvention().add("mdDescriptor", assemblePlugin.getDescriptor());
    }
    
    private void configureAssembleTask() {
        AssembleMagicDrawPlugin assemblePlugin = project.getTasks()
            .withType(AssembleMagicDrawPlugin.class).getAt(ASSEMBLE_PLUGIN_TASK_NAME);
        Descriptor descriptor = assemblePlugin.getDescriptor();
        GroovyCallable assembleTarget = () ->
            file(project.getBuildDir(), "magicDraw", descriptor.getPlugin().get("id"));
        assemblePlugin.into(assembleTarget);
        Configuration magicDrawApi = project.getConfigurations()
            .getAt(MAGICDRAW_CONFIGURATION);
        assemblePlugin.exclude(element -> magicDrawApi.contains(element.getFile()));
    }
    
    private void configureInstallationTasks() {
        MagicDrawExtension magicDraw = project.getExtensions()
            .getByType(MagicDrawExtension.class);
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
    
    private void setupDistributionPlugin() {
        AssembleMagicDrawPlugin assemblePlugin = project.getTasks()
            .withType(AssembleMagicDrawPlugin.class).getAt(ASSEMBLE_PLUGIN_TASK_NAME);
        Distribution mainDistribution = project.getExtensions()
            .getByType(DistributionContainer.class)
            .getByName(DistributionPlugin.MAIN_DISTRIBUTION_NAME);
        mainDistribution.getContents().from(assemblePlugin);
    }

}
