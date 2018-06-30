package de.lukaskoerfer.gradle.magicdraw.extensions;

import de.lukaskoerfer.gradle.magicdraw.descriptor.MagicDrawPluginDescriptor;
import lombok.Getter;
import org.gradle.api.plugins.ExtensionAware;

public class SinglePluginDescriptorContainer {

    @Getter
    private MagicDrawPluginDescriptor main;
    
    public SinglePluginDescriptorContainer() {
        main = asExtensionAware().getExtensions().create("main", MagicDrawPluginDescriptor.class, "main");
    }
    
    private ExtensionAware asExtensionAware() {
        return (ExtensionAware) this;
    }

}
