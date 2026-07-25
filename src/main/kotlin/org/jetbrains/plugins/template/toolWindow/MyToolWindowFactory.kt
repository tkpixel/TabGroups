package org.jetbrains.plugins.template.toolWindow

import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.ContentFactory
import org.jetbrains.plugins.template.models.TabGroup
import org.jetbrains.plugins.template.services.TabGroupStateService
import java.awt.BorderLayout
import java.awt.Component
import java.awt.FlowLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

class MyToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val myToolWindow = MyToolWindow(project)
        val content = ContentFactory.getInstance().createContent(myToolWindow.getContent(), null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    class MyToolWindow(private val project: Project) {

        private val stateService = project.service<TabGroupStateService>()
        private val listModel = DefaultListModel<TabGroup>()
        private val groupsList = JBList(listModel)
        private val mainPanel = JPanel(BorderLayout())

        init {
            groupsList.cellRenderer = GroupCellRenderer()
            groupsList.selectionMode = ListSelectionModel.SINGLE_SELECTION

            groupsList.addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (e.clickCount == 2) {
                        val index = groupsList.locationToIndex(e.point)
                        if (index >= 0 && groupsList.getCellBounds(index, index)?.contains(e.point) == true) {
                            val group = listModel.getElementAt(index)
                            if (group.isCollapsed) {
                                expandGroup(group)
                            } else {
                                collapseGroup(group)
                            }
                        }
                    }
                }
            })

            val topPanel = JPanel(FlowLayout(FlowLayout.LEFT))
            val refreshButton = JButton("Refresh")
            refreshButton.addActionListener { updateList() }
            topPanel.add(refreshButton)

            mainPanel.add(topPanel, BorderLayout.NORTH)
            mainPanel.add(JBScrollPane(groupsList), BorderLayout.CENTER)

            updateList()
        }

        fun getContent(): JComponent = mainPanel

        private fun updateList() {
            listModel.clear()
            stateService.getGroups().forEach { listModel.addElement(it) }
        }

        private fun collapseGroup(group: TabGroup) {
            group.isCollapsed = true
            stateService.updateGroup(group)

            val fileUrls = stateService.state.fileToGroupMap.filterValues { it == group.id }.keys
            val fileEditorManager = FileEditorManager.getInstance(project)
            val virtualFileManager = VirtualFileManager.getInstance()

            for (url in fileUrls) {
                val file = virtualFileManager.findFileByUrl(url)
                if (file != null) {
                    fileEditorManager.closeFile(file)
                }
            }
            updateList()
        }

        private fun expandGroup(group: TabGroup) {
            group.isCollapsed = false
            stateService.updateGroup(group)

            val fileUrls = stateService.state.fileToGroupMap.filterValues { it == group.id }.keys
            val fileEditorManager = FileEditorManager.getInstance(project)
            val virtualFileManager = VirtualFileManager.getInstance()

            for (url in fileUrls) {
                val file = virtualFileManager.findFileByUrl(url)
                if (file != null) {
                    fileEditorManager.openFile(file, true)
                }
            }
            updateList()
        }

        inner class GroupCellRenderer : DefaultListCellRenderer() {
            override fun getListCellRendererComponent(
                list: JList<*>?,
                value: Any?,
                index: Int,
                isSelected: Boolean,
                cellHasFocus: Boolean
            ): Component {
                val c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
                if (value is TabGroup) {
                    val status = if (value.isCollapsed) "(Collapsed)" else "(Expanded)"
                    text = "${value.name} $status"
                }
                return c
            }
        }
    }
}
