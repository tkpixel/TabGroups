# Evaluation of Custom Drag & Drop Tab Grouping in IntelliJ

## Objective
Investigate whether the standard tab dragging functionality in IntelliJ can be manipulated so that tabs automatically move into a group when visually dragged next to it.

## Findings
IntelliJ's `EditorTabbedContainer` and standard tab drag-and-drop mechanisms are heavily controlled by core UI and window management logic (e.g., `JBTabs`, `DragHelper`, `TabInfo`).

### Limitations:
1. **Restricted Internal APIs:** The actual drop targets, mouse drag events on tabs, and the visual feedback during drag & drop are tightly coupled in internal JetBrains UI components which are not exposed for plugins to intercept easily or safely.
2. **Tab Grouping is Logical, not just Visual:** In our plugin, tab groups are logical states tied to the project (a `fileUrl -> groupId` mapping). Dragging a tab in the IntelliJ UI merely moves the visual representation (the `EditorWindow` tab order) but does not inherently provide a public extension point to notify *where* it was dropped relative to other files that belong to specific custom groups.
3. **Complexity of Tab Dragging:** Moving a tab can result in splitting the editor, creating new windows, or moving tabs across editor instances. Injecting custom logic into this flow using workarounds (like adding generic AWT listeners or AOP/Reflection) breaks frequently and is rejected by the IntelliJ Plugin Verifier.

## Conclusion
Manipulating standard drag & drop for custom semantic groupings is **highly unfeasible** within the supported API boundaries. It requires intercepting highly restrictive internal platform mechanisms (`JBTabs`, `EditorTabbedContainer`).

Instead, the recommended approach is to rely on custom actions (e.g., right-click context menu) to add/remove files to groups, which is stable, supported, and predictable.
