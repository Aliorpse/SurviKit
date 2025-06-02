package me.aliorpse.surviKit.listeners.chat

import me.aliorpse.surviKit.utils.TextColor
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class ShowPlayerCommands() : Listener {
    @EventHandler
    fun showPlayerCommands(e: PlayerCommandPreprocessEvent) {
        Bukkit.broadcast(
            TextColor.parse("&bCMD" + " &8> &r" + e.player.name + ": " + e.message)
        )
    }
}