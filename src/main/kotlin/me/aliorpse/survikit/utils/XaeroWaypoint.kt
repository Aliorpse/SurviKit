package me.aliorpse.survikit.utils

data class XaeroWaypoint(
    val prefix: String,      // "xaero-waypoint" (固定前缀)
    val name: String,        // 航点名称
    val marker: String,      // 标记符号（单个字符）
    val x: Int,             // X坐标
    val y: Int,             // Y坐标
    val z: Int,             // Z坐标
    val color: Int,          // 颜色值 (0-15 或 -1)
    val useYaw: Boolean,    // 是否使用偏航角
    val yaw: Int,           // 偏航角度数
    val dimension: String    // 维度名称
) {
    companion object {
        fun parse(input: String): XaeroWaypoint? {
            val parts = input.split(':', limit = 10)
            if (parts.size < 10) return null

            val color = when(parts[6].toInt()) {
                in 0..15 -> parts[6].toInt()
                else -> parts[6].toInt() % 16
            }

            val dimension = parts[9].replace("Internal-", "").replace("-waypoints", "")

            return try {
                XaeroWaypoint(
                    prefix = parts[0],
                    name = parts[1],
                    marker = parts[2],
                    x = parts[3].toInt(),
                    y = parts[4].toInt(),
                    z = parts[5].toInt(),
                    color = color,
                    useYaw = parts[7].toBooleanStrict(),
                    yaw = parts[8].toInt(),
                    dimension = dimension
                )
            } catch (_: Exception) {
                null
            }
        }
    }
}