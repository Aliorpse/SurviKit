package me.aliorpse.survikit.apps.excl.commands

import me.aliorpse.survikit.utils.TextColor
import me.aliorpse.survikit.utils.parseLocation
import me.aliorpse.survikit.utils.parseString
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin

class ExclServices {
    /// Freelook 功能
    /// 处于 Freelook 状态时, 玩家将有一个 freelook_location 容器, 存储着进入 Freelook 时的位置
    /// 不处于 Freelook 状态时, 玩家不会拥有 freelook_location 容器
    fun specMode(pl: Player, key: NamespacedKey, plugin: Plugin) {
        // 命令应当在在生存/旁观者模式下使用
        if (pl.gameMode != GameMode.SURVIVAL && pl.gameMode != GameMode.SPECTATOR)
            return pl.sendMessage(
                TextColor.parse("&2SK &8> &f这个指令应在生存/旁观者模式下使用.")
            )

        val pdc = pl.persistentDataContainer
        val location = pdc.get(key, PersistentDataType.STRING)?.parseLocation()

        when {
            location == null -> {
                if (pl.gameMode == GameMode.SPECTATOR) {
                    return pl.sendMessage(
                        TextColor.parse("&2SK &8> &f你当前未处于 Freelook 状态. 请在生存模式下使用本功能.")
                    )
                }
                Bukkit.getScheduler().runTask(plugin, Runnable {
                    pdc.set(key, PersistentDataType.STRING, pl.location.parseString())

                    pl.gameMode = GameMode.SPECTATOR
                    pl.playerListName(TextColor.parse("&b&lFL&r " + pl.name))
                    pl.sendMessage(
                        TextColor.parse("&2SK &8> &fFreelook 已激活. 再次使用 !s 以退出")
                    )
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
                            TextColor.parse("&2SK &8> &fFreelook 已退出")
                        )
                    } catch (e: Exception) {
                        pl.sendMessage(
                            TextColor.parse("&2SK &8> &cFreelook 退出失败: ${e.message}")
                        )
                    }
                })
            }
        }
    }
}