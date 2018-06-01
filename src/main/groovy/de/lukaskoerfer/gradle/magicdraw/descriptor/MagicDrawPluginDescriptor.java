package de.lukaskoerfer.gradle.magicdraw.descriptor;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.gradle.api.Named;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores all necessary information about the MagicDraw info
 */
@Data
public class MagicDrawPluginDescriptor implements Named {
    
    /**
     * Internally used to distinguish different plugin descriptors
     * Do not mistake with label, which will be used as value for the 'name' property in the descriptor file
     */
    private final String name;
    
    /**
     *
     */
    private String label;
    
    /**
     *
     */
    private String id;
    
    /**
     *
     */
    private Object version;
    
    /**
     *
     */
    private String className;
    
    /**
     *
     */
    private String provider;
    
    /**
     * A list of paths to all required libraries
     */
    private final List<String> libraries = new ArrayList<>();
    
    /**
     * Any object describing the required MagicDraw API version
     */
    private Object requiredApi = 1.0;
    
    /**
     * A list of key-value-stores containing information on the required MagicDraw plugins
     */
    private final List<Map> requiredPlugins = new ArrayList<>();
    
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
