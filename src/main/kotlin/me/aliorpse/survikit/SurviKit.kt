package me.aliorpse.survikit

import me.aliorpse.survikit.modules.main.command.MainCommand
import me.aliorpse.survikit.modules.show.player.command.CommandsListener
import me.aliorpse.survikit.modules.chat.formatter.FormatterListener
import me.aliorpse.survikit.modules.excl.commands.ExclListener
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

        // MainCommand
        server.getPluginCommand("sk")?.apply {
            setExecutor(MainCommand())
            tabCompleter = MainCommand()
        }

        // ExclCommands
        server.pluginManager.registerEvents(ExclListener(
                NamespacedKey(this, "freelook_location"),
                this
            ), this
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