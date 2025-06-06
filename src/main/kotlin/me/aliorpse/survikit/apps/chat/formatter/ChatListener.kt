package me.aliorpse.survikit.apps.chat.formatter

import io.papermc.paper.event.player.AsyncChatEvent
import me.aliorpse.survikit.utils.TextColor
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.TreeMap

class ChatListener : Listener {

    private val chineseToEnglishPunctuation = TreeMap<Char, Char>().apply {
        // 常用标点
        put('，', ',')
        put('？', '?')
        put('！', '!')
        put('；', ';')
        put('：', ':')

        // 引号类
        put('“', '"')
        put('”', '"')
        put('‘', '\'')
        put('’', '\'')

        // 括号类
        put('（', '(')
        put('）', ')')
        put('【', '[')
        put('】', ']')
        put('｛', '{')
        put('｝', '}')

        // 特殊符号
        put('～', '~')
        put('—', '-')
    }

    @EventHandler
    fun onAsyncChat(e: AsyncChatEvent) {
        val originalMessage = e.message()
        val plainText = PlainTextComponentSerializer.plainText().serialize(originalMessage)

        val processedText = plainText.map { char ->
            chineseToEnglishPunctuation.getOrDefault(char, char)
        }.joinToString("")

        val newMessage: Component = TextColor.parse(processedText)
            .style(originalMessage.style())

        e.message(newMessage)
    }
}