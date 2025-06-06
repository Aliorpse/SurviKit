package me.aliorpse.survikit

import me.aliorpse.survikit.apps.main.command.MainCommand
import me.aliorpse.survikit.apps.show.player.command.CommandsListener
import me.aliorpse.survikit.apps.chat.formatter.ChatListener
import me.aliorpse.survikit.apps.excl.commands.ExclListener
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin

class SurviKit : JavaPlugin() {
    override fun onEnable() {
        saveDefaultConfig()

        /// Register the plugin by apps
        // MainCommand
        server.getPluginCommand("sk")?.apply {
            setExecutor(MainCommand())
            tabCompleter = MainCommand()
        }
        // ExclCommands
        server.pluginManager.registerEvents(ExclListener(
            NamespacedKey(this, "freelook_location"),
            this
        ), this)

        /// Optional features
        // ShowPlayerCommands
        if(config.getBoolean(("features.show-player-commands.enabled"), true))
            server.pluginManager.registerEvents(CommandsListener(), this)
        // ChatFormateer
        if(config.getBoolean("features.chat-formatter.enabled", true))
            server.pluginManager.registerEvents(ChatListener(), this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}