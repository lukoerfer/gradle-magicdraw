package de.lukaskoerfer.gradle.magicdraw;

import de.lukaskoerfer.gradle.magicdraw.tasks.PluginDescriptor;
import groovy.lang.GroovyCallable;
import lombok.val;
import org.gradle.api.Action;
import org.gradle.api.InvalidUserCodeException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.Delete;
import org.gradle.api.tasks.JavaExec;
import org.gradle.jvm.tasks.Jar;

import javax.swing.text.html.Option;
import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class MagicDrawPlugin implements Plugin<Project> {

    private MagicDrawExtension magicDraw;
    private MdPluginExtension plugin;

    private PluginDescriptor pluginDescriptor;
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
            .create("magicDraw", MagicDrawExtension.class);
        plugin = project.getExtensions()
            .create("plugin", MdPluginExtension.class);
        // Create tasks
        pluginDescriptor = project.getTasks()
            .create("pluginDescriptor", PluginDescriptor.class);
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
            .create("launch", JavaExec.class);
        launch.setDescription("Launches MagicDraw with this plugin");
        // Add all tasks to group
        Stream.of(pluginDescriptor, assemblePlugin, installPlugin, uninstallPlugin, launch)
            .forEach(task -> task.setGroup("MagicDraw"));
        // Apply task configuration
        configureTasks(project);
    }

    private void configureTasks(Project project) {
        Jar jar = project.getTasks().withType(Jar.class).getAt("jar");
        // Configure pluginDescriptor
        project.afterEvaluate($ ->
            pluginDescriptor.getLibraries().add(jar.getArchiveName())
        );
        // Configure assemblePlugin task
        assemblePlugin.into(file(project.getBuildDir(), "magicDraw"));
        assemblePlugin.into((GroovyCallable) () -> project.getGroup().toString(), copy ->
            copy.from(pluginDescriptor, jar)
        );
        // Configure installPlugin task
        installPlugin.from(assemblePlugin);
        installPlugin.into((GroovyCallable)() ->
            file(magicDraw.getInstallDir(), "plugins")
        );
        // Configure uninstallPlugin task
        uninstallPlugin.delete((GroovyCallable)() ->
            file(magicDraw.getInstallDir(), "plugins", project.getGroup())
        );
        // Configure launch task
        JavaPluginConvention java = project.getConvention().getPlugin(JavaPluginConvention.class);
        launch.setClasspath(java.getSourceSets().getAt("main").getRuntimeClasspath());
        launch.setMain("com.nomagic.osgi.launcher.ProductionFrameworkLauncher");
        launch.args("-verbose");
        launch.setMinHeapSize("60M");
        launch.setMaxHeapSize("200M");
        launch.jvmArgs("-Xss1024K", "-Xmx2000M");
        launch.systemProperty("LOCALCONFIG", true);
        launch.systemProperty("md.class.path", "$java.class.path");
        project.afterEvaluate($ -> {
            launch.systemProperty("md.plugins.dir",
                stringify(file(magicDraw.getInstallDir(), "plugins"),
                    assemblePlugin.getDestinationDir()));
            launch.systemProperty("com.nomagic.osgi.config.dir",
                file(magicDraw.getInstallDir(), "configuration"));
            launch.systemProperty("esi.system.config",
                file(magicDraw.getInstallDir(), "data", "application.conf"));
            launch.systemProperty("logback.configurationFile",
                file(magicDraw.getInstallDir(), "data", "logback.xml"));
        });
    }
    
    private Action<Project> evaluation = project -> {
        // Groovy truth for strings
        Predicate<Object> truth = val -> val != null && val.toString().length() > 0;
        // Set plugin id
        plugin.setId(Stream.of(plugin.getId(), project.getGroup())
            .filter(truth).findFirst().orElse("unknown").toString());
        plugin.setName(Stream.of(plugin.getName(), project.getName())
            .filter(truth).findFirst().orElseThrow(RuntimeException::new));
        
        Optional.of(plugin.getClassName()).filter(truth).orElseThrow(RuntimeException::new);
    };
    
    private static File file(File base, Object... subs) {
        for (Object sub : subs) {
            base = new File(base, sub.toString());
        }
        return base;
    }
    
    private static String stringify(Object... files) {
        return String.join(File.pathSeparator, Stream.of(files)
            .map(Object::toString).collect(Collectors.toList()));
    }

}
