package me.aliorpse.survikit.modules.commands

import me.aliorpse.survikit.SurviKit
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.lang.reflect.InvocationTargetException

class BlockStateCommand : CommandExecutor, TabCompleter {
    private val config = SurviKit.Companion.instance.config

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) return false
        val pl = sender

        // 目标方块
        val block = pl.getTargetBlockExact(4) ?: run {
            pl.sendMessage("§aSK §8> §f你当前没有看向任何方块. (最大检测距离4)")
            return false
        }

        // 允许方块
        val allowedBlocks = config.getList("modules.exclcmds.change-block-state.enabled-blocks")!!
        if (block.type.name !in allowedBlocks && !pl.isOp) {
            pl.sendMessage("§aSK §8> §f该方块不允许修改状态.")
            return false
        }

        // 解析参数
        val property = args.getOrNull(0)?.replaceFirstChar { it.uppercaseChar() }
            ?: run {
                pl.sendMessage("§aSK §8> §c缺少属性名")
                return false
            }
        val rawValue = args.getOrNull(1)
            ?: run {
                pl.sendMessage("§aSK §8> §c缺少属性值")
                return false
            }

        val data = block.blockData

        // 反射, 启动!
        try {
            // 查找方法
            val methodName = "set$property"
            val method = data::class.java.methods.firstOrNull {
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
            Bukkit.getScheduler().runTask(
                SurviKit.instance,
                Runnable {
                    try {
                        val newData = data.clone()
                        method.invoke(newData, value)

                        block.blockData = newData
                        pl.sendMessage("§aSK §8> §f方块属性已更改: ${property.replaceFirstChar { it2 -> it2.lowercaseChar() }} = $rawValue")
                    } catch (_: InvocationTargetException) {
                        pl.sendMessage("§aSK §8> §c修改失败, 请检查输入的参数是否合理 (合法)")
                    }
                }
            )
        } catch (e: Exception) {
            pl.sendMessage("§aSK §8> §c错误: ${e.message}")
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String?>? {
        if (sender !is Player) return emptyList()
        val pl = sender

        val block = pl.getTargetBlockExact(4) ?: return emptyList()
        val data = block.blockData

        return when (args.size) {
            1 -> {
                val prefix = args.getOrNull(0)?.lowercase() ?: ""

                val propertyList = data::class.java.methods
                    .filter { it.name.startsWith("set") && it.parameterTypes.size == 1 }
                    .map {
                        it.name.removePrefix("set")
                            .replaceFirstChar { it2 -> it2.lowercaseChar() }
                    }
                    .filter { it.startsWith(prefix) }

                return propertyList
            }

            2 -> {
                val property = "set${args[0].replaceFirstChar { it.uppercaseChar() }}"

                val method = data::class.java.methods.firstOrNull {
                    it.name == property && it.parameterTypes.size == 1
                } ?: return emptyList()

                val paramType = method.parameterTypes[0]

                return when {
                    paramType == Boolean::class.java || paramType == java.lang.Boolean.TYPE ->
                        listOf("true", "false")

                    paramType == Int::class.java || paramType == Integer.TYPE ->
                        listOf("[整数]")

                    paramType.isEnum ->
                        paramType.enumConstants?.map { it.toString().lowercase() } ?: emptyList()

                    paramType == String::class.java ->
                        listOf("[字符串]")

                    else -> emptyList()
                }
            }

            else -> emptyList()
        }
    }
}
