package me.aliorpse.survikit

import me.aliorpse.survikit.modules.commands.BlockStateCommand
import me.aliorpse.survikit.modules.commands.MainCommand
import me.aliorpse.survikit.modules.commands.WarpsCommand
import me.aliorpse.survikit.modules.listeners.CommandsListener
import me.aliorpse.survikit.modules.listeners.ExclListener
import me.aliorpse.survikit.modules.listeners.FormatterListener
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

        // BlockState
        server.getPluginCommand("bs")?.apply {
            setExecutor(BlockStateCommand())
            tabCompleter = BlockStateCommand()
        }

        // ExclCommands
        server.pluginManager.registerEvents(
            ExclListener(NamespacedKey(this, "freelook_location")),
            this
        )

        // ShowPlayerCommands
        server.pluginManager.registerEvents(CommandsListener(), this)
        // ChatFormatter
        server.pluginManager.registerEvents(FormatterListener(), this)
    }

    override fun onDisable() {
        // 喵喵
    }
}
