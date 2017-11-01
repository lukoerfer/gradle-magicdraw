package de.lukaskoerfer.gradle.magicdraw.tasks

import groovy.xml.MarkupBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class PluginDescriptor extends DefaultTask {

    @OutputFile
    File file = new File(temporaryDir, 'plugin.xml')

    Map<String, ?> plugin = [
        id: project.group ?: 'unknown',
        name: project.name,
        version: project.version,
        class: null,
        'provider-name': System.getProperty('user.name')
    ]

    Object requiredApi = 1.0

    List<Map<String, ?>> requiredPlugins = []

    List<String> libraries = []

    @TaskAction
    @SuppressWarnings("GroovyUnusedDeclaration")
    void run() {
        // Check properties
        if (!plugin.get('class'))
            throw new InvalidUserDataException("Unspecified plugin class")
        // Configure XML generation
        def xml = new MarkupBuilder(file.newWriter())
        xml.with {
            doubleQuotes = true
            omitEmptyAttributes = true
            omitNullAttributes = true
            mkp.xmlDeclaration(version: '1.0', encoding: 'utf-8')
        }
        // Build XML
        xml.plugin(plugin) {
            requires {
                api(version: requiredApi)
                requiredPlugins.each { props ->
                    'required-plugin'(props)
                }
            }
            runtime {
                libraries.each { path ->
                    library(name: path)
                }
            }
        }
    }
}
