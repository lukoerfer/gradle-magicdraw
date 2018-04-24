package de.lukaskoerfer.gradle.magicdraw.descriptor

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project

/**
 * Evaluates a MagicDraw plugin descriptor
 */
@CompileStatic
@Immutable
class DescriptorEvaluation {

    private Project project

    private Descriptor descriptor

    /**
     * Executes the evaluation of the plugin descriptor
     */
    void evaluate() {
        def missing = {
            throw new InvalidUserDataException("Missing plugin descriptor property $it")
        }
        descriptor.plugin << [
            id: descriptor.plugin.id ?: project.group ?: missing('id'),
            name: descriptor.plugin.name ?:  project.name ?: missing('name'),
            version: descriptor.plugin.version ?: project.version ?: missing('version'),
            class: descriptor.plugin.class ?: missing('class'),
            'provider-name': descriptor.plugin.'provider-name' ?: System.getProperty('user.name')
        ]
    }

}
