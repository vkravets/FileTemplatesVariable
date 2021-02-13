package org.vkravets.idea.project.filetemplate.configuration;

import com.intellij.configurationStore.XmlSerializer;
import com.intellij.conversion.CannotConvertException;
import com.intellij.conversion.ComponentManagerSettings;
import com.intellij.conversion.ConversionContext;
import com.intellij.conversion.ConversionProcessor;
import com.intellij.conversion.ProjectConverter;
import com.intellij.conversion.WorkspaceSettings;
import com.intellij.ide.impl.convert.JDomConvertingUtil;
import com.intellij.openapi.components.StorageScheme;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.util.SystemProperties;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Parent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.serialization.JDomSerializationUtil;
import org.vkravets.idea.project.filetemplate.ProjectTemplateVariableManager;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA. Author: vkravets E-Mail: Date: 12.02.2021 Time: 19:56
 */
class TemplateVariableProjectConverter extends ProjectConverter {

    private static final Logger LOGGER = Logger.getInstance(TemplateVariableProjectConverter.class);

    private final ConversionContext conversionContext;

    public TemplateVariableProjectConverter(ConversionContext conversionContext) {this.conversionContext = conversionContext;}

    @Override
    public @Nullable ConversionProcessor<WorkspaceSettings> createWorkspaceFileConverter() {
        // Converter for file-based Project Settings
        return new ConversionProcessor<>() {

            @Override
            public boolean isConversionNeeded(WorkspaceSettings settings) {
                Element oldSettings = JDomSerializationUtil.findComponent(settings.getRootElement(), "ProjectTemplateVariables");
                return oldSettings != null;
            }

            @Override
            public void process(WorkspaceSettings settings) throws CannotConvertException {
                Element oldSettings = JDomSerializationUtil.findComponent(settings.getRootElement(), "ProjectTemplateVariables");
                if (oldSettings != null) {
                    final Element rootSettings = oldSettings.getParentElement();
                    final ProjectTemplateVariableManager.LegacyConfiguration legacyConfiguration =
                            XmlSerializer.deserialize(oldSettings,
                                                      ProjectTemplateVariableManager.LegacyConfiguration.class);
                    final ProjectTemplateVariableManager newConfiguration =
                            ProjectTemplateVariableManager.transform(legacyConfiguration);

                    final Element newConfigurationElement = XmlSerializer.serialize(newConfiguration).getChild("templateVariables");
                    final Element projectTemplateVariables = JDomSerializationUtil.createComponentElement("ProjectTemplateVariables");
                    newConfigurationElement.detach();
                    projectTemplateVariables.addContent(newConfigurationElement);
                    JDomSerializationUtil.addComponent(rootSettings, projectTemplateVariables);
                }
            }


        };
    }

    private @NotNull ComponentManagerSettings getLegacyConfiguration() {
        return conversionContext.createProjectSettings("template/template_variable_settings.xml");
    }

    private @NotNull ComponentManagerSettings getConfiguration() {
        return conversionContext.createProjectSettings("template/template_variables.xml");
    }

    @Override
    public boolean isConversionNeeded() {
        return Files.exists(getLegacyConfiguration().getPath());
    }

    @Override
    public void processingFinished() throws CannotConvertException {
        ComponentManagerSettings oldSettings = getLegacyConfiguration();
        if (conversionContext.getStorageScheme() == StorageScheme.DIRECTORY_BASED && Files.exists(oldSettings.getPath())) {
            final Element templateVariables = JDomConvertingUtil.load(oldSettings.getPath());
            if (templateVariables != null) {
                final ProjectTemplateVariableManager.LegacyConfiguration configuration =
                        XmlSerializer.deserialize(templateVariables,
                                                  ProjectTemplateVariableManager.LegacyConfiguration.class);
                templateVariables.detach();
                final ProjectTemplateVariableManager newConfiguration = ProjectTemplateVariableManager.transform(configuration);
                final Element newConfigurationElement = XmlSerializer.serialize(newConfiguration).getChild("templateVariables");

                final ComponentManagerSettings projectSettings =
                        conversionContext.createProjectSettings(getConfiguration().getPath()
                                                                                  .toString());
                final Element projectTemplateVariables = JDomSerializationUtil.createComponentElement("ProjectTemplateVariables");
                newConfigurationElement.detach();
                projectTemplateVariables.addContent(newConfigurationElement);
                final Element rootElement = projectSettings.getRootElement();
                final Document document = rootElement.getDocument();
                rootElement.detach();
                document.setRootElement(projectTemplateVariables);

                try {
                    writeDocument(document, getConfiguration().getPath());
                    Files.delete(oldSettings.getPath());
                }
                catch (IOException e) {
                    LOGGER.warn(e);
                }
            }
        }
    }

    private void writeDocument(Document document, Path path) throws IOException {
        if (document != null) {
            Files.createDirectories(path.getParent());
            BufferedWriter writer = Files.newBufferedWriter(path);

            try {
                JDOMUtil.writeDocument(document, writer, SystemProperties.getLineSeparator());
            }
            catch (Throwable writeException) {
                try {
                    writer.close();
                }
                catch (Throwable closeException) {
                    writeException.addSuppressed(closeException);
                }

                throw writeException;
            }
            writer.close();
        }

    }

    @Override
    public @NotNull Collection<Path> getCreatedFiles() {
        return Collections.singleton(getConfiguration().getPath());
    }

    @Override
    public @NotNull
    Collection<Path> getAdditionalAffectedFiles() {
        return Collections.singleton(getLegacyConfiguration().getPath());
    }

}
