package gg.rsmod.game.action

import gg.rsmod.game.fs.def.AnimDef
import gg.rsmod.game.model.LockState
import gg.rsmod.game.model.World
import gg.rsmod.game.model.attr.KILLER_ATTR
import gg.rsmod.game.model.entity.GroundItem
import gg.rsmod.game.model.entity.Npc
import gg.rsmod.game.model.entity.Player
import gg.rsmod.game.model.item.Item
import gg.rsmod.game.model.queue.QueueTask
import gg.rsmod.game.model.queue.TaskPriority
import gg.rsmod.game.plugin.Plugin
import gg.rsmod.game.service.log.LoggerService
import java.lang.ref.WeakReference

/**
 * This class is responsible for handling npc death events.
 *
 * @author Tom <rspsmods@gmail.com>
 */
object NpcDeathAction {

    val deathPlugin: Plugin.() -> Unit = {
        val npc = ctx as Npc
        npc.lock()
        npc.queue(TaskPriority.STRONG) {
            death(npc)
        }
    }

    private suspend fun QueueTask.death(npc: Npc) {
        val world = npc.world
        val deathAnimation = npc.combatDef.deathAnimation
        val respawnDelay = npc.combatDef.respawnDelay

        npc.damageMap.getMostDamage()?.let { killer ->
            if (killer is Player) {
                world.getService(LoggerService::class.java, searchSubclasses = true)?.logNpcKill(killer, npc)
            }
            npc.attr[KILLER_ATTR] = WeakReference(killer)
        }

        world.plugins.executeNpcPreDeath(npc)

        npc.resetFacePawn()

        deathAnimation.forEach { anim ->
            val def = npc.world.definitions.get(AnimDef::class.java, anim)
            npc.animate(def.id)
            wait(def.cycleLength + 1)
        }

        npc.animate(-1)

        npc.attr[KILLER_ATTR]?.get()?.let { killer ->
            npc.dropLoot(world, killer as? Player)
        }

        world.plugins.executeNpcDeath(npc)

        if (npc.respawns) {
            npc.invisible = true
            npc.reset()
            wait(respawnDelay)
            npc.invisible = false
            world.plugins.executeNpcSpawn(npc)
        } else {
            world.remove(npc)
        }
    }

    private fun Npc.reset() {
        lock = LockState.NONE
        tile = spawnTile
        setTransmogId(-1)

        attr.clear()
        timers.clear()
        world.setNpcDefaults(this)
    }

    private fun Npc.dropLoot(world: World, killer: Player?) {
        val dropSet = world.dropRepository[id] ?: return

        val dropTile = if (killer != null && dropSet.tile != null) {
            dropSet.tile.invoke(this, killer)
        } else {
            tile
        }
        val staticDrops = dropSet.staticDrops
        val dynamicDrops = dropSet.dynamicDrops
        val dynamicRolls = dropSet.dynamicRolls

        staticDrops.forEach { drop ->
            val item = Item(drop.item, drop.amount)
            world.spawn(GroundItem(item, dropTile, killer))
        }

        repeat(dynamicRolls) {
            for (drop in dynamicDrops) {
                val success = world.chance(1, drop.rate)
                if (success) {
                    val item = Item(drop.item, drop.amount)
                    world.spawn(GroundItem(item, dropTile, killer))
                    killer?.let { player ->
                        drop.action?.invoke(this, player)
                    }
                    break
                }
            }
        }
    }
}