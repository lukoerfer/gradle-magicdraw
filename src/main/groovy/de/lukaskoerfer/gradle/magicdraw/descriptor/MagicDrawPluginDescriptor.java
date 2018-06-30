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
     * Gets the name of the plugin descriptor. It is used internally to distinguish the plugins.
     * <br><br>
     * <b>Please note:</b> Do not mistake with the label, which will be used as value for the <i>name</i> property in the descriptor file.
     */
    @Getter
    private final String name;
    
    /**
     * -- GETTER --
     * Gets the plugin label
     * -- SETTER --
     * Sets the plugin label
     */
    @Getter @Setter
    private String label;
    
    /**
     *
     */
    @Getter @Setter
    private String id;
    
    /**
     *
     */
    @Getter @Setter
    private Object version;
    
    /**
     *
     */
    @Getter @Setter
    private String className;
    
    /**
     * -- GETTER --
     * Gets the plugin provider
     * <br><br>
     * It will be mapped to the field <i>provider-name</i> of the plugin descriptor.
     * @return Defaults to the system property <i>user.name</i>
     * -- SETTER --
     * Sets the plugin provider
     * <br><br>
     * It will be mapped to the <i>provider-name</i> property of the plugin descriptor.
     * @param provider Any string
     */
    @Getter @Setter
    private String provider;
    
    /**
     * -- GETTER --
     * Provides a writable list of relative paths to the required libraries
     * @return Defaults to an empty list.
     */
    @Getter
    private final List<String> libraries = new ArrayList<>();
    
    /**
     * -- GETTER --
     * Gets the object describing the required MagicDraw API version
     * @return Defaults to version <i>1.0</i>
     * -- SETTER --
     * Sets the object describing the required MagicDraw API version
     * @param requiredApi Any object can be passed. The version will be evaluated via the <code>toString()</code> method.
     */
    @Getter @Setter
    private Object requiredApi = 1.0;
    
    /**
     * -- GETTER --
     * Provides a writable list of maps to provide information about required MagicDraw plugins
     * <br><br>
     * The passed maps will be put directly into the XML presentation of the plugin descriptor.
     * @return Defaults to an empty list.
     */
    @Getter
    private final List<Map<?,?>> requiredPlugins = new ArrayList<>();
    
    /**
     * Creates a map providing the descriptor properties <i>id</i>, <i>name</i>, <i>version</i>, <i>class</i> and <i>provider-name</i>
     * @return A simple map with the listed descriptor properties
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
