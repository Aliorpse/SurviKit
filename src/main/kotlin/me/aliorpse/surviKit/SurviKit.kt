package me.aliorpse.surviKit

import me.aliorpse.surviKit.listeners.ShowPlayerCommands
import me.aliorpse.surviKit.listeners.ExclCommands
import me.aliorpse.surviKit.listeners.PunctuationsAutoEnglishify
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin

class SurviKit : JavaPlugin() {
    override fun onEnable() {
        saveDefaultConfig()
        // Register EventHandlers
        server.pluginManager.registerEvents(ExclCommands(
            NamespacedKey(this, "freelook_location"),
            this
        ), this)
        if(config.getBoolean("features.ShowPlayerCommands.enabled", true))
            server.pluginManager.registerEvents(ShowPlayerCommands(server), this)
        if(config.getBoolean("features.PunctuationsAutoEnglishify.enabled", true))
            server.pluginManager.registerEvents(PunctuationsAutoEnglishify(), this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}