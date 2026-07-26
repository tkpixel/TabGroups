package org.jetbrains.plugins.template.actions

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.service
import org.jetbrains.plugins.template.models.TabGroup
import org.jetbrains.plugins.template.services.TabGroupStateService
import com.intellij.ui.EditorNotifications

class AddToExistingGroupActionGroup : ActionGroup("Add to Existing Group", true) {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        val project = e?.project ?: return emptyArray()
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return emptyArray()

        val service = project.service<TabGroupStateService>()
        val groups = service.getGroups()

        return groups.map { group ->
            object : AnAction(group.name) {
                override fun getActionUpdateThread(): ActionUpdateThread {
                    return ActionUpdateThread.BGT
                }

                override fun actionPerformed(e: AnActionEvent) {
                    service.assignFileToGroup(file.url, group.id)
                    EditorNotifications.getInstance(project).updateNotifications(file)
                }
            }
        }.toTypedArray()
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)

        if (project == null || file == null) {
            e.presentation.isEnabledAndVisible = false
            return
        }

        val service = project.service<TabGroupStateService>()
        val groups = service.getGroups()
        e.presentation.isEnabledAndVisible = groups.isNotEmpty()
    }
}
