package org.jetbrains.idea.project.filetemplate.configuration;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.AnActionButtonRunnable;
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
                setAddAction(new AnActionButtonRunnable() {
                    @Override
                    public void run(AnActionButton anActionButton) {
                        table.addVariable();
                    }
                }).
                setRemoveAction(new AnActionButtonRunnable() {
                    @Override
                    public void run(AnActionButton anActionButton) {
                        table.removeSelectedMacros();
                    }
                }).
                setEditAction(new AnActionButtonRunnable() {
                    @Override
                    public void run(AnActionButton anActionButton) {
                        table.editVariable();
                    }
                }).
                disableUpDownActions().createPanel(), BorderLayout.CENTER);

    }

    public JComponent getPanel() {
        return wholePanel;
    }

    public void commit() {
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                table.commit();
            }
        });
    }

    public void reset() {
        table.reset();
    }

    public boolean isModified() {
        return table.isModified();
    }
}
