package de.lukaskoerfer.gradle.magicdraw.descriptor

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import groovy.xml.MarkupBuilder

/**
 * Writes a MagicDraw plugin descriptor to an XML file
 */
@CompileStatic
@Immutable
class DescriptorMarkup {

    private Descriptor descriptor

    /**
     * Writes the plugin descriptor to the given markup builder
     * @param xml A XML markup builder
     */
    void writeTo(MarkupBuilder xml) {
        // Setup markup builder
        xml.with {
            doubleQuotes = true
            omitEmptyAttributes = true
            omitNullAttributes = true
        }
        // Write XML
        xml.mkp.xmlDeclaration(version: '1.0', encoding: 'utf-8')
        xml.with {
            'plugin'(descriptor.plugin) {
                'requires' {
                    'api'('version': descriptor.requiredApi)
                    descriptor.requiredPlugins.each { plugin ->
                        'required-plugin'(plugin)
                    }
                }
                'runtime' {
                    descriptor.libraries.each { path ->
                        'library'(name: path)
                    }
                }
            }
        }
    }

}
