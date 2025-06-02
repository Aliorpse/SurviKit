package me.aliorpse.surviKit.commands

import me.aliorpse.surviKit.utils.TextColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class Main : CommandExecutor, TabCompleter {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (args.isEmpty()) {
            val pl = Bukkit.getServer().pluginManager.getPlugin("SurviKit")
            sender.sendMessage(
                TextColor.parse("&2SK &7> &fRunning SurviKit ${pl!!.pluginMeta.version}")
            )
            return true
        }
        when (args[0]) {
            "reload" -> {
                sender.sendMessage(
                    TextColor.parse("&2SK &7> &f尚未实现.")
                )
                return true
            }
            else -> {
                sender.sendMessage(
                    TextColor.parse("&2SK &7> &c未知命令.")
                )
                return false
            }
        }
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String?>? {
        return when {
            args.isEmpty() -> {
                listOf("reload", "ciallo")
            }

            else -> emptyList()
        }
    }
}