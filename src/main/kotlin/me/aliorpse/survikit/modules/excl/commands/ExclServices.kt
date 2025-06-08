package me.aliorpse.survikit.modules.excl.commands

import me.aliorpse.survikit.SurviKit
import me.aliorpse.survikit.utils.TextColor
import me.aliorpse.survikit.utils.parseLocation
import me.aliorpse.survikit.utils.parseString
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import java.text.DecimalFormat

class ExclServices {

    private val config = SurviKit.instance.config

    fun showHelp(pl: Player) {
        val cmdArgs = hashMapOf(
            "!" to "显示本菜单",
            "!s" to "切换 Freelook (灵魂出窍)",
            "!loc" to "分享当前位置",
            "!bs [key] [value]" to "修改当前看向方块的状态"
        )
        var msg = "§aSK §8> §f感叹号指令列表\n \n"

        cmdArgs.forEach {
            msg += "  §7${it.key} §8- §f${it.value}§r\n"
        }
        pl.sendMessage(msg)
    }

    fun freeLook(pl: Player, key: NamespacedKey, plugin: Plugin) {
        if(!(config.getBoolean("modules.exclcmds.freelook.enabled")))
            return pl.sendMessage("§aSK §8> §f该功能未启用.")

        // 命令应当在在生存/旁观者模式下使用
        if (pl.gameMode != GameMode.SURVIVAL && pl.gameMode != GameMode.SPECTATOR)
            return pl.sendMessage(
                TextColor.parse("&aSK &8> &f这个指令应在生存/旁观者模式下使用.")
            )

        val pdc = pl.persistentDataContainer
        val location = pdc.get(key, PersistentDataType.STRING)?.parseLocation()

        when {
            location == null -> {
                if (pl.gameMode == GameMode.SPECTATOR) {
                    return pl.sendMessage(
                        TextColor.parse("&aSK &8> &f你当前未处于 Freelook 状态. 请在生存模式下使用本功能.")
                    )
                }
                Bukkit.getScheduler().runTask(plugin, Runnable {
                    pdc.set(key, PersistentDataType.STRING, pl.location.parseString())

                    pl.gameMode = GameMode.SPECTATOR
                    pl.playerListName(TextColor.parse("&b&lFL&r " + pl.name))
                    pl.sendMessage(
                        TextColor.parse("&aSK &8> &fFreelook 已激活. 再次使用 !s 以退出")
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
                            TextColor.parse("&aSK &8> &fFreelook 已退出")
                        )
                    } catch (e: Exception) {
                        pl.sendMessage(
                            TextColor.parse("&aSK &8> &cFreelook 退出失败: ${e.message}")
                        )
                    }
                })
            }
        }
    }

    fun showLocation(pl: Player) {
        val df = DecimalFormat("#.#")
        fun Double.toFixed(): String = df.format(this)

        val dimension = when(pl.world.name) {
            "world" -> "&2主世界"
            "the_nether" -> "&c下界"
            "the_end" -> "&d末地"
            else -> pl.world.name
        }

        // 坐标文本与复制内容
        val coordsDisplay = "(${pl.location.x.toFixed()}, ${pl.location.y.toFixed()}, ${pl.location.z.toFixed()})"
        val coordsCopy = "${pl.location.x.toFixed()} ${pl.location.y.toFixed()} ${pl.location.z.toFixed()}"

        val message = TextColor.parse("&e# &7${pl.name} 分享坐标 &7- &f$dimension ")
            .append(
                TextColor.parse("&e$coordsDisplay")
                    .hoverEvent(HoverEvent.showText(TextColor.parse("点击复制坐标")))
                    .clickEvent(ClickEvent.copyToClipboard(coordsCopy))
            )

        Bukkit.broadcast(message)
    }

    fun blockState(pl: Player, args: List<String>, plugin: Plugin) {

        // 目标方块
        val block = pl.getTargetBlockExact(4) ?: run {
            pl.sendMessage("§aSK §8> §f你当前没有看向任何方块. (最大检测距离4)")
            return
        }

        // 允许方块
        val allowedBlocks = config.getList("modules.exclcmds.change-block-state.enabled-blocks")!!
        if (block.type.name !in allowedBlocks && !pl.isOp)
            return pl.sendMessage("§aSK §8> §f该方块不允许修改状态.")


        // 解析参数
        val property = args.getOrNull(0)?.replaceFirstChar { it.uppercaseChar() } ?:
            return pl.sendMessage("§aSK §8> §c缺少属性名")
        val rawValue = args.getOrNull(1) ?:
            return pl.sendMessage("§aSK §8> §c缺少属性值")

        val data = block.blockData

        // 反射, 启动!
        try {
            // 查找方法
            val methodName = "set$property"
            val method = data.javaClass.methods.firstOrNull {
                it.name == methodName && it.parameterTypes.size == 1
            } ?: throw NoSuchMethodException("该方块似乎不具有 $property 属性")

            // 转换参数类型
            val paramType = method.parameterTypes[0]
            val value: Any = when {
                paramType == Boolean::class.java || paramType == java.lang.Boolean.TYPE ->
                    rawValue.toBooleanStrict()

                paramType == Int::class.java || paramType == Integer.TYPE ->
                    rawValue.toInt()

                paramType.isEnum -> {
                    val enumConstants = paramType.enumConstants
                    enumConstants.firstOrNull {
                        it.toString().equals(rawValue, ignoreCase = true)
                    } ?: throw IllegalArgumentException("无效枚举值: $rawValue")
                }

                paramType == String::class.java ->
                    rawValue

                else -> throw IllegalArgumentException("不支持的方法参数类型: ${paramType.simpleName}")
            }

            // 主线程更新
            Bukkit.getScheduler().runTask(plugin, Runnable {
                try {
                    val newData = data.clone()
                    method.invoke(newData, value)

                    block.blockData = newData
                    pl.sendMessage("§aSK §8> §f方块属性已更改: $property = $rawValue")
                } catch (e: Exception) {
                    pl.sendMessage("§aSK §8> §c修改失败: ${e.message}")
                    e.printStackTrace()
                }
            })
        } catch (e: Exception) {
            pl.sendMessage("§aSK §8> §c错误: ${e.message}")
        }
    }
}