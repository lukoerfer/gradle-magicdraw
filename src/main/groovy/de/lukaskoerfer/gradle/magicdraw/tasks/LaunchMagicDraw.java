package de.lukaskoerfer.gradle.magicdraw.tasks;

import de.lukaskoerfer.gradle.magicdraw.MagicDrawPlugin;
import de.lukaskoerfer.gradle.magicdraw.extensions.MagicDrawExtension;
import lombok.Getter;
import lombok.Setter;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.SourceSet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static de.lukaskoerfer.gradle.magicdraw.util.FileUtil.*;

/**
 * Task type to launch a MagicDraw instance
 */
public class LaunchMagicDraw extends JavaExec {
    
    /**
     * Whether MagicDraw should be launched in verbose mode (more detailed console output)
     */
    @Getter @Setter
    private boolean verbose = true;
    
    /**
     * Whether MagicDraw should be launched with a local configuration file
     */
    @Getter @Setter
    private boolean localConfig = true;
    
    /**
     * A list of directories where MagicDraw should search for plugins
     */
    @Getter
    private List<File> pluginDirs = new ArrayList<>();
    
    public LaunchMagicDraw() {
        preSetup();
        getProject().afterEvaluate($ -> finalSetup());
    }
    
    private void preSetup() {
        setClasspath(buildClasspath());
        setMain("com.nomagic.osgi.launcher.ProductionFrameworkLauncher");
        setMinHeapSize("60M");
        setMaxHeapSize("200M");
        jvmArgs("-Xss1024K", "-Xmx2000M");
        systemProperty("md.class.path", "$java.class.path");
    }
    
    private void finalSetup() {
        MagicDrawExtension magicDraw = getProject().getExtensions()
            .getByType(MagicDrawExtension.class);
        File root = magicDraw.getInstallDir();
        // Specify configuration properties
        if (verbose) args("-verbose");
        systemProperty("localConfig", localConfig);
        systemProperty("com.nomagic.osgi.config.dir", file(root, "configuration"));
        systemProperty("esi.system.config", file(root, "data", "application.conf"));
        systemProperty("logback.configurationFile", file(root, "data", "logback.xml"));
        // Specify plugin directories
        Copy assemblePlugin = getProject().getTasks()
            .withType(Copy.class).getAt(MagicDrawPlugin.ASSEMBLE_PLUGIN_TASK_NAME);
        dependsOn(assemblePlugin);
        pluginDirs.add(file(root, "plugins"));
        pluginDirs.add(assemblePlugin.getDestinationDir().getParentFile());
        systemProperty("md.plugins.dir", stringify(pluginDirs));
    }
    
    private FileCollection buildClasspath() {
        JavaPluginConvention java = getProject().getConvention()
            .getPlugin(JavaPluginConvention.class);
        return java.getSourceSets().getAt(SourceSet.MAIN_SOURCE_SET_NAME)
            .getRuntimeClasspath();
    }
    
}
