package de.lukaskoerfer.gradle.magicdraw.descriptor;

import lombok.*;
import org.gradle.api.Named;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores all necessary information about a MagicDraw plugin
 */
@RequiredArgsConstructor
public class MagicDrawPluginDescriptor implements Named {
    
    /**
     * -- GETTER --
     * Gets the name of the plugin descriptor.
     * <p>Only used internally to distinguish the plugins. <b>Please note:</b> Do not mistake with <b>label</b>, which will be put as value for the <i>name</i> property in the descriptor file instead.</p>
     * @return A string that identifies the plugin during build time.
     */
    @Getter
    private final String name;
    
    /**
     * -- GETTER --
     * Gets the plugin label.
     * <p>This will be used as value for the <i>name</i> property in the descriptor file.
     * It is necessary to set this value, otherwise the plugin will fail.</p>
     * @return The previously set plugin label or null, if it has not been set.
     * -- SETTER --
     * Sets the plugin label
     * <p>This will be used as value for the <i>name</i> property in the descriptor file.</p>
     * @param label Any string
     */
    @Getter @Setter
    private String label;
    
    /**
     * -- GETTER --
     * Gets the plugin id.
     * <p>This will be used as value for the <i>id</i> property in the descriptor file.
     * If not set, the value of <code>project.group</code> will be used.</p>
     * @return The previously set plugin id or null, if it has not been set.
     * -- SETTER --
     * Sets the plugin id.
     * <p>This will be used as value for the <i>id</i> property in the descriptor file.</p>
     * @param id Any string
     */
    @Getter @Setter
    private String id;
    
    /**
     * -- GETTER --
     * Gets the plugin version.
     * <p>This will be used as value for the <i>version</i> property in the descriptor file.
     * If not set, the value of <code>project.version</code> will be used.</p>
     * @return The previously set plugin version or null, if it has not been set.
     * -- SETTER --
     * Sets the plugin version.
     * <p>This will be used as value for the <i>version</i> property in the descriptor file.</p>
     * @param version Any object can be passed. The version will be evaluated via the <code>toString()</code> method.
     */
    @Getter @Setter
    private Object version;
    
    /**
     * -- GETTER --
     * Gets the plugin implementation class.
     * <p>This will be used as value for the <i>class</i> property in the descriptor file.
     * It is necessary to set this value, otherwise the plugin will fail.</p>
     * @return The previously set plugin implementation class or null, if it has not been set.
     * -- SETTER --
     * Sets the plugin implementation class.
     * <p>This will be used as value for the <i>class</i> property in the descriptor file.</p>
     * @param className The full name (including the package) of a class
     */
    @Getter @Setter
    private String className;
    
    /**
     * -- GETTER --
     * Gets the plugin provider.
     * <p>This will be used as value for the <i>provider-name</i> property in the descriptor file.
     * If not set, the system property <code>user.name</code> will be used.</p>
     * @return The previously set provider name or null, if it has not been set.
     * -- SETTER --
     * Sets the plugin provider.
     * <p>This will be used as value for the <i>provider-name</i> property in the descriptor file.</p>
     * @param provider Any string
     */
    @Getter @Setter
    private String provider;
    
    /**
     * Provides a modifiable list of relative paths for the required libraries of the plugin.
     * <p>The plugin will fill this list on its own during the assembly of the plugin.
     * It is possible to add additional libraries, which are not processed by Gradle, beforehand.</p>
     * @return A modifiable list
     */
    @Getter
    private final List<String> libraries = new ArrayList<>();
    
    /**
     * -- GETTER --
     * Gets the required MagicDraw API version.
     * <p>Defaults to version 1.0</p>
     * @return The previously set version object or the default value, if it has not been set
     * -- SETTER --
     * Sets the required MagicDraw API version.
     * <p>Defaults to version 1.0</p>
     * @param requiredApi Any object can be passed. The version will be evaluated via the <code>toString()</code> method.
     */
    @Getter @Setter
    private Object requiredApi = 1.0;
    
    /**
     * Provides a modifiable list of maps to provide information about required MagicDraw plugins.
     * <p>Defaults to an empty list. The passed maps will be put directly into the XML representation of the plugin descriptor.</p>
     * @return A modifiable list
     */
    @Getter
    private final List<Map<?,?>> requiredPlugins = new ArrayList<>();
    
    /**
     * Creates a map providing the major descriptor properties.
     * <p>The major properties are <i>id</i>, <i>name</i>, <i>version</i>, <i>class</i> and <i>provider-name</i>.</p>
     * @return A map with the listed descriptor properties
     */
    public Map<String, Object> getInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("id", id);
        info.put("name", label);
        info.put("version", version);
        info.put("class", className);
        info.put("provider-name", provider);
        return info;
    }
    
}
