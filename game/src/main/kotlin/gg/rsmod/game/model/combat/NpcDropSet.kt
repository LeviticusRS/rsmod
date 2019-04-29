package gg.rsmod.game.model.combat

import gg.rsmod.game.model.Tile
import gg.rsmod.game.model.entity.Npc
import gg.rsmod.game.model.entity.Player

/**
 * Holds data in regards to one or more [NpcDynamicDrop]s.
 *
 * @param dynamicRolls the amount of items that will be selected from [dynamicDrops]
 * as items that will be dropped.
 *
 * @param tile receiver function used to modify the [Tile] where the drop will
 * be dropped.
 *
 * @param staticDrops a [Collection] of [NpcDynamicDrop]
 *
 * @param dynamicDrops a [Collection] of [NpcDynamicDrop] that have the possibility of
 * being dropped.
 *
 * @author Tom <rspsmods@gmail.com>
 */
data class NpcDropSet(val dynamicRolls: Int, val tile: ((Npc, Player) -> Tile)?,
                      val staticDrops: Collection<NpcStaticDrop>,
                      val dynamicDrops: Collection<NpcDynamicDrop>)