package me.aliorpse.survikit.utils

import org.bukkit.Bukkit
import org.bukkit.Location

fun Location.parseString(): String =
    "${world?.name},$x,$y,$z,$yaw,$pitch"

@Suppress("MagicNumber")
fun String.parseLocation(): Location? {
    val parts = split(",").takeIf { it.size == 6 } ?: return null
    return try {
        val world = Bukkit.getWorld(parts[0])
        Location(
            world,
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
