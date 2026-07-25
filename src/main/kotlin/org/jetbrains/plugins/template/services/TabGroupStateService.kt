package org.jetbrains.plugins.template.services

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

import org.jetbrains.plugins.template.models.TabGroup
import org.jetbrains.plugins.template.models.TabState

@Service(Service.Level.PROJECT)
@State(
    name = "TabGroupSettings",
    storages = [Storage("TabGroups.xml")]
)
class TabGroupStateService : PersistentStateComponent<TabState> {

    private var myState = TabState()

    override fun getState(): TabState {
        return myState
    }

    override fun loadState(state: TabState) {
        myState = state
    }

    fun assignFileToGroup(fileUrl: String, groupId: String) {
        myState.fileToGroupMap[fileUrl] = groupId
    }

    fun removeFileFromGroup(fileUrl: String) {
        myState.fileToGroupMap.remove(fileUrl)
    }

    fun getGroups(): List<TabGroup> {
        return myState.groups.toList()
    }

    fun addGroup(group: TabGroup) {
        myState.groups.add(group)
    }

    fun removeGroup(groupId: String) {
        myState.groups.removeIf { it.id == groupId }
        // Also remove assignments for this group
        myState.fileToGroupMap.entries.removeIf { it.value == groupId }
    }

    fun updateGroup(group: TabGroup) {
        val index = myState.groups.indexOfFirst { it.id == group.id }
        if (index != -1) {
            myState.groups[index] = group
        }
    }

    fun getGroupById(groupId: String): TabGroup? {
        return myState.groups.find { it.id == groupId }
    }

    fun getGroupForFile(fileUrl: String): TabGroup? {
        val groupId = myState.fileToGroupMap[fileUrl] ?: return null
        return getGroupById(groupId)
    }
}
