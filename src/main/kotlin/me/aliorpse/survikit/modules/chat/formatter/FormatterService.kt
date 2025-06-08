package me.aliorpse.survikit.modules.chat.formatter

import io.papermc.paper.event.player.AsyncChatEvent
import me.aliorpse.survikit.SurviKit
import me.aliorpse.survikit.utils.TextColor
import me.aliorpse.survikit.utils.XaeroWaypoint
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Sound

class FormatterService {
    private val config = SurviKit.instance.config
    private val ipAddressRegex = """(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)|(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|(?:([0-9a-fA-F]{1,4}:){0,6}[0-9a-fA-F]{1,4})?::(?:([0-9a-fA-F]{1,4}:){0,6}[0-9a-fA-F]{1,4})?""".toPattern()

    fun parseXaeroWaypoint(e: AsyncChatEvent) {
        if (!config.getBoolean("modules.override-xaero-waypoint-share.enabled")) return

        val waypoint = XaeroWaypoint.parse(PlainTextComponentSerializer.plainText().serialize(e.message()))
        if (waypoint == null) return

        val dimension = when(waypoint.dimension) {
            "overworld" -> "&2主世界"
            "the-nether" -> "&c下界"
            "the-end" -> "&d末地"
            else -> waypoint.dimension
        }

        val message = Component.text()
            .append(TextColor.parse("&e# &7${e.player.name} 分享路径点 &f${waypoint.name} &7- &f${dimension} "))
            .append(
                Component.text("(${waypoint.x}, ${waypoint.y}, ${waypoint.z})")
                    .color(NamedTextColor.YELLOW)
                    .hoverEvent(HoverEvent.showText(Component.text("点击复制坐标")))
                    .clickEvent(ClickEvent.copyToClipboard("${waypoint.x} ${waypoint.y} ${waypoint.z}"))
            )
            .build()
        Bukkit.broadcast(message)
    }

    fun universalChatFormatter(e: AsyncChatEvent) {
        var msgRaw = PlainTextComponentSerializer.plainText().serialize(e.message())

        // 非 Component
        // At
        Bukkit.getOnlinePlayers().forEach { player ->
            if (player.name in msgRaw) {
                msgRaw = msgRaw.replace(player.name, "§3§l@§b${player.name}§r")
                player.playSound(
                    player.location,
                    Sound.BLOCK_NOTE_BLOCK_HARP,
                    0.8f,
                    1.0f
                )
            }
        }

        // Component
        var message = TextColor.parse(msgRaw)

        // IP地址
        val ipReplacement = TextReplacementConfig.builder()
            .match(ipAddressRegex)
            .replacement { match, _ ->
                Component.text(match.group())
                    .color(NamedTextColor.YELLOW)
                    .decorate(TextDecoration.UNDERLINED)
                    .hoverEvent(HoverEvent.showText(Component.text("这是一个IP地址! 点击复制")))
                    .clickEvent(ClickEvent.copyToClipboard(match.group()))
            }
            .build()

        message = message.replaceText(ipReplacement)

        Bukkit.broadcast(Component.text()
            .append(Component.text("<${e.player.name}> "))
            .append(message)
            .build()
        )
    }
}