package org.jetbrains.plugins.template.ui

import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.impl.EditorTabTitleProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.plugins.template.services.TabGroupStateService

class TabGroupTitleProvider : EditorTabTitleProvider {
    override fun getEditorTabTitle(project: Project, file: VirtualFile): String? {
        val stateService = project.service<TabGroupStateService>()
        val state = stateService.state

        val fileUrl = file.url
        val groupId = state.fileToGroupMap[fileUrl] ?: return null

        val group = state.groups.find { it.id == groupId } ?: return null
        val groupName = group.name

        if (groupName.isEmpty()) return null

        return "[$groupName] ${file.presentableName}"
    }
}
