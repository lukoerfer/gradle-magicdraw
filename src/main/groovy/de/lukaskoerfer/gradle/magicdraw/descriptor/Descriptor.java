package de.lukaskoerfer.gradle.magicdraw.descriptor;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores all necessary information about the MagicDraw plugin
 */
@Data
public class Descriptor {
    
    /**
     * A key-value-store containing basic plugin information (id, name, version, implementation class)
     */
    private final Map<String, Object> plugin = new HashMap<>();
    
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
    
}
