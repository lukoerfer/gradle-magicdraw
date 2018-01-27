package de.lukaskoerfer.gradle.magicdraw.descriptor;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Descriptor {
    
    /**
     * Plugin meta data
     */
    private final Map<String, Object> plugin = new HashMap<>();
    
    /**
     *
     */
    private final List<String> libraries = new ArrayList<>();
    
    /**
     * Required MagicDraw API version
     */
    private Object requiredApi = 1.0;
    
    /**
     * Required MagicDraw plugins
     */
    private final List<Map> requiredPlugins = new ArrayList<>();
    
}
