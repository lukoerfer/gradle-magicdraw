package de.lukaskoerfer.gradle.magicdraw.extensions;

import lombok.Getter;
import lombok.Setter;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.FileTree;

import java.io.File;
import java.util.Optional;

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

    public MagicDrawExtension(Project project) {
        if (project.hasProperty(INSTALL_DIR_PROPERTY)) {
            installDir = project.file(project.property(INSTALL_DIR_PROPERTY));
        }
        project.afterEvaluate(this::afterEvaluate);
    }
    
    private void afterEvaluate(Project project) {
        // Validate extension
        Optional.ofNullable(installDir)
            .orElseThrow(InvalidUserDataException::new);
        // Register dependencies
        Configuration mdCompile = project.getConfigurations()
            .getByName("mdCompile");
        FileTree dependencies = project.fileTree(
            file(installDir, "lib"),
            tree -> tree.include("**/*.jar")
        );
        project.getDependencies().add(mdCompile.getName(), dependencies);
    }
    
}
