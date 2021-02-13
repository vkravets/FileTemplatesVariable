package org.vkravets.idea.project.filetemplate.configuration;

import com.intellij.conversion.ConversionContext;
import com.intellij.conversion.ConverterProvider;
import com.intellij.conversion.ProjectConverter;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA. Author: vkravets E-Mail: Date: 12.02.2021 Time: 16:28
 */
public class ProjectVariableConfigurationMigration extends ConverterProvider {
    @Override
    public @NotNull String getConversionDescription() {
        return "Migrate legacy configuration FileTemplateVariable plugin";
    }

    @NotNull
    @Override
    public ProjectConverter createConverter(@NotNull ConversionContext conversionContext) {
        return new TemplateVariableProjectConverter(conversionContext);
    }

}
