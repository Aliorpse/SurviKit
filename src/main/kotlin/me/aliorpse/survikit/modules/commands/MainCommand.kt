package me.aliorpse.survikit.modules.commands

import me.aliorpse.survikit.utils.TextColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class MainCommand : CommandExecutor, TabCompleter {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (args.isEmpty()) {
            val pl = Bukkit.getServer().pluginManager.getPlugin("SurviKit")
            sender.sendMessage(
                TextColor.parse("&aSK &8> &fRunning SurviKit ${pl!!.pluginMeta.version}")
            )
            return true
        }
        when (args[0]) {
            "reload" -> {
                sender.sendMessage(
                    TextColor.parse("&aSK &8> &f尚未实现.")
                )
            }
            else -> {
                sender.sendMessage(
                    TextColor.parse("&aSK &8> &f未知指令.")
                )
            }
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String> {
        return when {
            args.size == 1 -> listOf("reload", "ciallo")
            else -> emptyList()
        }
    }
}