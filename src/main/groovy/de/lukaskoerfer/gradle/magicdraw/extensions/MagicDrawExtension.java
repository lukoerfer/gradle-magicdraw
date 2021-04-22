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
     * -- GETTER --
     * Gets the MagicDraw installation directory.
     * <p>A default value can be set via the project property <code>magicDraw.installDir</code>.
     * It is <b>required</b> to specify this property either directly or via the project property.
     * If this property has not been set until the end of the <i>configuration phase</i>, the build will fail.</p>
     * @return The previously set installation directory or the default value or null, if neither has been set
     * -- SETTER --
     * Sets the MagicDraw installation directory.
     * <p>A default value can be set via the project property <code>magicDraw.installDir</code>.
     * It is <b>required</b> to specify this property either directly or via the project property.
     * If this property has not been set until the end of the <i>configuration phase</i>, the build will fail.</p>
     * @param installDir The base directory of the MagicDraw installation
     */
    @Getter @Setter
    private DirectoryProperty installDir;
    
    /**
     * Gets a container for MagicDraw plugin descriptors
     * @return A MagicDraw plugin descriptor container
     */
    @Getter
    private final SinglePluginDescriptorContainer plugins;
    
    /**
     * Creates a new extension for MagicDraw plugin settings
     * @param project The Gradle project instance
     */
    public MagicDrawExtension(Project project) {
        plugins = asExtensionAware().getExtensions().create("plugins", SinglePluginDescriptorContainer.class);
        installDir = project.getObjects().directoryProperty();
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
