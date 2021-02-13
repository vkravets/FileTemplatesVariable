package org.vkravets.idea.project.filetemplate;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.openapi.components.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.apache.velocity.runtime.parser.ParseException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


/**
 * Created by IntelliJ IDEA.
 * Author: Vladimir Kravets
 * E-Mail: vova.kravets@gmail.com
 */
@State(name = "ProjectTemplateVariables",
        storages = {
            @Storage(value = "template/template_variable_settings.xml")
        }
      )
public class ProjectTemplateVariableManager implements PersistentStateComponent<VariablesConfigurationState> {

    public VariablesConfigurationState state = new VariablesConfigurationState();

    private static final Logger logger = Logger.getInstance("#org.vkravets.idea.project.filetemplate.ProjectTemplateVariableManager");

    protected ProjectTemplateVariableManager() {
    }

    @Nullable
    @Override
    public VariablesConfigurationState getState() {
        return this.state;
    }

    @Override
    public void loadState(@NotNull VariablesConfigurationState element) {
        this.state = element;
    }

    public VariablesConfigurationState getProjectVariables() {
        return this.state;
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
}
