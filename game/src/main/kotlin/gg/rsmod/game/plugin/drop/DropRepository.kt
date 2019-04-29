package gg.rsmod.game.plugin.drop

import gg.rsmod.game.model.World
import gg.rsmod.game.model.combat.NpcDropSet
import io.github.classgraph.ClassGraph
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import mu.KLogging

/**
 * @author Tom <rspsmods@gmail.com>
 */
class DropRepository(val world: World) {

    private val drops = Int2ObjectOpenHashMap<NpcDropSet>()

    operator fun set(npcId: Int, dropSet: NpcDropSet) {
        if (drops.containsKey(npcId)) {
            val exception = RuntimeException("Npc drop already set: $npcId")
            logger.error(exception) {}
            throw exception
        }
        drops[npcId] = dropSet
    }

    operator fun get(npcId: Int): NpcDropSet? = drops.getOrDefault(npcId, null)

    fun init() {
        ClassGraph().enableAllInfo().whitelistModules().scan().use { result ->
            val plugins = result.getSubclasses(DropPlugin::class.java.name).directOnly()
            plugins.forEach { p ->
                val pluginClass = p.loadClass(DropPlugin::class.java)
                val constructor = pluginClass.getConstructor(World::class.java)
                val plugin = constructor.newInstance(world)

                plugin.npcs.forEach { npc ->
                    val dropSet = plugin.build()
                    set(npc, dropSet)
                }
            }
        }
    }

    companion object : KLogging()
}