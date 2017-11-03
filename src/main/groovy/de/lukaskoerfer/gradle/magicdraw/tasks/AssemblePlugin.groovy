package de.lukaskoerfer.gradle.magicdraw.tasks

import groovy.xml.MarkupBuilder
import org.gradle.api.InvalidUserDataException
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.OutputFile

class AssemblePlugin extends Copy {

    @OutputFile
    File file

    Map<String, Object> plugin = [:]

    def requiredApi = 1.0

    List<Map> requiredPlugins = []

    AssemblePlugin() {
        project.afterEvaluate {
            // Validate model
            def missing = { throw new InvalidUserDataException("Missing plugin property $it") }
            plugin << [
                id: plugin.id ?: project.group ?: missing('id'),
                name: plugin.name ?:  project.name ?: missing('name'),
                version: plugin.version ?: project.version ?: missing('version'),
                class: plugin.class ?: missing('class'),
                'provider-name': plugin.'provider-name' ?: System.getProperty('user.name')
            ]
            // Define descriptor file
            file = new File(destinationDir, 'plugin.xml')
        }
        // Collect all copied files
        def libraries = []
        eachFile {
            libraries << it.path
        }
        // Create descriptor file
        doLast {
            // Setup XML writer
            def xml = new MarkupBuilder(file.newWriter())
            xml.with {
                doubleQuotes = true
                omitEmptyAttributes = true
                omitNullAttributes = true
            }
            // Write XML
            xml.mkp.xmlDeclaration(version: '1.0', encoding: 'utf-8')
            xml.with {
                'plugin'(plugin) {
                    'requires' {
                        'api'('version': requiredApi)
                        requiredPlugins.each {
                            'required-plugin'(it)
                        }
                    }
                    'runtime' {
                        libraries.each {
                            'library'(name: it)
                        }
                    }
                }
            }
        }
    }
}
