package gcov.state

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil

@State(name = "GCoverageShowInEditor")
data class EditorState(var showInEditor: Boolean = true) : PersistentStateComponent<EditorState> {

    override fun getState(): EditorState = this

    override fun loadState(editorState: EditorState) = XmlSerializerUtil.copyBean(editorState, this)

    companion object {

        fun getInstance(project: Project): EditorState = ServiceManager.getService(project, EditorState::class.java)!!
    }
}
