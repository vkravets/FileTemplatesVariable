package org.vkravets.idea.project.filetemplate;

import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.Tag;
import com.intellij.util.xmlb.annotations.Text;

/**
 * Created by IntelliJ IDEA. Author: vkravets E-Mail: Date: 13.02.2021 Time: 19:48
 */
@Tag("variable")
public class TemplateVariable {

    @Attribute("name")
    public String name;

    @Text
    public String value;

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public static TemplateVariable build(String name, String value) {
        TemplateVariable templateVariable = new TemplateVariable();
        templateVariable.name = name;
        templateVariable.value = value;
        return templateVariable;
    }
}
