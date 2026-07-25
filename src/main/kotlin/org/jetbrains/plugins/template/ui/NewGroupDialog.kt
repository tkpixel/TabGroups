package org.jetbrains.plugins.template.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.bindText
import com.intellij.util.ui.ColorIcon
import org.jetbrains.plugins.template.models.TabGroup
import java.awt.Component
import javax.swing.DefaultListCellRenderer
import javax.swing.JComponent
import javax.swing.JList

class NewGroupDialog(project: Project?, existingGroup: TabGroup? = null) : DialogWrapper(project) {

    var groupName: String = existingGroup?.name ?: ""

    // We try to match existing group color string to TabColor enum, otherwise default to BLUE
    private var selectedTabColor: TabColor = existingGroup?.color?.takeIf { it.isNotBlank() }?.let { colorStr ->
        TabColor.fromString(colorStr)
    } ?: TabColor.BLUE

    private val colorComboBox = ComboBox(TabColor.values()).apply {
        selectedItem = this@NewGroupDialog.selectedTabColor

        renderer = object : DefaultListCellRenderer() {
            override fun getListCellRendererComponent(
                list: JList<*>?,
                value: Any?,
                index: Int,
                isSelected: Boolean,
                cellHasFocus: Boolean
            ): Component {
                val c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
                if (value is TabColor) {
                    text = value.displayName
                    icon = ColorIcon(16, value.toJBColor())
                }
                return c
            }
        }

        addActionListener {
            this@NewGroupDialog.selectedTabColor = this.selectedItem as TabColor
        }
    }

    init {
        title = "New Tab Group"
        init()
    }

    override fun createCenterPanel(): JComponent {
        return panel {
            row("Group Name:") {
                textField()
                    .bindText(::groupName)
                    .focused()
            }
            row("Group Color:") {
                cell(colorComboBox)
            }
        }
    }

    fun getTabGroup(): TabGroup {
        return TabGroup(name = groupName, color = selectedTabColor.name)
    }
}
