package de.lukaskoerfer.gradle.magicdraw.extensions;

import de.lukaskoerfer.gradle.magicdraw.MagicDrawPlugin;
import lombok.Getter;
import lombok.Setter;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.FileTree;

import java.io.File;

import static de.lukaskoerfer.gradle.magicdraw.util.FileUtil.file;

/**
 * Provides a project extension for general MagicDraw settings
 */
public class MagicDrawExtension {
    
    private static final String INSTALL_DIR_PROPERTY = "magicDraw.installDir";
    
    /**
     * The MagicDraw installation directory
     * <p>
     * A default value can be set via the project project 'magicDraw.installDir'.
     */
    @Getter @Setter
    private File installDir;
    
    /**
     * Creates a new MagicDrawExtension
     * @param project The Gradle project instance
     */
    public MagicDrawExtension(Project project) {
        if (project.hasProperty(INSTALL_DIR_PROPERTY)) {
            installDir = project.file(project.property(INSTALL_DIR_PROPERTY));
        }
        project.afterEvaluate(this::afterEvaluate);
    }
    
    private void afterEvaluate(Project project) {
        if (installDir == null) {
            throw new InvalidUserDataException("Missing MagicDraw installation directory");
        }
        registerMagicDrawDependencies(project);
    }
    
    private void registerMagicDrawDependencies(Project project) {
        Configuration magicDrawConfiguration = project.getConfigurations()
            .getByName(MagicDrawPlugin.MAGICDRAW_CONFIGURATION);
        FileTree dependencies = project.fileTree(
            file(installDir, "lib"),
            tree -> tree.include("**/*.jar")
        );
        project.getDependencies().add(magicDrawConfiguration.getName(), dependencies);
    }
    
}
