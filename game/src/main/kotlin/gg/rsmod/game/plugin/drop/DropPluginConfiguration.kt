package gg.rsmod.game.plugin.drop

import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.defaultImports

object DropPluginConfiguration : ScriptCompilationConfiguration({
    defaultImports(
            "gg.rsmod.game.model.combat.NpcStaticDrop",
            "gg.rsmod.game.model.combat.NpcDynamicDrop",
            "gg.rsmod.game.model.combat.NpcDropSet",

            "gg.rsmod.plugins.api.cfg.Npcs",
            "gg.rsmod.plugins.api.cfg.Items"
    )
})