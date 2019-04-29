package gg.rsmod.game.plugin.drop

import gg.rsmod.game.event.Event
import gg.rsmod.game.model.Tile
import gg.rsmod.game.model.World
import gg.rsmod.game.model.combat.NpcDropSet
import gg.rsmod.game.model.combat.NpcDynamicDrop
import gg.rsmod.game.model.combat.NpcStaticDrop
import gg.rsmod.game.model.entity.Npc
import gg.rsmod.game.model.entity.Player
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import kotlin.script.experimental.annotations.KotlinScript

/**
 * @author Tom <rspsmods@gmail.com>
 */
@KotlinScript(
        displayName = "Kotlin Plugin",
        fileExtension = "drop.kts",
        compilationConfiguration = DropPluginConfiguration::class
)
abstract class DropPlugin(val world: World) {

    val npcs = IntOpenHashSet()

    var rolls = -1

    var tile: ((Npc, Player) -> Tile)? = null

    var delay = -1

    val staticDrops = mutableListOf<NpcStaticDrop>()

    val dynamicDrops = mutableListOf<NpcDynamicDrop>()

    fun build(): NpcDropSet {
        delay = Math.max(0, delay)
        rolls = Math.max(1, rolls)
        return NpcDropSet(rolls, tile, staticDrops.toList(), dynamicDrops.toList())
    }

    @DslMarker
    annotation class DropDslMarker

    @DropDslMarker
    class StaticDropBuilder {

        var item: Int = -1

        var amount: Int = -1
    }

    @DropDslMarker
    class DynamicDropBuilder {

        var item: Int = -1

        var amount: Int = -1

        var rate: Int = -1

        val events = mutableListOf<Event>()
    }
}