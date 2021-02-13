package org.vkravets.idea.project.filetemplate;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.openapi.components.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.util.text.UniqueNameGenerator;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.Tag;
import com.intellij.util.xmlb.annotations.Text;
import com.intellij.util.xmlb.annotations.XCollection;
import com.intellij.util.xmlb.annotations.XMap;
import org.apache.velocity.runtime.parser.ParseException;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static java.util.stream.Collectors.toMap;

/**
 * Created by IntelliJ IDEA.
 * Author: Vladimir Kravets
 * E-Mail: vova.kravets@gmail.com
 */
@State(name = "ProjectTemplateVariables",
        storages = {
                @Storage(value = StoragePathMacros.WORKSPACE_FILE),
                @Storage(value = "template/template_variables.xml")
        }
      )
public class ProjectTemplateVariableManager implements PersistentStateComponent<ProjectTemplateVariableManager> {

    @XMap(propertyElementName = TEMPLATE_VARIABLES,
          entryTagName = "variable",
          keyAttributeName = "name")
    public Map<String, String> projectVariables = new HashMap<>();

    private static final Logger logger = Logger.getInstance("#org.vkravets.idea.project.filetemplate.ProjectTemplateVariableManager");

    protected ProjectTemplateVariableManager() {
    }

    private static final String TEMPLATE_VARIABLES = "templateVariables";

    @Nullable
    @Override
    public ProjectTemplateVariableManager getState() {
        return this;
    }

    @Override
    public void loadState(ProjectTemplateVariableManager element) {
        XmlSerializerUtil.copyBean(element, this);
    }

    public static ProjectTemplateVariableManager transform(LegacyConfiguration configuration) {
        final ProjectTemplateVariableManager manager = new ProjectTemplateVariableManager();
        manager.projectVariables =
                Arrays.stream(configuration.templateVariables)
                      .collect(toMap(TemplateVariable::getName, TemplateVariable::getValue));
        return manager;
    }

    public Map<String, String> getProjectVariables() {
        return projectVariables;
    }

    public Set<String> getAllFileTemplatesVariables(Project project) {
        Set<String> result = new TreeSet<>();
        List<FileTemplate> allTemplates = new ArrayList<>();
        FileTemplateManager fileTemplateManager = FileTemplateManager.getDefaultInstance();
        FileTemplate[] templates = fileTemplateManager.getAllTemplates();
        FileTemplate[] patterns = fileTemplateManager.getAllPatterns();
        FileTemplate[] codeTemplates = fileTemplateManager.getAllCodeTemplates();
        FileTemplate[] j2eeTemplates = fileTemplateManager.getAllJ2eeTemplates();
        allTemplates.addAll(Arrays.asList(templates));
        allTemplates.addAll(Arrays.asList(codeTemplates));
        allTemplates.addAll(Arrays.asList(j2eeTemplates));
        allTemplates.addAll(Arrays.asList(patterns));

        for (FileTemplate template : allTemplates) {
            try {
                String[] variables = FileTemplateUtil.calculateAttributes(template.getText(),
                                                                          new Properties(),
                                                                          true,
                                                                          project);
                result.addAll(Arrays.asList(variables));
            }
            catch (ParseException e) {
                logger.warn("Parsing exception", e);
            }
        }
        return result;
    }

    @Tag("templateVariables")
    public static class LegacyConfiguration {

        @XCollection(propertyElementName = "templateVariables",
                     elementName = "variable",
                     valueAttributeName = "",
                     elementTypes = TemplateVariable.class)
        public TemplateVariable[] templateVariables;

        public LegacyConfiguration() {

        }
    }

    @Tag("variable")
    static class TemplateVariable {

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
    }
}
