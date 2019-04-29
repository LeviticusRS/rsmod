package gg.rsmod.game.model.combat

import gg.rsmod.game.model.item.Item

/**
 * Represents a single [Item] dropped by an npc.
 *
 * @author Tom <rspsmods@gmail.com>
 */
data class NpcDrop(val item: Item, val rate: Int)