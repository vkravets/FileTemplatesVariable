package org.vkravets.idea.project.filetemplate;

import com.intellij.ide.fileTemplates.DefaultTemplatePropertiesProvider;
import com.intellij.psi.PsiDirectory;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * Author: Vladimir Kravets
 * E-Mail: vova.kravets@gmail.com
 * Date: 2/14/14
 * Time: 5:52 PM
 */
public class PerProjectTemplatePropertiesProvider implements DefaultTemplatePropertiesProvider {
    @Override
    public void fillProperties(PsiDirectory psiDirectory, Properties properties) {
        PerProjectTemplateManager manager = PerProjectTemplateManager.getInstance(psiDirectory.getProject());
        Properties projectVariables = manager.getProjectVariables();
        properties.putAll(projectVariables);
    }
}
