package org.jetbrains.plugins.template.listeners

import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.plugins.template.services.TabGroupStateService

class TabGroupFileEditorListener : FileEditorManagerListener {
    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        val project = source.project
        val stateService = project.service<TabGroupStateService>()
        val fileUrl = file.url
        val groupId = stateService.state.fileToGroupMap[fileUrl]
        if (groupId != null) {
            applyVisualDesign(source, file, groupId)
        }
    }

    override fun fileClosed(source: FileEditorManager, file: VirtualFile) {
        // Cache updates or logic on file closed could be added here
    }

    private fun applyVisualDesign(source: FileEditorManager, file: VirtualFile, groupId: String) {
        source.updateFilePresentation(file)
    }
}
