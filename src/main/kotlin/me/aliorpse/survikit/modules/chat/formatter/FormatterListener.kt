package me.aliorpse.survikit.modules.chat.formatter

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import kotlin.text.startsWith

class FormatterListener : Listener {

    private val services = FormatterService()

    @EventHandler
    fun onAsyncChat(e: AsyncChatEvent) {
        val msg = PlainTextComponentSerializer.plainText().serialize(e.message())

        e.isCancelled = true

        when {
            msg.startsWith("!") ->
                return
            msg.startsWith("xaero-waypoint:")  ->
                services.parseXaeroWaypoint(e)
            else ->
                services.universalChatFormatter(e)
        }
    }
}