package de.lukaskoerfer.gradle.magicdraw.tasks;

import de.lukaskoerfer.gradle.magicdraw.descriptor.Descriptor;
import de.lukaskoerfer.gradle.magicdraw.descriptor.DescriptorEvaluation;
import de.lukaskoerfer.gradle.magicdraw.descriptor.DescriptorMarkup;
import groovy.xml.MarkupBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.Sync;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * Task type to assemble all components of a MagicDraw plugin and create the descriptor file
 */
public class AssembleMagicDrawPlugin extends Sync {
    
    @OutputFile
    File file;
    
    @Getter
    private Descriptor descriptor = new Descriptor();
    
    public AssembleMagicDrawPlugin() {
        preSetup();
        getProject().afterEvaluate($ -> finalSetup());
    }
    
    private void preSetup() {
        getConvention().add("descriptor", descriptor);
        getInputs().file(getProject().getBuildFile());
        eachFile(file -> descriptor.getLibraries().add(file.getPath()));
        doLast($ -> createDescriptorFile());
    }
    
    private void finalSetup() {
        new DescriptorEvaluation(getProject(), descriptor).evaluate();
        file = new File(getDestinationDir(), "plugin.xml");
    }
    
    @SneakyThrows(IOException.class)
    private void createDescriptorFile() {
        Writer writer = new PrintWriter(file);
        MarkupBuilder xml = new MarkupBuilder(writer);
        new DescriptorMarkup(descriptor).writeTo(xml);
        writer.close();
    }
    

}
