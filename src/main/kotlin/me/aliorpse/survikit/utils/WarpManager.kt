package me.aliorpse.survikit.utils

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin

// GPT写的, 我改了点
object WarpManager {

    private lateinit var plugin: JavaPlugin
    private const val KEY_PREFIX = "warp_"

    fun init(plugin: JavaPlugin) {
        this.plugin = plugin
    }

    private fun getWarpKey(name: String): NamespacedKey {
        return NamespacedKey(plugin, "$KEY_PREFIX$name")
    }

    // 保存 Warp 到对应世界
    fun saveWarp(name: String, location: Location) {
        val container = location.world?.persistentDataContainer ?: return
        val key = getWarpKey(name)
        container.set(key, PersistentDataType.STRING, location.parseString())
    }

    // 从所有世界中尝试加载 Warp
    fun loadWarp(name: String): Location? {
        return Bukkit.getWorlds().firstNotNullOfOrNull { world ->
            val key = getWarpKey(name)
            val data = world.persistentDataContainer.get(key, PersistentDataType.STRING)
            data?.parseLocation()
        }
    }

    // 删除指定名字的 Warp（从所有世界中尝试）
    fun removeWarp(name: String) {
        val key = getWarpKey(name)
        Bukkit.getWorlds().forEach { world ->
            world.persistentDataContainer.remove(key)
        }
    }

    // 获取所有世界中所有 Warp 的名字
    fun listAllWarps(): List<String> {
        val warps = mutableSetOf<String>()
        Bukkit.getWorlds().forEach { world ->
            val keys = world.persistentDataContainer.keys
            warps += keys
                .filter { it.namespace == plugin.name.lowercase() && it.key.startsWith(KEY_PREFIX) }
                .map { it.key.removePrefix(KEY_PREFIX) }
        }
        return warps.toList()
    }

    // 获取指定世界的 Warp 名列表
    fun listWarpsInWorld(world: World): List<String> {
        return world.persistentDataContainer.keys
            .filter { it.namespace == plugin.name.lowercase() && it.key.startsWith(KEY_PREFIX) }
            .map { it.key.removePrefix(KEY_PREFIX) }
    }
}
