package de.lukaskoerfer.gradle.magicdraw.descriptor

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project

@CompileStatic
@Immutable
class DescriptorEvaluation {

    private Project project

    private Descriptor descriptor

    void evaluate() {
        def missing = { throw new InvalidUserDataException("Missing plugin descriptor property $it") }
        def plugin = descriptor.plugin
        plugin << [
            id: plugin.id ?: project.group ?: missing('id'),
            name: plugin.name ?:  project.name ?: missing('name'),
            version: plugin.version ?: project.version ?: missing('version'),
            class: plugin.class ?: missing('class'),
            'provider-name': plugin.'provider-name' ?: System.getProperty('user.name')
        ]
    }

}
