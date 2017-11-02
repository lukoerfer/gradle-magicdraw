package de.lukaskoerfer.gradle.magicdraw.extensions;

import lombok.Getter;
import lombok.Setter;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;

import java.io.File;
import java.util.Optional;

public class MagicDrawExtension {
    
    @Getter @Setter
    private File installDir;

    public MagicDrawExtension(Project project) {
        if (project.hasProperty("magicDraw.installDir")) {
            installDir = project.file(project.property("magicDraw.installDir"));
        }
        project.afterEvaluate(this::afterEvaluate);
    }
    
    private void afterEvaluate(Project project) {
        Optional.ofNullable(installDir)
            .orElseThrow(InvalidUserDataException::new);
    }
    
}
