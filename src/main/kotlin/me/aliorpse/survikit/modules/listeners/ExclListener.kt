package me.aliorpse.survikit.modules.listeners

import io.papermc.paper.event.player.AsyncChatEvent
import me.aliorpse.survikit.SurviKit
import me.aliorpse.survikit.modules.services.ExclService
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.UUID

class ExclListener(
    private val key: NamespacedKey,
) : Listener {
    companion object {
        const val COOLDOWN_TIME_MS = 1000L
    }

    private val plugin = SurviKit.instance
    private val cooldowns = mutableMapOf<UUID, Long>()

    @EventHandler
    fun handleExclCommands(e: AsyncChatEvent) {
        val msg = PlainTextComponentSerializer.plainText().serialize(e.message())
        val pl = e.player

        if (!msg.startsWith("!")) return
        e.isCancelled = true

        val currentTime = System.currentTimeMillis()
        if (cooldowns.getOrDefault(pl.uniqueId, 0L) > currentTime) {
            return pl.sendMessage("§aSK §8> §c请稍后再使用此命令.")
        }
        cooldowns[pl.uniqueId] = currentTime + COOLDOWN_TIME_MS

        // 处理指令
        val args = msg.substring(1).split(" ")
        val cmd = args[0].lowercase()
        val cmdArgs = args.drop(1)

        when (cmd) {
            "" ->
                ExclService.showHelp(pl)
            "s" ->
                ExclService.freeLook(pl, key, plugin)
            "loc" ->
                ExclService.showLocation(pl)
            else -> pl.sendMessage("§aSK §8> §f未知命令.")
        }
        Bukkit.getServer().consoleSender.sendMessage("${pl.name} issued exclamation mark command: $msg")
    }
}
