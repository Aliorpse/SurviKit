package me.aliorpse.survikit.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

object TextColor {
    private val LEGACY_SERIALIZER = LegacyComponentSerializer.builder()
        .character('&')
        .hexColors()
        .useUnusualXRepeatedCharacterHexFormat()
        .build()

    fun parse(text: String?): Component {
        if (text == null || text.isEmpty()) {
            return Component.empty()
        }
        return LEGACY_SERIALIZER.deserialize(text)
    }
}