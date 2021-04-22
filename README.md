# Gradle MagicDraw Plugin
Gradle plugin to develop plugins for MagicDraw

## Motivation
The MagicDraw developer guide provides manuals to develop plugins for MagicDraw with either Eclipse or IntelliJ IDEA.
Using build tools like Maven or Gradle is not supported, even if it can be considered best practice nowadays.
This plugin tries to provide a set of tools to simplify the development of plugins for MagicDraw with Gradle.

## Download
The plugin is available via the [Gradle plugin portal](https://plugins.gradle.org/plugin/de.lukaskoerfer.gradle.magicdraw). Simply use the `plugins` block to apply the plugin to your project:

``` gradle
plugins {
    id 'de.lukaskoerfer.gradle.magicdraw' version '0.3'
}
```

The plugin does not require any other plugin. No other plugin will be applied automatically.

## Usage

### Configuration

Once the plugin is applied, it can be configured via the `magicDraw` closure:

``` gradle
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
```

The most important configuration parameter is called `installDir` and must be set to the installation directory of MagicDraw.

> It is also possible to use the project property `magicDraw.installDir` to set the installation directory from the user-specific `gradle.properties` file.

Every MagicDraw plugin needs a descriptor file (`plugin.xml`) describing the most important aspects as the ID, the name, the version, the provider, the implementation class, referenced libraries and requirements regarding the MagicDraw API or other plugins.

The Gradle MagicDraw plugin will generate this descriptor file automatically. The descriptor file can be parametrized using the properties inside the `magicDraw.plugins.main` closure.
For technical reasons, the descriptor attributes *name*, *class* and *provider-name* are mapped to properties with other names (*label*, *className* and *provider*).

The five properties in the example above are required for each plugin. However, only the *label* and the *className* must always be specified via the closure.
Both the *id* and the *version* can be left out, which will cause the Gradle MagicDraw plugin to use the Gradle properties `project.group` and `project.version`.
The *provider* property will default to the system property `user.name`, if not specified.

> When leaving out the properties *id* and *version* to use the Gradle properties `project.group` and `project.version`, it is necessary to specify those Gradle properties, otherwise the build will fail.

### Dependencies

All MagicDraw libraries and their dependencies are provided in a single Gradle configuration called `magicDraw`. To use these dependencies in compilation or to import them into an IDE, it may required to extend another configuration like `compileOnly`:

``` gradle
configurations {
    compileOnly.extendsFrom magicDraw
}
```

Another possible syntax is using a dependency:

``` gradle
dependencies {
    compileOnly configurations.magicDraw
}
```

The `compileOnly` configuration is used to prevent the MagicDraw dependencies from being carried around with the project, as they are available with any MagicDraw installation. Of course, it is possible to use other configurations like `compile` or `implementation` and to manually take care, e.g. when creating a "fat" jar.

### Tasks

The Gradle MagicDraw plugin will create the following tasks:

* **assemblePlugin** - Assembles all MagicDraw plugin components in this project
* **launch** - Launches MagicDraw with this plugin
* **installPlugin** - Installs this MagicDraw plugin into the local MagicDraw instance
* **uninstallPlugin** - Removes this MagicDraw plugin from the local MagicDraw instance

To let the Gradle MagicDraw plugin provide the highest possible amount of flexibility, it is not linked or integrated with any other plugin like the `java` plugin.
By default, the `assemblePlugin` task does not collect any files, so it is required to register any library files that are used to implement the plugin, e.g.:

``` gradle
assemblePlugin {
    from jar
}
```

The example above can be used when using the `java` plugin. To include any dependencies, it is required to add the runtime classpath, too:

``` gradle
assemblePlugin {
    from jar
    from configurations.runtime
}
```

Instead of passing all dependencies manually, it is also possible to use the [`com.github.johnrengelman.shadow`](https://github.com/johnrengelman/shadow) plugin and create a "fat" jar:

``` gradle
assemblePlugin {
    from shadowJar
}
```

## License
The software is licensed under the [MIT license](https://github.com/lukoerfer/gradle-magicdraw/blob/master/LICENSE).
