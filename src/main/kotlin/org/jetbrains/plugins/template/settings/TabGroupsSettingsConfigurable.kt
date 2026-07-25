package org.jetbrains.plugins.template.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.components.service
import com.intellij.ui.CollectionListModel
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import com.intellij.ui.dsl.builder.panel
import org.jetbrains.plugins.template.models.TabGroup
import org.jetbrains.plugins.template.services.TabGroupStateService
import org.jetbrains.plugins.template.ui.NewGroupDialog
import javax.swing.JComponent
import javax.swing.JPanel
import java.awt.BorderLayout

class TabGroupsSettingsConfigurable(private val project: Project) : Configurable {

    private val service = project.service<TabGroupStateService>()
    private val listModel = CollectionListModel<TabGroup>()
    private val groupList = JBList(listModel).apply {
        setCellRenderer { list, value, index, isSelected, cellHasFocus ->
            val label = javax.swing.JLabel(value.name)
            label.isOpaque = true
            if (isSelected) {
                label.background = list.selectionBackground
                label.foreground = list.selectionForeground
            } else {
                label.background = list.background
                label.foreground = list.foreground
            }
            // Add a small colored square if possible, but keep it simple
            if (value.color.isNotBlank()) {
                try {
                    label.text = "<html><font color='${value.color}'>\u25A0</font> ${value.name}</html>"
                } catch (e: Exception) {
                    // Ignore parsing error
                }
            }
            label
        }
    }

    private var modified = false
    private var panel: JPanel? = null

    override fun getDisplayName(): String = "Tab Groups"

    override fun createComponent(): JComponent {
        val decorator = ToolbarDecorator.createDecorator(groupList)
            .setAddAction {
                val dialog = NewGroupDialog(project)
                if (dialog.showAndGet()) {
                    val group = dialog.getTabGroup()
                    if (group.name.isNotBlank()) {
                        listModel.add(group)
                        modified = true
                    }
                }
            }
            .setRemoveAction {
                val selectedIndices = groupList.selectedIndices
                for (i in selectedIndices.reversed()) {
                    listModel.remove(i)
                }
                modified = true
            }
            .setEditAction {
                val selectedIndex = groupList.selectedIndex
                if (selectedIndex >= 0) {
                    val group = listModel.getElementAt(selectedIndex)
                    val dialog = NewGroupDialog(project, group)
                    if (dialog.showAndGet()) {
                        val newGroup = dialog.getTabGroup().copy(id = group.id) // preserve ID
                        if (newGroup.name.isNotBlank()) {
                            listModel.setElementAt(newGroup, selectedIndex)
                            modified = true
                        }
                    }
                }
            }
            .disableUpDownActions()

        val listPanel = decorator.createPanel()

        panel = JPanel(BorderLayout()).apply {
            add(panel {
                row {
                    label("Manage Tab Groups:")
                }
            }, BorderLayout.NORTH)
            add(listPanel, BorderLayout.CENTER)
        }

        return panel!!
    }

    override fun isModified(): Boolean = modified

    override fun apply() {
        // Find deleted ones
        val currentIds = listModel.items.map { it.id }.toSet()
        val oldGroups = service.getGroups()

        for (oldGroup in oldGroups) {
            if (!currentIds.contains(oldGroup.id)) {
                service.removeGroup(oldGroup.id)
            }
        }

        // Add or update
        for (group in listModel.items) {
            if (service.getGroupById(group.id) == null) {
                service.addGroup(group)
            } else {
                service.updateGroup(group)
            }
        }
        modified = false
    }

    override fun reset() {
        listModel.removeAll()
        listModel.add(service.getGroups())
        modified = false
    }
}
