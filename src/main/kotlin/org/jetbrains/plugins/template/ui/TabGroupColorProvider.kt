package org.jetbrains.plugins.template.ui

import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.impl.EditorTabColorProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.ColorUtil
import org.jetbrains.plugins.template.services.TabGroupStateService
import java.awt.Color

class TabGroupColorProvider : EditorTabColorProvider {
    override fun getEditorTabColor(project: Project, file: VirtualFile): Color? {
        val stateService = project.service<TabGroupStateService>()
        val state = stateService.state

        val fileUrl = file.url
        val groupId = state.fileToGroupMap[fileUrl] ?: return null

        val group = state.groups.find { it.id == groupId } ?: return null
        val colorHex = group.color

        if (colorHex.isEmpty()) return null

        return try {
            ColorUtil.fromHex(colorHex)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}
