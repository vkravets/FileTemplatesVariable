package org.jetbrains.idea.project.filetemplate;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.impl.DefaultProject;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.util.Pair;
import com.intellij.util.text.UniqueNameGenerator;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * Author: Vladimir Kravets
 * E-Mail: vova.kravets@gmail.com
 * Date: 2/14/14
 * Time: 5:35 PM
 */
@State(name = "ProjectTemplateVariables",
        storages = {
                @Storage(file = StoragePathMacros.PROJECT_FILE),
                @Storage(file = StoragePathMacros.PROJECT_CONFIG_DIR + "/template/", scheme = StorageScheme.DIRECTORY_BASED,
                         stateSplitter = PerProjectTemplateManager.ProjectTemplateStateSplitter.class)
        }
      )
public class PerProjectTemplateManager extends AbstractProjectComponent implements PersistentStateComponent<Element> {

    private Properties projectVariables;

    protected PerProjectTemplateManager(Project project) {
        super(project);
        projectVariables = new Properties();
    }

    private static final String VARIABLE_TAG = "variable";
    private static final String VARIABLE_NAME_ATTR = "name";
    private static final String TEMPLATE_VARIABLES = "templateVariables";

    @Nullable
    @Override
    public Element getState() {
        Element root = new Element(TEMPLATE_VARIABLES);
        for (String name : projectVariables.stringPropertyNames()) {
            Element variable = new Element(VARIABLE_TAG);
            variable.setAttribute(VARIABLE_NAME_ATTR, name);
            variable.addContent(projectVariables.getProperty(name));
            root.addContent(variable);
        }
        return root;
    }

    @Override
    public void loadState(Element element) {
        projectVariables.clear();
        List<Element> elements = JDOMUtil.getChildren(element, TEMPLATE_VARIABLES);
        if (elements.size() > 0) {
            updateVariablesFromElement(elements.get(0));
        } else {
            // Workaround for DefaultProject storage behavior
            // it's save information without root element, in our case without TEMPLATE_VARIABLES tag
            updateVariablesFromElement(element);
        }
    }

    private void updateVariablesFromElement(Element element) {
        List<Element> elements = JDOMUtil.getChildren(element, VARIABLE_TAG);
        for (Element variable : elements) {
            Attribute attribute = variable.getAttribute(VARIABLE_NAME_ATTR);
            if (attribute != null && attribute.getValue() != null) {
                projectVariables.put(attribute.getValue(), variable.getValue());
            }
        }
    }

    public static class ProjectTemplateStateSplitter implements StateSplitter{

        public ProjectTemplateStateSplitter() {
        }

        @Override
        public List<Pair<Element, String>> splitState(Element e) {
            final UniqueNameGenerator generator = new UniqueNameGenerator();
            final List<Pair<Element, String>> result = new ArrayList<Pair<Element, String>>();
            result.add(new Pair<Element, String>(e, generator.generateUniqueName("template_variable_settings") + ".xml"));
            return result;
        }

        @Override
        public void mergeStatesInto(Element target, Element[] elements) {
            for (Element element : elements) {
                if (element.getName().equals(TEMPLATE_VARIABLES)) {
                    element.detach();
                    Element child = target.getChild(TEMPLATE_VARIABLES);
                    if (child == null) {
                        child = new Element(TEMPLATE_VARIABLES);
                        target.addContent(child);
                    }
                    Element[] variables = JDOMUtil.getElements(element);
                    for (Element variable : variables) {
                        variable.detach();
                        child.addContent(variable);
                    }
                }
                else {
                    final Element[] states = JDOMUtil.getElements(element);
                    for (Element state : states) {
                        state.detach();
                        target.addContent(state);
                    }
                    for (Object attr : element.getAttributes()) {
                        target.setAttribute(((Attribute)attr).clone());
                    }
                }
            }
        }
    }

    public static PerProjectTemplateManager getInstance(Project project) {
        return project.getComponent(PerProjectTemplateManager.class);
    }


    public Properties getProjectVariables() {
        return projectVariables;
    }
}
