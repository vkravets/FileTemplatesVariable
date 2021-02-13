package org.vkravets.idea.project.filetemplate.configuration;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.ToolbarDecorator;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * Author: Vladimir Kravets
 * E-Mail: vova.kravets@gmail.com
 * Date: 2/15/14
 * Time: 1:22 AM
 *
 */
public class TemplateVariablesConfigurationPanel {

    private JPanel wholePanel;
    private TemplateVariablesTable table;

    public TemplateVariablesConfigurationPanel(Project project) {
        table = new TemplateVariablesTable(project);
        wholePanel.add(ToolbarDecorator.createDecorator(table).
                setAddAction(anActionButton -> table.addVariable()).
                setRemoveAction(anActionButton -> table.removeSelectedMacros()).
                setEditAction(anActionButton -> table.editVariable()).
                disableUpDownActions().createPanel(), BorderLayout.CENTER);

    }

    public JComponent getPanel() {
        return wholePanel;
    }

    public void commit() {
        ApplicationManager.getApplication().runWriteAction(() -> table.commit());
    }

    public void reset() {
        table.reset();
    }

    public boolean isModified() {
        return table.isModified();
    }
}
