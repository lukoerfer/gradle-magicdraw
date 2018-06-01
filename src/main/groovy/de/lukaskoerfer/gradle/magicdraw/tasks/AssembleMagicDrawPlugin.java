package de.lukaskoerfer.gradle.magicdraw.tasks;

import de.lukaskoerfer.gradle.magicdraw.MagicDrawPlugin;
import de.lukaskoerfer.gradle.magicdraw.descriptor.MagicDrawPluginDescriptor;
import de.lukaskoerfer.gradle.magicdraw.descriptor.DescriptorEvaluator;
import de.lukaskoerfer.gradle.magicdraw.descriptor.DescriptorMarkup;
import de.lukaskoerfer.gradle.magicdraw.extensions.MagicDrawExtension;
import groovy.lang.GroovyCallable;
import groovy.xml.MarkupBuilder;
import lombok.SneakyThrows;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.Sync;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import static de.lukaskoerfer.gradle.magicdraw.util.FileUtil.file;

/**
 * Provides a task type to assemble all components of a MagicDraw plugin and create the descriptor file
 */
public class AssembleMagicDrawPlugin extends Sync {
    
    /**
     * Provides the descriptor file
     */
    @OutputFile
    File file;
    
    private final MagicDrawPluginDescriptor descriptor;
    
    /**
     * Creates a new MagicDraw assemble task
     */
    public AssembleMagicDrawPlugin() {
        descriptor = getDescriptor();
        setupSync();
        getInputs().file(getProject().getBuildFile());
        getProject().afterEvaluate(project -> {
            DescriptorEvaluator.evaluate(descriptor, getProject());
            file = new File(getDestinationDir(), "plugin.xml");
        });
        doLast(task -> writeDescriptorFile());
    }
    
    private MagicDrawPluginDescriptor getDescriptor() {
        return getProject().getExtensions()
            .getByType(MagicDrawExtension.class)
            .getPlugins().maybeCreate("plugin");
    }
    
    private void setupSync() {
        GroovyCallable assembleTarget = () ->
            file(getProject().getBuildDir(), "magicDraw", descriptor.getInfo().get("id"));
        into(assembleTarget);
        Configuration magicDrawApi = getProject().getConfigurations()
            .getAt(MagicDrawPlugin.MAGICDRAW_CONFIGURATION);
        exclude(element -> magicDrawApi.contains(element.getFile()));
        eachFile(file -> descriptor.getLibraries().add(file.getPath()));
    }
    
    @SneakyThrows(IOException.class)
    private void writeDescriptorFile() {
        Writer writer = new PrintWriter(file);
        MarkupBuilder xml = new MarkupBuilder(writer);
        DescriptorMarkup.write(descriptor, xml);
        writer.close();
    }
    

}
