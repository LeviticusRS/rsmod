package gg.rsmod.game.model.combat

import gg.rsmod.game.model.item.Item

/**
 * Represents a static item that is <strong>always</strong> be dropped by
 * an npc.
 *
 * @author Tom <rspsmods@gmail.com>
 */
data class NpcStaticDrop(val item: Int, val amount: Int) {

    constructor(item: Item) : this(item.id, item.amount)
}