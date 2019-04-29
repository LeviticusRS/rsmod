package gg.rsmod.game.model.combat

import gg.rsmod.game.model.entity.Npc
import gg.rsmod.game.model.entity.Player
import gg.rsmod.game.model.item.Item

/**
 * Represents a dynamic item that <strong>can</strong> be dropped by an npc.
 *
 * @author Tom <rspsmods@gmail.com>
 */
class NpcDynamicDrop(val item: Int, val amount: Int, val rate: Int, val action: ((Npc, Player) -> Unit)? = null) {

    constructor(item: Item, rate: Int) : this(item.id, item.amount, rate)
}