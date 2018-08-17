package de.lukaskoerfer.gradle.magicdraw.tasks;

import de.lukaskoerfer.gradle.magicdraw.descriptor.DescriptorEvaluator;
import de.lukaskoerfer.gradle.magicdraw.descriptor.DescriptorMarkup;
import de.lukaskoerfer.gradle.magicdraw.descriptor.MagicDrawPluginDescriptor;
import de.lukaskoerfer.gradle.magicdraw.extensions.MagicDrawExtension;
import groovy.xml.MarkupBuilder;
import lombok.SneakyThrows;
import org.gradle.api.tasks.Sync;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * Provides a task type to assemble all components of a MagicDraw plugin and create the descriptor file
 */
public class AssembleMagicDrawPlugin extends Sync {
    
    private final MagicDrawPluginDescriptor descriptor = getProject()
        .getExtensions().getByType(MagicDrawExtension.class)
        .getPlugins().getMain();
    
    private File descriptorFile;
    
    /**
     * Creates a new MagicDraw assemble task
     */
    public AssembleMagicDrawPlugin() {
        getInputs().file(getProject().getBuildFile());
        setupAssemble();
        setupDescriptor();
    }
    
    private void setupAssemble() {
        into(getProject().getLayout().getBuildDirectory().dir("magicdraw"));
        eachFile(file -> {
            String path = file.getPath();
            descriptor.getLibraries().add(path);
            file.setPath(descriptor.getId() + "/" + path);
        });
    }
    
    private void setupDescriptor() {
        getProject().afterEvaluate(project -> {
            DescriptorEvaluator.evaluate(descriptor, project);
            descriptorFile = new File(getDestinationDir(), descriptor.getId() + "/plugin.xml");
            getOutputs().file(descriptorFile);
        });
        doLast(task -> writeDescriptor());
    }
    
    @SneakyThrows(IOException.class)
    private void writeDescriptor() {
        Writer writer = new PrintWriter(descriptorFile);
        MarkupBuilder xml = new MarkupBuilder(writer);
        DescriptorMarkup.write(descriptor, xml);
        writer.close();
    }
    

}
