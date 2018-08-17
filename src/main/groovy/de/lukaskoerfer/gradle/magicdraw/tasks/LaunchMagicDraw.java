package de.lukaskoerfer.gradle.magicdraw.tasks;

import de.lukaskoerfer.gradle.magicdraw.MagicDrawPlugin;
import de.lukaskoerfer.gradle.magicdraw.extensions.MagicDrawExtension;
import lombok.Getter;
import lombok.Setter;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.JavaExec;

/**
 * Provides a task type to launch a MagicDraw instance
 */
public class LaunchMagicDraw extends JavaExec {
    
    /**
     * -- GETTER --
     * Gets whether MagicDraw should be launched in verbose mode (more detailed console output).
     * <p>Defaults to true</p>
     * @return The previously set value or true, if it has not been set
     * -- SETTER --
     * Sets whether MagicDraw should be launched in verbose mode (more detailed console output).
     * <p>Defaults to true</p>
     * @param verbose True to launch in verbose mode, false otherwise
     */
    @Getter @Setter
    private boolean verbose = true;
    
    /**
     * -- GETTER --
     * Gets whether MagicDraw should be launched with a local configuration file.
     * <p>Defaults to true</p>
     * @return The previously set value or true, if it has not been set
     * -- SETTER --
     * Sets whether MagicDraw should be launched with a local configuration file.
     * <p>Defaults to true</p>
     * @param localConfig True to launch in with a local configuration file, false otherwise
     */
    @Getter @Setter
    private boolean localConfig = true;
    
    /**
     * Gets the list of directories where MagicDraw should search for plugins.
     * @return A modifiable and lazy-evaluated file collection
     */
    @Getter
    private ConfigurableFileCollection pluginDirs = getProject().files();
    
    /**
     * Creates a new MagicDraw launch task
     */
    public LaunchMagicDraw() {
        setClasspath(getProject().getConfigurations()
            .getByName(MagicDrawPlugin.MAGICDRAW_CONFIGURATION));
        setMain("com.nomagic.osgi.launcher.ProductionFrameworkLauncher");
        setMinHeapSize("60M");
        setMaxHeapSize("200M");
        jvmArgs("-Xss1024K", "-Xmx2000M");
        systemProperty("md.class.path", "$java.class.path");
        MagicDrawExtension magicDraw = getProject().getExtensions().getByType(MagicDrawExtension.class);
        pluginDirs.from(magicDraw.getInstallDir().dir("plugins"));
        getProject().afterEvaluate(project -> evaluate());
    }
    
    private void evaluate() {
        DirectoryProperty installDir = getProject().getExtensions()
            .getByType(MagicDrawExtension.class).getInstallDir();
        // Specify configuration properties
        if (verbose) args("-verbose");
        systemProperty("localConfig", localConfig);
        systemProperty("com.nomagic.osgi.config.dir", installDir
            .dir("configuration").get().getAsFile());
        systemProperty("esi.system.config", installDir
            .file("data/application.conf").get().getAsFile());
        systemProperty("logback.configurationFile", installDir
            .file("data/logback.xml").get().getAsFile());
        // Setup plugin directories
        setupPluginDirs();
    }
    
    private void setupPluginDirs() {
        AssembleMagicDrawPlugin assemblePlugin = getProject().getTasks()
            .withType(AssembleMagicDrawPlugin.class).getAt(MagicDrawPlugin.ASSEMBLE_PLUGIN_TASK);
        dependsOn(assemblePlugin);
        pluginDirs.from(assemblePlugin.getDestinationDir());
        systemProperty("md.plugins.dir", pluginDirs.getAsPath());
    }
    
}
