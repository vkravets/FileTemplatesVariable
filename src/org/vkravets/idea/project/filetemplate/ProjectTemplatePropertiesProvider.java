package org.vkravets.idea.project.filetemplate;

import com.intellij.ide.fileTemplates.DefaultTemplatePropertiesProvider;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;

import java.util.Map;
import java.util.Properties;

import static java.util.stream.Collectors.toMap;

/**
 * Created by IntelliJ IDEA.
 * Author: Vladimir Kravets
 * E-Mail: vova.kravets@gmail.com
 * Date: 2/14/14
 * Time: 5:52 PM
 */
public class ProjectTemplatePropertiesProvider implements DefaultTemplatePropertiesProvider {
    @Override
    public void fillProperties(PsiDirectory psiDirectory, Properties properties) {
        final Project project = psiDirectory.getProject();
        final ProjectTemplateVariableManager manager = project.getService(ProjectTemplateVariableManager.class);

        final VariablesConfigurationState projectVariables = manager.getProjectVariables();
        final Map<String, String> variablesMap = projectVariables.templateVariables.stream()
                                                                                   .collect(toMap(TemplateVariable::getName,
                                                                                                  TemplateVariable::getValue));
        properties.putAll(variablesMap);
    }
}
