package de.lukaskoerfer.gradle.magicdraw.tasks;

import de.lukaskoerfer.gradle.magicdraw.extensions.MagicDrawExtension;
import lombok.Getter;
import lombok.Setter;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.JavaExec;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static de.lukaskoerfer.gradle.magicdraw.util.FileUtil.*;

public class Launch extends JavaExec {
    
    @Getter @Setter
    private boolean verbose = true;
    
    @Getter @Setter
    private boolean localConfig = true;
    
    @Getter @Setter
    private List<File> pluginDirs = new ArrayList<>();
    
    public Launch() {
        Project project = getProject();
        JavaPluginConvention java = project.getConvention()
            .getPlugin(JavaPluginConvention.class);
        // Set default values for process
        setClasspath(java.getSourceSets().getAt("main").getRuntimeClasspath());
        setMain("com.nomagic.osgi.launcher.ProductionFrameworkLauncher");
        setMinHeapSize("60M");
        setMaxHeapSize("200M");
        jvmArgs("-Xss1024K", "-Xmx2000M");
        systemProperty("md.class.path", "$java.class.path");
        // Apply evaluation
        project.afterEvaluate(this::afterEvaluate);
    }
    
    private void afterEvaluate(Project project) {
        MagicDrawExtension magicDraw = project.getExtensions()
            .getByType(MagicDrawExtension.class);
        File root = magicDraw.getInstallDir();
        // Specify configuration properties
        if (verbose) args("-verbose");
        systemProperty("localConfig", localConfig);
        systemProperty("com.nomagic.osgi.config.dir", file(root, "configuration"));
        systemProperty("esi.system.config", file(root, "data", "application.conf"));
        systemProperty("logback.configurationFile", file(root, "data", "logback.xml"));
        // Specify plugin directories
        Copy assemblePlugin = project.getTasks()
            .withType(Copy.class).getAt("assemblePlugin");
        dependsOn(assemblePlugin);
        pluginDirs.add(assemblePlugin.getDestinationDir().getParentFile());
        pluginDirs.add(file(root, "plugins"));
        systemProperty("md.plugins.dir", stringify(pluginDirs));
    }
    
}
