package org.jetbrains.plugins.template.services

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

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
}
