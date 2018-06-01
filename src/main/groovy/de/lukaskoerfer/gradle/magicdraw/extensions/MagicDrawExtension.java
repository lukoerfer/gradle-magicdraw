package de.lukaskoerfer.gradle.magicdraw.extensions;

import de.lukaskoerfer.gradle.magicdraw.MagicDrawPlugin;
import de.lukaskoerfer.gradle.magicdraw.descriptor.MagicDrawPluginDescriptor;
import de.lukaskoerfer.gradle.magicdraw.tasks.AssembleMagicDrawPlugin;
import groovy.lang.Closure;
import lombok.Getter;
import lombok.Setter;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.FileTree;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.tasks.TaskCollection;

import java.io.File;

import static de.lukaskoerfer.gradle.magicdraw.util.FileUtil.file;

/**
 * Provides a project extension for general MagicDraw settings
 */
public class MagicDrawExtension {
    
    private static final String INSTALL_DIR_PROPERTY = "magicDraw.installDir";
    
    private final Project project;
    
    /**
     * The MagicDraw installation directory
     * <p>
     * A default value can be set via the project project 'magicDraw.installDir'.
     */
    @Getter @Setter
    private File installDir;
    
    /**
     *
     */
    @Getter
    private final NamedDomainObjectContainer<MagicDrawPluginDescriptor> plugins;
    
    /**
     * Creates a new MagicDrawExtension
     * @param project The Gradle project instance
     */
    public MagicDrawExtension(Project project) {
        this.project = project;
        plugins = project.container(MagicDrawPluginDescriptor.class);
        asDynamic().getExtensions().add("plugins", plugins);
        if (project.hasProperty(INSTALL_DIR_PROPERTY)) {
            installDir = project.file(project.property(INSTALL_DIR_PROPERTY));
        }
        project.afterEvaluate(p -> {
            if (installDir == null) {
                throw new InvalidUserDataException("Missing MagicDraw installation directory");
            }
            registerMagicDrawDependencies();
        });
    }
    
    public TaskCollection<AssembleMagicDrawPlugin> all() {
        return project.getTasks().withType(AssembleMagicDrawPlugin.class);
    }
    
    private void registerMagicDrawDependencies() {
        Configuration magicDrawConfiguration = project.getConfigurations()
            .getByName(MagicDrawPlugin.MAGICDRAW_CONFIGURATION);
        FileTree dependencies = project.fileTree(
            file(installDir, "lib"),
            tree -> tree.include("**/*.jar")
        );
        project.getDependencies().add(magicDrawConfiguration.getName(), dependencies);
    }
    
    private ExtensionAware asDynamic() {
        return (ExtensionAware) this;
    }
    
}
