package org.jetbrains.plugins.template.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.JBColor
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.bindText
import org.jetbrains.plugins.template.models.TabGroup
import java.awt.Color
import javax.swing.JComponent
import com.intellij.ui.ColorPanel

class NewGroupDialog(project: Project?, existingGroup: TabGroup? = null) : DialogWrapper(project) {

    var groupName: String = existingGroup?.name ?: ""
    private var selectedColor: Color? = existingGroup?.color?.takeIf { it.isNotBlank() }?.let {
        try {
            Color.decode(it)
        } catch (e: Exception) {
            JBColor.BLUE
        }
    } ?: JBColor.BLUE

    private val colorPanel = ColorPanel().apply {
        selectedColor = this@NewGroupDialog.selectedColor
        addActionListener {
            this@NewGroupDialog.selectedColor = this.selectedColor
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
                cell(colorPanel)
            }
        }
    }

    fun getTabGroup(): TabGroup {
        val hexColor = selectedColor?.let {
            String.format("#%02x%02x%02x", it.red, it.green, it.blue)
        } ?: ""
        return TabGroup(name = groupName, color = hexColor)
    }
}
