package org.jetbrains.plugins.template.ui

import com.intellij.ui.JBColor
import com.intellij.ui.ColorUtil
import java.awt.Color

enum class TabColor(val displayName: String, val lightHex: String, val darkHex: String) {
    GREY("Grey", "#bdc1c6", "#5f6368"),
    BLUE("Blue", "#8ab4f8", "#1a73e8"),
    RED("Red", "#f28b82", "#d93025"),
    YELLOW("Yellow", "#fde293", "#f29900"),
    GREEN("Green", "#81c995", "#188038"),
    PINK("Pink", "#ff8bcb", "#d81b60"),
    PURPLE("Purple", "#c58af9", "#9334e6"),
    CYAN("Cyan", "#78d9ec", "#12b5cb"),
    ORANGE("Orange", "#fcad70", "#e37400");

    fun toJBColor(): JBColor {
        val lightColor = try { ColorUtil.fromHex(lightHex) } catch (e: Exception) { Color.GRAY }
        val darkColor = try { ColorUtil.fromHex(darkHex) } catch (e: Exception) { Color.GRAY }
        return JBColor(lightColor, darkColor)
    }

    companion object {
        fun fromString(value: String): TabColor? {
            return entries.find { it.name.equals(value, ignoreCase = true) || it.displayName.equals(value, ignoreCase = true) }
        }
    }
}
