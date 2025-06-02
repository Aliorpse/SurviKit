package me.aliorpse.surviKit.listeners

import me.aliorpse.surviKit.utils.TextColor
import org.bukkit.Server
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class ShowPlayerCommands(private val server: Server) : Listener {
    @EventHandler
    fun showPlayerCommands(e: PlayerCommandPreprocessEvent) {
        val pl = e.player
        server.broadcast(TextColor.parse(
            "&eCMD" + " &8> &r" + pl.name + ": " + e.message
        ))
    }
}