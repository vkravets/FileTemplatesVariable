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
package org.jetbrains.idea.project.filetemplate.configuration;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.DocumentAdapter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * Author: Vladimir Kravets
 * E-Mail: vova.kravets@gmail.com
 * Date: 2/17/14
 * Time: 7:50 PM

 * Based on: com.intellij.application.options.pathMacros.PathMacroEditor
 * @see com.intellij.application.options.pathMacros.PathMacroEditor
 * @author dsl
 */
public class TemplateVariableEditor extends DialogWrapper {
  private JTextField myNameField;
  private JPanel myPanel;
  private JTextField myValueField;
  private final Validator myValidator;

  public interface Validator {
    boolean checkName(String name);
    boolean isOK(String name, String value);
  }

  public TemplateVariableEditor(String title, String macroName, String value, Validator validator) {
    super(true);
    setTitle(title);
    myValidator = validator;
    myNameField.setText(macroName);
    DocumentListener documentListener = new DocumentAdapter() {
      public void textChanged(DocumentEvent event) {
        updateControls();
      }
    };
    myNameField.getDocument().addDocumentListener(documentListener);
    myValueField.getDocument().addDocumentListener(documentListener);
    myValueField.setText(value);
    init();
    updateControls();
  }

  public void setMacroNameEditable(boolean isEditable) {
    myNameField.setEditable(isEditable);
  }

  private void updateControls() {
    final boolean isNameOK = myValidator.checkName(myNameField.getText());
    getOKAction().setEnabled(isNameOK);
    if (isNameOK) {
      final String text = myValueField.getText().trim();
      getOKAction().setEnabled(text.length() > 0);
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

  protected void doOKAction() {
    if (!myValidator.isOK(getName(), getValue())) return;
    super.doOKAction();
  }

  public String getName() {
    return myNameField.getText().trim();
  }

  public String getValue() {
    String path = myValueField.getText().trim();
    File file = new File(path);
    if (file.isAbsolute()) {
      try {
        return file.getCanonicalPath();
      }
      catch (IOException ignored) {
      }
    }
    return path;
  }

  protected JComponent createNorthPanel() {
    return myPanel;
  }

  protected JComponent createCenterPanel() {
    return null;
  }
}
