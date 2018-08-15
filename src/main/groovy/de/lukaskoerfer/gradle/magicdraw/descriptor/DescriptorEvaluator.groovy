package de.lukaskoerfer.gradle.magicdraw.descriptor

import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project

/**
 * Evaluates MagicDraw plugin descriptors
 */
class DescriptorEvaluator {

    static def missing = {
        throw new InvalidUserDataException("Missing MagicDraw plugin descriptor property '$it'")
    }

    /**
     * Evaluates a MagicDraw plugin descriptor against a Gradle project
     */
    static void evaluate(MagicDrawPluginDescriptor descriptor, Project project) {
        descriptor.id = descriptor.id ?: project.group ?: missing('id')
        descriptor.version = descriptor.version ?: project.version ?: missing('version')
        descriptor.label = descriptor.label ?: missing('label')
        descriptor.className = descriptor.className ?: missing('className')
        descriptor.provider = descriptor.provider ?: System.getProperty('user.name')
    }

}
