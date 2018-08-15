package de.lukaskoerfer.gradle.magicdraw.descriptor

import groovy.xml.MarkupBuilder

/**
 * Writes MagicDraw plugin descriptors to XML files
 */
class DescriptorMarkup {

    /**
     * Writes a plugin descriptor to a XML markup builder
     * @param descriptor A MagicDraw plugin descriptor
     * @param xml A XML markup builder
     */
    static void write(MagicDrawPluginDescriptor descriptor, MarkupBuilder xml) {
        // Setup markup builder
        xml.with {
            doubleQuotes = true
            omitEmptyAttributes = true
            omitNullAttributes = true
        }
        // Write XML
        xml.mkp.xmlDeclaration(version: '1.0', encoding: 'utf-8')
        xml.with {
            'plugin'(descriptor.info) {
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
