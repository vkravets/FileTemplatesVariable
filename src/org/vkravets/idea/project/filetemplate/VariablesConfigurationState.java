package org.vkravets.idea.project.filetemplate;

import com.intellij.util.xmlb.annotations.Tag;
import com.intellij.util.xmlb.annotations.XCollection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA. Author: vkravets E-Mail: Date: 13.02.2021 Time: 19:48
 */
public class VariablesConfigurationState {

    @Tag("templateVariables")
    @XCollection(propertyElementName = "templateVariables",
                 elementName = "variable",
                 valueAttributeName = "",
                 elementTypes = TemplateVariable.class,
                 style = XCollection.Style.v2)
    public List<TemplateVariable> templateVariables = new ArrayList<>();

    public VariablesConfigurationState() {

    }
}
