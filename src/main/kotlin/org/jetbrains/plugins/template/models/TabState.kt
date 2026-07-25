package org.jetbrains.plugins.template.models

import com.intellij.util.xmlb.annotations.XMap

/**
 * State class to hold tab groups and file assignments.
 * Properties are mutable for IntelliJ's XML serialization.
 */
class TabState {
    var groups: MutableList<TabGroup> = mutableListOf()

    // Maps file URL (persistent, not absolute path) to TabGroup.id
    @XMap(propertyElementName = "fileAssignments", keyAttributeName = "fileUrl", valueAttributeName = "groupId")
    var fileToGroupMap: MutableMap<String, String> = mutableMapOf()
}
