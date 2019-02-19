package gg.rsmod.game.plugin

import gg.rsmod.game.model.Tile
import gg.rsmod.game.model.entity.Pawn
import gg.rsmod.game.model.entity.Player
import gg.rsmod.game.plugin.coroutine.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import mu.KotlinLogging
import kotlin.coroutines.*

/**
 * Represents a plugin that can be executed at any time by a context.
 *
 * @param ctx
 * Can be anything from [Player] to [gg.rsmod.game.model.entity.Pawn].
 *
 * @param dispatcher
 * The [CoroutineDispatcher] used to create the scope for our suspendable plugins.
 *
 * @author Tom <rspsmods@gmail.com>
 */
class Plugin(var ctx: Any?, private val dispatcher: CoroutineDispatcher) : Continuation<Unit> {

    companion object {
        private val logger = KotlinLogging.logger {  }
    }

    /**
     * A value that can be requested by a plugin, such as an input for dialogs.
     */
    var requestReturnValue: Any? = null

    /**
     * Can represent an action that should be executed if, and only if, this plugin
     * was interrupted by another action such as walking or a new script being
     * executed by the same [ctx].
     */
    var interruptAction: Function1<Plugin, Unit>? = null

    /**
     * The next [SuspendableStep], if any, that must be handled once a [SuspendableCondition]
     * returns [SuspendableCondition.resume] as true.
     */
    private var nextStep: SuspendableStep? = null

    /**
     * The [CoroutineContext] implementation for our [Plugin].
     */
    override val context: CoroutineContext = EmptyCoroutineContext

    /**
     * When the [nextStep] [SuspendableCondition.resume] returns true, this
     * method is called.
     */
    override fun resumeWith(result: Result<Unit>) {
        nextStep = null
        result.exceptionOrNull()?.let { e -> logger.error("Error with plugin!", e) }
    }

    /**
     * Boilerplate to signal the use of suspendable logic.
     */
    fun suspendable(block: suspend CoroutineScope.() -> Unit) {
        val coroutine = block.createCoroutine(receiver = CoroutineScope(dispatcher), completion = this)
        coroutine.resume(Unit)
    }

    /**
     * The logic in each [SuspendableStep] must be game-thread-safe, so we use pulse
     * method to keep them in-sync.
     */
    fun pulse() {
        val next = nextStep ?: return

        if (next.condition.resume()) {
            next.continuation.resume(Unit)
            requestReturnValue = null
        }
    }

    fun removeContext() {
        ctx = null
    }

    /**
     * Terminate any further execution of this plugin, at any state.
     */
    fun terminate() {
        nextStep = null
        requestReturnValue = null
        interruptAction?.invoke(this)
    }

    /**
     * If the plugin has been suspended.
     */
    fun suspended(): Boolean = nextStep != null

    /**
     * Wait for the specified amount of game cycles [cycles] before
     * continuing the logic associated with this plugin.
     */
    suspend fun wait(cycles: Int): Unit = suspendCoroutine {
        check(cycles > 0) { "Wait cycles must be greater than 0." }
        nextStep = SuspendableStep(WaitCondition(cycles), it)
    }

    /**
     * Wait for [predicate] to return true.
     */
    suspend fun wait(predicate: () -> Boolean): Unit = suspendCoroutine {
        nextStep = SuspendableStep(PredicateCondition { predicate.invoke() }, it)
    }

    /**
     * Wait for our [ctx] to reach [tile]. Note that [ctx] MUST be an instance
     * of [Pawn] and that the height of the [tile] and [Pawn.tile] must be equal,
     * as well as the x and z coordinates.
     */
    suspend fun waitTile(tile: Tile): Unit = suspendCoroutine {
        nextStep = SuspendableStep(TileCondition((ctx as Pawn).tile, tile), it)
    }

    /**
     * Wait for our [ctx] as [Player] to close the [interfaceId].
     */
    suspend fun waitInterfaceClose(interfaceId: Int): Unit = suspendCoroutine {
        nextStep = SuspendableStep(PredicateCondition { !(ctx as Player).interfaces.isVisible(interfaceId) }, it)
    }

    /**
     * Wait for a return value to be available before continuing.
     */
    suspend fun waitReturnValue(): Unit = suspendCoroutine {
        nextStep = SuspendableStep(PredicateCondition { requestReturnValue != null }, it)
    }
}