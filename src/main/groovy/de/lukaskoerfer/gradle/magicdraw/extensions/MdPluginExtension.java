package de.lukaskoerfer.gradle.magicdraw.extensions;

import de.lukaskoerfer.gradle.magicdraw.tasks.MdPluginDescriptor;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class MdPluginExtension {
    
    @Getter @Setter
    private String id;
    
    @Getter @Setter
    private String name;
    
    @Getter @Setter
    private Object version;
    
    @Getter @Setter
    private String provider;
    
    @Getter @Setter
    private String className;
    
    public MdPluginExtension(Project project) {
        project.afterEvaluate(this::afterEvaluate);
    }
    
    private void afterEvaluate(Project project) {
        // Groovy truth for strings
        Predicate<Object> stringTruth = val -> val != null && val.toString().length() > 0;
        // Validate plugin details
        id = Stream.of(id, project.getGroup())
            .filter(stringTruth)
            .findFirst()
            .orElse("unknown").toString();
        name = Stream.of(name, project.getName())
            .filter(stringTruth)
            .findFirst()
            .orElseThrow(InvalidUserDataException::new);
        version = Stream.of(version, project.getVersion())
            .filter(stringTruth)
            .findFirst()
            .orElseThrow(InvalidUserDataException::new);
        provider = Optional.of(provider)
            .filter(stringTruth)
            .orElse(System.getProperty("user.name"));
        Optional.of(className)
            .filter(stringTruth)
            .orElseThrow(InvalidUserDataException::new);
        // Apply details to descriptor task
        val pluginDescriptor = project.getTasks()
            .withType(MdPluginDescriptor.class).getAt("pluginDescriptor");
        val plugin = pluginDescriptor.getPlugin();
        plugin.putIfAbsent("id", id);
        plugin.putIfAbsent("name", name);
        plugin.putIfAbsent("version", version);
        plugin.putIfAbsent("class", className);
        plugin.putIfAbsent("provider-name", provider);
    }

}
