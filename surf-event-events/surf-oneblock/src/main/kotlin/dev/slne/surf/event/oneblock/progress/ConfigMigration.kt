package dev.slne.surf.event.oneblock.progress

import dev.slne.surf.event.oneblock.plugin
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.NodePath
import org.spongepowered.configurate.transformation.ConfigurationTransformation
import org.spongepowered.configurate.yaml.NodeStyle
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.nio.file.Path

object ConfigMigration {
    private const val VERSION_LATEST = 0

    fun create(): ConfigurationTransformation.Versioned {
        return ConfigurationTransformation.versionedBuilder()
            .addVersion(0, initialMigrateParents())
            .build()
    }

    fun initialMigrateParents(): ConfigurationTransformation {
        return ConfigurationTransformation.builder()
            .addAction(
                NodePath.path(
                    "phases",
                    ConfigurationTransformation.WILDCARD_OBJECT,
                    "parents"
                )
            ) { _, parentsNode ->
                if (!parentsNode.isList) return@addAction null

                val children = parentsNode.childrenList()
                for (i in children.indices) {
                    val entry = parentsNode.node(i)

                    val id = entry.string
                    if (id != null) {
                        entry.set(null)
                        entry.node("id").set(id)
                        entry.node("weight").set(1.0)
                    } else {
                        if (!entry.node("weight").virtual()) continue
                        if (!entry.node("id").virtual() && entry.node("weight").virtual()) {
                            entry.node("weight").set(1.0)
                        }
                    }
                }

                null
            }
            .build()
    }

    fun <N : ConfigurationNode> updateNode(node: N): N {
        if (!node.virtual()) {
            val trans = create()
            val start = trans.version(node)
            trans.apply(node)
            val end = trans.version(node)
            if (start != end) {
                plugin.logger.info("Updated config schema from version $start to $end")
            }
        }
        return node
    }

    fun upgradeFile(path: Path) {
        val loader = YamlConfigurationLoader.builder()
            .nodeStyle(NodeStyle.BLOCK)
            .defaultOptions {
                it.shouldCopyDefaults(true)
            }
            .path(path)
            .build()

        loader.save(updateNode(loader.load()))
    }
}