package gg.rsmod.game.model.combat

import gg.rsmod.game.model.Tile

/**
 * Holds data in regards to one or more [NpcDrop]s.
 *
 * @param tile the [Tile] where [drops] will appear on.
 *
 * @param delay the delay before the [drops] are registered to the world, in
 * game cycles.
 *
 * @param drops a [Collection] of [NpcDrop] that are being dropped.
 *
 * @author Tom <rspsmods@gmail.com>
 */
data class NpcDropSet(val tile: Tile, val delay: Int, val drops: Collection<NpcDrop>)