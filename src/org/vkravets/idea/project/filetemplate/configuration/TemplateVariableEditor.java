/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vkravets.idea.project.filetemplate.configuration;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.DocumentAdapter;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.vkravets.idea.project.filetemplate.AutoCompletion;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * Author: Vladimir Kravets
 * E-Mail: vova.kravets@gmail.com
 * Date: 2/17/14
 * Time: 7:50 PM
 * <p/>
 * Based on: com.intellij.application.options.pathMacros.PathMacroEditor
 * author: dsl
 *
 * @see com.intellij.application.options.pathMacros.PathMacroEditor
 */
public class TemplateVariableEditor extends DialogWrapper {
    private JComboBox myNameField;
    private JPanel myPanel;
    private JTextField myValueField;
    private final Validator myValidator;

    public interface Validator {
        boolean checkName(String name);

        boolean isOK(String name, String value);

        String getErrorMessage();
    }

    public TemplateVariableEditor(String title, String variableName, String value, Validator validator) {
        super(true);
        setTitle(title);
        myValidator = validator;
//        AutoCompleteDecorator.decorate(myNameField);
        AutoCompletion.enable(myNameField, false);
//        myNameField.setMinimumAndPreferredWidth(20);
        Dimension preferredSize = myNameField.getPreferredSize();
        myNameField.setPreferredSize(new Dimension(20, UIUtil.fixComboBoxHeight(preferredSize.height)));
        myNameField.setSelectedItem(variableName);
        myNameField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ValidationInfo validationInfo = doValidate();
                if (validationInfo != null) {
                    setErrorText(validationInfo.message);
                } else {
                    setErrorText(null);
                }
            }
        });
        DocumentListener documentListener = new DocumentAdapter() {
            public void textChanged(DocumentEvent event) {
                ValidationInfo validationInfo = doValidate();
                if (validationInfo != null) {
                    setErrorText(validationInfo.message);
                } else {
                    setErrorText(null);
                }
            }
        };
        JTextComponent editorComponent = (JTextComponent) myNameField.getEditor().getEditorComponent();
        editorComponent.getDocument().addDocumentListener(documentListener);
        myValueField.getDocument().addDocumentListener(documentListener);
        myValueField.setText(value);
        init();
    }

    public void setDefaultVariables(Set<String> defaultVariables) {
        for (String name : defaultVariables) {
            myNameField.addItem(name);
        }
    }

    public JComponent getPreferredFocusedComponent() {
        return myNameField;
    }

    @NotNull
    protected Action[] createActions() {
        return new Action[]{getOKAction(), getCancelAction(), getHelpAction()};
    }

    protected void doHelpAction() {
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        if (!myValidator.checkName(getName()) || !myValidator.isOK(getName(), getValue())) {
            return new ValidationInfo(myValidator.getErrorMessage());
        }
        return super.doValidate();
    }

    public String getName() {
        String selectedItem = (String) myNameField.getSelectedItem();
        if (selectedItem != null) {
            return selectedItem.trim();
        }
        return null;
    }

    public String getValue() {
        String text = myValueField.getText();
        return text == null ? "" : text.trim();
    }

    protected JComponent createNorthPanel() {
        return myPanel;
    }

    protected JComponent createCenterPanel() {
        return null;
    }
}
