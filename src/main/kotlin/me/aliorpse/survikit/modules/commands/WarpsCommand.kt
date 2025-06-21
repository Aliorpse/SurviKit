package me.aliorpse.survikit.modules.commands

import me.aliorpse.survikit.utils.TextColor
import me.aliorpse.survikit.utils.WarpManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.util.Locale

class WarpsCommand : CommandExecutor, TabCompleter {

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (args.isEmpty()) {
            val menu = buildString {
                append("&aSK &8> &fWarp 帮助菜单:\n \n")
                append("&7  /$label list &8- &f列出所有 Warp\n")
                append("&7  /$label set <名称> &8- &f设置当前位置为 Warp\n")
                append("&7  /$label get <名称> &8- &f查看 Warp 信息\n")
                append("&7  /$label del <名称> &8- &f删除 Warp\n")
            }
            sender.sendMessage(TextColor.parse(menu))
            return true
        }

        when (args[0].lowercase()) {
            "list" -> {
                val warps = WarpManager.listAllWarps().sorted()
                if (warps.isEmpty()) {
                    sender.sendMessage(TextColor.parse("&aSK &8> &fWarp 列表: &7(暂无 Warp 点)"))
                } else {
                    sender.sendMessage(TextColor.parse("&aSK &8> &fWarp 列表:"))
                    warps.forEach { name ->
                        val component = Component.text("  - ", NamedTextColor.GRAY)
                            .append(
                                Component.text(name, NamedTextColor.WHITE)
                                    .hoverEvent(HoverEvent.showText(Component.text("点击查看坐标")))
                                    .clickEvent(ClickEvent.runCommand("/$label get $name"))
                            )
                        sender.sendMessage(component)
                    }
                }
                return true
            }

            "set" -> {
                if (args.size < 2 || args[1].isBlank()) {
                    sender.sendMessage(TextColor.parse("&aSK &8> &c用法: /$label set <名称>"))
                    return true
                }
                if (sender !is Player) {
                    sender.sendMessage(TextColor.parse("&aSK &8> &c只有玩家可以执行此命令"))
                    return true
                }
                val name = args[1]
                WarpManager.saveWarp(name, sender.location)
                sender.sendMessage(TextColor.parse("&aSK &8> &f已保存 &a$name&f 于你所在位置"))
                return true
            }

            "get" -> {
                if (args.size < 2 || args[1].isBlank()) {
                    sender.sendMessage(TextColor.parse("&aSK &8> &c用法: /$label get <名称>"))
                    return true
                }
                val name = args[1]
                val loc = WarpManager.loadWarp(name)
                if (loc == null) {
                    sender.sendMessage(TextColor.parse("&aSK &8> &c不存在名为 &f$name &c的 Warp"))
                } else {
                    val x = String.Companion.format(Locale.ROOT, "%.1f", loc.x)
                    val y = String.Companion.format(Locale.ROOT, "%.1f", loc.y)
                    val z = String.Companion.format(Locale.ROOT, "%.1f", loc.z)
                    val msg = Component.text()
                        .append(TextColor.parse("&aSK &8> &fWarp &a$name &f的信息:\n"))
                        .append(TextColor.parse("  世界: ${loc.world?.name} \n"))
                        .append(TextColor.parse("  坐标: "))
                        .append(
                            TextColor.parse("&e$x, $y, $z")
                                .hoverEvent(HoverEvent.showText(Component.text("点击复制坐标")))
                                .clickEvent(ClickEvent.copyToClipboard("$x $y $z"))
                        )
                    sender.sendMessage(msg)
                }
                return true
            }

            "del" -> {
                if (args.size < 2 || args[1].isBlank()) {
                    sender.sendMessage(TextColor.parse("&aSK &8> &c用法: /$label del <名称>"))
                    return true
                }
                val name = args[1]
                if (!WarpManager.listAllWarps().contains(name)) {
                    sender.sendMessage(TextColor.parse("&aSK &8> &c不存在名为 &a$name &c的 Warp"))
                } else {
                    WarpManager.removeWarp(name)
                    sender.sendMessage(TextColor.parse("&aSK &8> &c已删除 &a$name"))
                }
                return true
            }

            else -> {
                sender.sendMessage(TextColor.parse("&aSK &8> &c未知指令，请使用 /$label 查看菜单。"))
                return true
            }
        }
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String> {
        return when (args.size) {
            1 -> listOf("list", "set", "get", "del").filter { it.startsWith(args[0].lowercase()) }
            2 -> when (args[0].lowercase()) {
                "get", "del" -> WarpManager.listAllWarps().filter { it.startsWith(args[1].lowercase()) }
                else -> emptyList()
            }
            else -> emptyList()
        }
    }
}