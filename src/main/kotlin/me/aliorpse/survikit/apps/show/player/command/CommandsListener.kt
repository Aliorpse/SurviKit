package me.aliorpse.survikit.apps.show.player.command

import me.aliorpse.survikit.utils.TextColor
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class CommandsListener() : Listener {
    @EventHandler
    fun showPlayerCommands(e: PlayerCommandPreprocessEvent) {
        Bukkit.broadcast(
            TextColor.parse("&bCMD" + " &8> &7" + e.player.name + ": " + e.message)
        )
    }
}