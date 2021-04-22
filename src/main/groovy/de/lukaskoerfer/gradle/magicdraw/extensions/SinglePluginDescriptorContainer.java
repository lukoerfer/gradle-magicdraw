package de.lukaskoerfer.gradle.magicdraw.extensions;

import de.lukaskoerfer.gradle.magicdraw.descriptor.MagicDrawPluginDescriptor;
import lombok.Getter;
import org.gradle.api.plugins.ExtensionAware;

/**
 * Provides a container for MagicDraw plugin descriptors that only stores a single plugin called <i>main</i>
 */
public class SinglePluginDescriptorContainer {
    
    /**
     * -- GETTER --
     * Gets the only MagicDraw plugin descriptor provided by this container.
     * @return A MagicDraw plugin descriptor with the internal name <i>main</i>
     */
    @Getter
    private final MagicDrawPluginDescriptor main;
    
    /**
     * Creates a new MagicDraw plugin descriptor container that only stores a single plugin descriptor called <i>main</i>.
     * <p>The MagicDraw plugin descriptor called <i>main</i> will be directly accessible.</p>
     */
    public SinglePluginDescriptorContainer() {
        main = asExtensionAware().getExtensions().create("main", MagicDrawPluginDescriptor.class, "main");
    }
    
    private ExtensionAware asExtensionAware() {
        return (ExtensionAware) this;
    }

}
