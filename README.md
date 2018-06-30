# gradle-magicdraw
Gradle plugin to develop plugins for MagicDraw

## Motivation
The MagicDraw developer guide provides manuals to develop plugins for MagicDraw with either Eclipse or IntelliJ IDEA.
Using build tools like Maven or Gradle is not supported, even if it can be considered best practice nowadays.
This plugin tries to provide a set of tools to simplify the development of plugins for MagicDraw with Gradle.

## Download
The plugin is available via the [Gradle plugin portal](https://plugins.gradle.org/plugin/de.lukaskoerfer.gradle.magicdraw). Simply use the `plugins` block to apply the plugin to your project:

    plugins {
        id 'de.lukaskoerfer.gradle.magicdraw' version '0.1'
    }

The plugin does not require any other plugin. No other plugin will be applied automatically.

## Usage
Once the plugin is applied, it can be configured via the `magicDraw` closure:

    magicDraw {
        installDir = file('C:/MagicDraw')
        plugins {
            main {
                id = 'my.plugin.id'
                version = '1.2.3'
                label = 'My plugin'
                className = 'package.path.to.PluginClass'
                provider = 'John Doe'
            }
        }
    }

The most important configuration parameter is called `installDir` and must be set to the installation directory of MagicDraw.

> It is also possible to use the project property `magicDraw.installDir` to set the installation directory from the user-specific `gradle.properties` file.

## License
The software is licensed under the [MIT license](https://github.com/lukoerfer/gradle-magicdraw/blob/master/LICENSE).
