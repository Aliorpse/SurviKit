package me.aliorpse.survikit.modules.listeners

import io.papermc.paper.event.player.AsyncChatEvent
import me.aliorpse.survikit.modules.services.FormatterService
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class FormatterListener : Listener {

    @EventHandler
    fun onAsyncChat(e: AsyncChatEvent) {
        val msg = PlainTextComponentSerializer.plainText().serialize(e.message())

        when {
            msg.startsWith("!") ->
                return
            msg.startsWith("xaero-waypoint:") ->
                FormatterService.parseXaeroWaypoint(e)
            else ->
                FormatterService.universalChatFormatter(e)
        }
    }
}
