package org.jetbrains.plugins.template.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.service
import org.jetbrains.plugins.template.services.TabGroupStateService
import org.jetbrains.plugins.template.ui.NewGroupDialog
import com.intellij.ui.EditorNotifications

class AddToNewGroupAction : AnAction("Add to New Group...") {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return

        val dialog = NewGroupDialog(project)
        if (dialog.showAndGet()) {
            val group = dialog.getTabGroup()
            if (group.name.isNotBlank()) {
                val service = project.service<TabGroupStateService>()
                service.addGroup(group)
                service.assignFileToGroup(file.url, group.id)
                // Trigger update
                EditorNotifications.getInstance(project).updateNotifications(file)
            }
        }
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)
        e.presentation.isEnabledAndVisible = project != null && file != null
    }
}
