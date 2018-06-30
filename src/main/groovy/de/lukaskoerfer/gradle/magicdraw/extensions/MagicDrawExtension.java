package de.lukaskoerfer.gradle.magicdraw.extensions;

import de.lukaskoerfer.gradle.magicdraw.MagicDrawPlugin;
import lombok.Getter;
import lombok.Setter;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileTree;
import org.gradle.api.plugins.ExtensionAware;

/**
 * Provides a project extension for general MagicDraw settings
 */
public class MagicDrawExtension {
    
    private static final String INSTALL_DIR_PROPERTY = "magicDraw.installDir";
    
    /**
     * The MagicDraw installation directory
     * <p>
     * A default value can be set via the project property 'magicDraw.installDir'.
     */
    @Getter @Setter
    private DirectoryProperty installDir;
    
    /**
     *
     */
    @Getter
    private final SinglePluginDescriptorContainer plugins;
    
    /**
     * Creates a new MagicDrawExtension
     * @param project The Gradle project instance
     */
    public MagicDrawExtension(Project project) {
        plugins = asExtensionAware().getExtensions().create("plugins", SinglePluginDescriptorContainer.class);
        installDir = project.getLayout().directoryProperty();
        if (project.hasProperty(INSTALL_DIR_PROPERTY)) {
            installDir.set(project.file(project.property(INSTALL_DIR_PROPERTY)));
        }
        project.afterEvaluate(this::registerMagicDrawDependencies);
    }
    
    private void registerMagicDrawDependencies(Project project) {
        if (installDir == null) {
            throw new InvalidUserDataException("Missing MagicDraw installation directory");
        }
        FileTree dependencies = project.fileTree(installDir.dir("lib"), tree -> tree.include("**/*.jar"));
        project.getDependencies().add(MagicDrawPlugin.MAGICDRAW_CONFIGURATION, dependencies);
    }
    
    private ExtensionAware asExtensionAware() {
        return (ExtensionAware) this;
    }
    
}
