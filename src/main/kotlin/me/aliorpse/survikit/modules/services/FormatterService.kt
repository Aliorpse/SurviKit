package me.aliorpse.survikit.modules.services

import io.papermc.paper.event.player.AsyncChatEvent
import me.aliorpse.survikit.SurviKit
import me.aliorpse.survikit.utils.TextColor
import me.aliorpse.survikit.utils.XaeroWaypoint
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Sound
import java.util.regex.Pattern

object FormatterService {
    private val config = SurviKit.Companion.instance.config

    // 不要问我为啥写成这样, detekt 说我这行太长(
    private val ipAddressRegex = (
        """(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}""" +
            """(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)|""" +
            """(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|""" +
            """(?:([0-9a-fA-F]{1,4}:){0,6}[0-9a-fA-F]{1,4})?::""" +
            """(?:([0-9a-fA-F]{1,4}:){0,6}[0-9a-fA-F]{1,4})?"""
        ).toPattern()

    fun parseXaeroWaypoint(e: AsyncChatEvent) {
        e.isCancelled = true

        if (!config.getBoolean("modules.override-xaero-waypoint-share.enabled")) return

        val waypoint = XaeroWaypoint.Companion.parse(PlainTextComponentSerializer.plainText().serialize(e.message()))
        if (waypoint == null) return

        val dimension = when (waypoint.dimension) {
            "overworld" -> "&2主世界"
            "the-nether" -> "&c下界"
            "the-end" -> "&d末地"
            else -> waypoint.dimension
        }

        val message = Component.text()
            .append(TextColor.parse("&e# &7${e.player.name} 分享路径点 &f${waypoint.name} &7- &f$dimension "))
            .append(
                Component.text("(${waypoint.x}, ${waypoint.y}, ${waypoint.z})")
                    .color(NamedTextColor.YELLOW)
                    .hoverEvent(HoverEvent.showText(Component.text("点击复制坐标")))
                    .clickEvent(ClickEvent.copyToClipboard("${waypoint.x} ${waypoint.y} ${waypoint.z}"))
            )
            .build()
        Bukkit.broadcast(message)
    }

    @Suppress("MagicNumber")
    fun universalChatFormatter(e: AsyncChatEvent) {
        var msg = e.message()

        // At
        run {
            val onlinePlayers = Bukkit.getOnlinePlayers()
            val playerNameMap = onlinePlayers.associateBy { it.name }
            val pattern = Pattern.compile(
                onlinePlayers
                    .map { it.name }
                    .joinToString("|") { Pattern.quote(it) }
            )

            msg = msg.replaceText {
                it.match(pattern)
                    .replacement { match, _ ->
                        playerNameMap[match.group()]?.let { player ->
                            player.playSound(
                                player.location,
                                Sound.BLOCK_NOTE_BLOCK_HARP,
                                0.8f,
                                1.0f
                            )
                        }
                        TextColor.parse("&3&l@&b${match.group()}&r")
                    }
                    .build()
            }
        }

        // IP地址
        msg = msg.replaceText {
            it.match(ipAddressRegex)
                .replacement { match, _ ->
                    TextColor.parse("&e&n${match.group()}&r")
                        .hoverEvent(HoverEvent.showText(Component.text("这是一个IP地址! 点击复制")))
                        .clickEvent(ClickEvent.copyToClipboard(match.group()))
                }
                .build()
        }

        e.message(msg)
    }
}
