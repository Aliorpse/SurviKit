package me.aliorpse.surviKit

import me.aliorpse.surviKit.commands.Main
import me.aliorpse.surviKit.listeners.chat.ExclCommands
import me.aliorpse.surviKit.listeners.chat.ShowPlayerCommands
import me.aliorpse.surviKit.listeners.chat.PunctuationsAutoEnglishify
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin

class SurviKit : JavaPlugin() {
    override fun onEnable() {
        saveDefaultConfig()

        // Events
        server.pluginManager.registerEvents(ExclCommands(
            NamespacedKey(this, "freelook_location"),
            this
        ), this)
        if(config.getBoolean("features.ShowPlayerCommands.enabled", true))
            server.pluginManager.registerEvents(ShowPlayerCommands(), this)
        if(config.getBoolean("features.PunctuationsAutoEnglishify.enabled", true))
            server.pluginManager.registerEvents(PunctuationsAutoEnglishify(), this)

        // Commands
        server.getPluginCommand("sk")?.apply {
            setExecutor(Main())
            tabCompleter = Main()
        }
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}