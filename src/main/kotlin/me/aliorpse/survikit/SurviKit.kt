package me.aliorpse.survikit

import me.aliorpse.survikit.modules.chat.formatter.FormatterListener
import me.aliorpse.survikit.modules.excl.commands.ExclListener
import me.aliorpse.survikit.modules.main.command.MainCommand
import me.aliorpse.survikit.modules.show.player.command.CommandsListener
import me.aliorpse.survikit.modules.warps.WarpsCommand
import me.aliorpse.survikit.utils.WarpManager
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin

class SurviKit : JavaPlugin() {
    companion object {
        lateinit var instance: SurviKit
            private set
    }

    override fun onEnable() {
        saveDefaultConfig()
        instance = this
        WarpManager.init(this)

        // MainCommand
        server.getPluginCommand("sk")?.apply {
            setExecutor(MainCommand())
            tabCompleter = MainCommand()
        }

        // Warps
        server.getPluginCommand("warp")?.apply {
            setExecutor(WarpsCommand())
            tabCompleter = WarpsCommand()
        }

        // ExclCommands
        server.pluginManager.registerEvents(
            ExclListener(
                NamespacedKey(this, "freelook_location"),
                this
            ),
            this
        )

        // ShowPlayerCommands
        server.pluginManager.registerEvents(CommandsListener(), this)
        // ChatFormatter
        server.pluginManager.registerEvents(FormatterListener(), this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
