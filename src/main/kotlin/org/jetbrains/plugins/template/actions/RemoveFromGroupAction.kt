package org.jetbrains.plugins.template.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.service
import org.jetbrains.plugins.template.services.TabGroupStateService
import com.intellij.ui.EditorNotifications

class RemoveFromGroupAction : AnAction("Remove from Group") {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return

        val service = project.service<TabGroupStateService>()
        service.removeFileFromGroup(file.url)
        EditorNotifications.getInstance(project).updateNotifications(file)
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)

        if (project == null || file == null) {
            e.presentation.isEnabledAndVisible = false
            return
        }

        val service = project.service<TabGroupStateService>()
        val group = service.getGroupForFile(file.url)
        e.presentation.isEnabledAndVisible = group != null
    }
}
