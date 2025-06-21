package me.aliorpse.survikit.modules.listeners

import me.aliorpse.survikit.SurviKit
import me.aliorpse.survikit.utils.TextColor
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class CommandsListener : Listener {

    private val config = SurviKit.Companion.instance.config

    @EventHandler
    fun onCommand(e: PlayerCommandPreprocessEvent) {
        if (
            !config.getBoolean("modules.show-player-command.enabled") ||
            e.message == "/"
        ) {
            return
        }

        val filtered = config.getList("modules.show-player-command.ignores")!!
        val rootCommand = e.message.substring(1)
            .takeWhile { !it.isWhitespace() }
            .lowercase()

        if (rootCommand in filtered) return

        Bukkit.getOnlinePlayers().forEach {
            it.sendMessage(
                TextColor.parse("&b&l>_ &7&o${e.player.name}: ${e.message}")
            )
        }
    }
}