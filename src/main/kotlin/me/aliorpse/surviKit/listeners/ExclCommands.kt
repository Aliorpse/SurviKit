package me.aliorpse.surviKit.listeners

import me.aliorpse.surviKit.utils.TextColor
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import java.util.UUID

class ExclCommands(private val key: NamespacedKey, private val plugin: Plugin) : Listener {
    private fun Location.parseString() = "${world?.name},$x,$y,$z,$yaw,$pitch"

    private fun String.parseLocation(): Location? {
        val parts = split(",").takeIf { it.size == 6 } ?: return null
        return try {
            val world = Bukkit.getWorld(parts[0]) ?: return null
            Location(world,
                parts[1].toDouble(),
                parts[2].toDouble(),
                parts[3].toDouble(),
                parts[4].toFloat(),
                parts[5].toFloat()
            )
        } catch (_: Exception) {
            null
        }
    }

    private val cooldowns = mutableMapOf<UUID, Long>()
    private val cooldownTime = 1000L

    @EventHandler
    fun handleExclCommands(e: AsyncChatEvent) {
        val msg = PlainTextComponentSerializer.plainText().serialize(e.message())
        val pl = e.player

        if (!msg.startsWith("!")) return
        e.isCancelled = true

        val currentTime = System.currentTimeMillis()
        if (cooldowns.getOrDefault(pl.uniqueId, 0L) > currentTime) {
            return pl.sendMessage(TextColor.parse("&2SK &7> &c请稍后再使用此命令"))
        }
        cooldowns[pl.uniqueId] = currentTime + cooldownTime

        // 处理指令
        val args = msg.substring(1).split(" ")  // 去掉感叹号
        val cmd = args[0].lowercase()
        val cmdArgs = args.drop(1)

        when (cmd) {
            /// Freelook 功能
            /// 处于 Freelook 状态时, 玩家将有一个 freelook_location 容器, 存储着进入 Freelook 时的位置
            /// 不处于 Freelook 状态时, 玩家不会拥有 freelook_location 容器
            "s" -> {
                // 命令应当在在生存/旁观者模式下使用
                if (pl.gameMode != GameMode.SURVIVAL && pl.gameMode != GameMode.SPECTATOR)
                    return pl.sendMessage(
                        TextColor.parse("&2SK &7> &f这个指令应在生存/旁观者模式下使用."))

                val pdc = pl.persistentDataContainer
                val location = pdc.get(key, PersistentDataType.STRING)?.parseLocation()

                when {
                    location == null -> {
                        if (pl.gameMode == GameMode.SPECTATOR) {
                            return pl.sendMessage(
                                TextColor.parse("&2SK &7> &f你当前未处于 Freelook 状态. 请在生存模式下使用本功能."))
                        }
                        Bukkit.getScheduler().runTask(plugin, Runnable {
                            pdc.set(key, PersistentDataType.STRING, pl.location.parseString())

                            pl.gameMode = GameMode.SPECTATOR
                            pl.playerListName(TextColor.parse("&b&lFL &o&f" + pl.name))
                            pl.sendMessage(
                                TextColor.parse("&2SK &7> &fFreelook 已激活. 再次使用 !s 以退出"))
                        })
                    }
                    else -> {
                        Bukkit.getScheduler().runTask(plugin, Runnable {
                            try {
                                pdc.remove(key)

                                pl.teleport(location)
                                pl.gameMode = GameMode.SURVIVAL
                                pl.playerListName(TextColor.parse(pl.name))
                                pl.sendMessage(
                                    TextColor.parse("&2SK &7> &fFreelook 已退出"))
                            } catch (e: Exception) {
                                pl.sendMessage(
                                    TextColor.parse("&2SK &7> &cFreelook 退出失败: ${e.message}"))
                            }
                        })
                    }
                }
            }
            else -> pl.sendMessage(
                TextColor.parse("&2SK &7> &f未知命令."))
        }
    }
}