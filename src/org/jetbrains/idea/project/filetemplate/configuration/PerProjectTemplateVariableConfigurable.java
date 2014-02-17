package org.jetbrains.idea.project.filetemplate.configuration;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * Author: Vladimir Kravets
 * E-Mail: vova.kravets@gmail.com
 * Date: 2/14/14
 * Time: 7:38 PM
 */
public class PerProjectTemplateVariableConfigurable implements Configurable.NoScroll, Configurable {

    private Project project;
    private TemplateVariablesConfigurationPanel panel;

    public PerProjectTemplateVariableConfigurable(Project project) {
        this.project = project;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Template Variables";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        panel = new TemplateVariablesConfigurationPanel(project);
        return panel.getPanel();
    }

    @Override
    public boolean isModified() {
        return panel.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        panel.commit();
    }

    @Override
    public void reset() {
        panel.reset();
    }

    @Override
    public void disposeUIResources() {
        panel = null;
    }
}
