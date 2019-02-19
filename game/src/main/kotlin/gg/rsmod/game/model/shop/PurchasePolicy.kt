package gg.rsmod.game.model.shop

/**
 * @author Tom <rspsmods@gmail.com>
 */
enum class PurchasePolicy {
    /**
     * Does not buy any item.
     */
    BUY_NONE,
    /**
     * Buys only items that are in the starting stock.
     */
    BUY_STOCK,
    /**
     * Buys all tradeable items.
     */
    BUY_TRADEABLES,
    /**
     * Buys all items.
     */
    BUY_ALL
}