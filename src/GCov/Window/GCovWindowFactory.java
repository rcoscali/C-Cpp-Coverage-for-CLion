package GCov.Window;

import GCov.Data.GCovCoverageGatherer;
import GCov.Messaging.GCoverageRunEnded;
import GCov.State.EditorState;
import GCov.State.ShowNonProjectSourcesState;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class GCovWindowFactory implements ToolWindowFactory {

    static private GCovCoverageGatherer gatherer;
    private ToolWindow m_toolWindow;
    private JPanel m_panel;
    private JCheckBox m_showNonProjectSources;
    private JToolBar.Separator m_seperator;
    private JToolBar m_toolbar;
    private CoverageTree m_tree;
    private JScrollPane m_scrollPane;
    private JCheckBox m_showInEditor;
    private JButton m_clear;

    @Override
    public void init(ToolWindow window) {
        m_toolWindow = window;
        window.setAvailable(false,null);
    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        gatherer = GCovCoverageGatherer.getInstance(project);
        project.getMessageBus().connect().subscribe(GCoverageRunEnded.GCOVERAGE_RUN_ENDED_TOPIC,
                cmakeDirectory -> {
                    m_tree.resetModel();
                    m_tree.getEmptyText().setText("Gathering coverage data...");
                    gatherer.setBuildDirectory(project.getBasePath() + "/" + cmakeDirectory);
                    gatherer.gather(() -> gatherer.display(m_tree));
                    ApplicationManager.getApplication().invokeLater(() -> {
                        m_toolWindow.setAvailable(true,null);
                        m_toolWindow.show(null);
                    });
                });
        return false;
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        m_tree.addMouseListener(new CoverageTree.TreeMouseHandler(project,m_tree));
        m_showNonProjectSources.addItemListener(e -> {
            ShowNonProjectSourcesState.getInstance(project).showNonProjectSources = m_showNonProjectSources.isSelected();
            gatherer.display(m_tree);
        });
        m_showInEditor.setSelected(EditorState.getInstance(project).showInEditor);
        m_showInEditor.addItemListener(e -> {
            EditorState.getInstance(project).showInEditor = m_showInEditor.isSelected();
            gatherer.updateEditor();
        });
        m_clear.addActionListener(e -> {
            gatherer.clearCoverage();
            m_tree.resetModel();
        });
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(m_panel, "", false);
        toolWindow.getContentManager().addContent(content);
    }

    private void createUIComponents() {
        m_tree = new CoverageTree(new DefaultMutableTreeNode("empty-root"));
        m_tree.getEmptyText().setText("No coverage data found. Did you compile with \"-fprofile-arcs -ftest-coverage\"?");
    }
}
