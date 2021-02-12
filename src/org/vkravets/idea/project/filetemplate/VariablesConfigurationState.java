package org.vkravets.idea.project.filetemplate;

import com.intellij.util.xmlb.annotations.Tag;
import com.intellij.util.xmlb.annotations.XCollection;

import java.util.List;

/**
 * Created by IntelliJ IDEA. Author: vkravets E-Mail: Date: 13.02.2021 Time: 19:48
 */
@Tag("templateVariables")
public class VariablesConfigurationState {

    @XCollection(propertyElementName = "templateVariables",
                 elementName = "variable",
                 valueAttributeName = "",
                 elementTypes = TemplateVariable.class)
    List<TemplateVariable> templateVariables;

    public VariablesConfigurationState() {

    }

    public List<TemplateVariable> getTemplateVariables() {
        return templateVariables;
    }
}
