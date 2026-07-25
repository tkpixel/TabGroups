package org.jetbrains.plugins.template.models

import java.util.UUID

/**
 * Represents a group of tabs.
 * All properties are mutable and have default values for IntelliJ's XML serialization.
 */
data class TabGroup(
    var id: String = UUID.randomUUID().toString(),
    var name: String = "",
    var color: String = "", // Hex-Code or predefined IntelliJ color
    var isCollapsed: Boolean = false
)
