package de.lukaskoerfer.gradle.magicdraw;

import lombok.Getter;
import lombok.Setter;

class MdPluginExtension {
    
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

}
