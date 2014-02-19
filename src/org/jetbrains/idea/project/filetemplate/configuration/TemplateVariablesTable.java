package org.jetbrains.idea.project.filetemplate.configuration;

import com.intellij.openapi.application.ApplicationBundle;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.ui.JBColor;
import com.intellij.ui.table.JBTable;
import org.jetbrains.idea.project.filetemplate.PerProjectTemplateManager;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: Vladimir Kravets
 * E-Mail: vova.kravets@gmail.com
 * Date: 2/17/14
 * Time: 7:55 PM
 *
 * Based on: com.intellij.application.options.pathMacros.PathMacroTable
 *           author dsl
 * @see com.intellij.application.options.pathMacros.PathMacroTable
 */


public class TemplateVariablesTable extends JBTable {
    private final TemplateVariableTableModel tableModel = new TemplateVariableTableModel();
    private static final int NAME_COLUMN = 0;
    private static final int VALUE_COLUMN = 1;

    private final List<Pair<String, String>> curTemplateVariables = new ArrayList<Pair<String, String>>();
    private static final Comparator<Pair<String, String>> VARIABLES_COMPARATOR = new Comparator<Pair<String, String>>() {
        public int compare(Pair<String, String> pair, Pair<String, String> pair1) {
            return pair.getFirst().compareTo(pair1.getFirst());
        }
    };

    private final Project project;

    public TemplateVariablesTable(Project project) {
        this.project = project;
        setModel(tableModel);
        TableColumn column = getColumnModel().getColumn(NAME_COLUMN);
        column.setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                final Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                final String variableValue = getVariableValueAt(row);
                component.setForeground(variableValue.length() == 0
                        ? JBColor.RED
                        : isSelected ? table.getSelectionForeground() : table.getForeground());
                return component;
            }
        });
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        //obtainData();

        getEmptyText().setText("No Project's template variables");
    }

    public String getVariableValueAt(int row) {
        return (String) getValueAt(row, VALUE_COLUMN);
    }

    public String getVariableNameAt(int row) {
        return (String)getValueAt(row, NAME_COLUMN);
    }

    public void addVariable() {
        final String title = ApplicationBundle.message("title.add.variable");
        final TemplateVariableEditor variableEditor = new TemplateVariableEditor(title, "", "", new AddValidator());
        variableEditor.show();
        if (variableEditor.isOK()) {
            final String name = variableEditor.getName();
            curTemplateVariables.add(new Pair<String, String>(name, variableEditor.getValue()));
            Collections.sort(curTemplateVariables, VARIABLES_COMPARATOR);
            final int index = indexOfVariableWithName(name);
            tableModel.fireTableDataChanged();
            setRowSelectionInterval(index, index);
        }
    }

    private boolean isValidRow(int selectedRow) {
        return selectedRow >= 0 && selectedRow < curTemplateVariables.size();
    }

    public void removeSelectedMacros() {
        final int[] selectedRows = getSelectedRows();
        if(selectedRows.length == 0) return;
        Arrays.sort(selectedRows);
        final int originalRow = selectedRows[0];
        for (int i = selectedRows.length - 1; i >= 0; i--) {
            final int selectedRow = selectedRows[i];
            if (isValidRow(selectedRow)) {
                curTemplateVariables.remove(selectedRow);
            }
        }
        tableModel.fireTableDataChanged();
        if (originalRow < getRowCount()) {
            setRowSelectionInterval(originalRow, originalRow);
        } else if (getRowCount() > 0) {
            final int index = getRowCount() - 1;
            setRowSelectionInterval(index, index);
        }
    }

    public void commit() {
        PerProjectTemplateManager perProjectTemplateManager = PerProjectTemplateManager.getInstance(project);
        Properties projectVariables = perProjectTemplateManager.getProjectVariables();
        projectVariables.clear();
        for (Pair<String, String> pair : curTemplateVariables) {
            final String value = pair.getSecond();
            if (value != null && value.trim().length() > 0) {
                projectVariables.setProperty(pair.getFirst(), value);
            }
        }
    }

    public void reset() {
        obtainData();
    }

    private boolean hasVariableWithName(String name) {
        for (Pair<String, String> variableInfo : curTemplateVariables) {
            if (name.equals(variableInfo.getFirst())) {
                return true;
            }
        }
        return false;
    }

    private int indexOfVariableWithName(String name) {
        for (int i = 0; i < curTemplateVariables.size(); i++) {
            final Pair<String, String> pair = curTemplateVariables.get(i);
            if (name.equals(pair.getFirst())) {
                return i;
            }
        }
        return -1;
    }

    private void obtainData() {
        obtainVariablesPairs(curTemplateVariables);
        tableModel.fireTableDataChanged();
    }

    private void obtainVariablesPairs(final List<Pair<String, String>> macros) {
        PerProjectTemplateManager projectTemplateManager = PerProjectTemplateManager.getInstance(project);
        macros.clear();
        Properties projectVariables = projectTemplateManager.getProjectVariables();
        final Set<String> macroNames = projectVariables.stringPropertyNames();
        for (String name : macroNames) {
            macros.add(Pair.create(name, projectVariables.getProperty(name)));
        }

        Collections.sort(macros, VARIABLES_COMPARATOR);
    }

    public void editVariable() {
        if (getSelectedRowCount() != 1) {
            return;
        }
        final int selectedRow = getSelectedRow();
        final Pair<String, String> pair = curTemplateVariables.get(selectedRow);
        final String title = ApplicationBundle.message("title.edit.variable");
        final String variableName = pair.getFirst();
        final TemplateVariableEditor variableEditor = new TemplateVariableEditor(title, variableName, pair.getSecond(), new EditValidator(variableName));
        variableEditor.show();
        if (variableEditor.isOK()) {
            curTemplateVariables.remove(selectedRow);
            curTemplateVariables.add(Pair.create(variableEditor.getName(), variableEditor.getValue()));
            Collections.sort(curTemplateVariables, VARIABLES_COMPARATOR);
            tableModel.fireTableDataChanged();
        }
    }

    public boolean isModified() {
        final ArrayList<Pair<String, String>> variables = new ArrayList<Pair<String, String>>();
        obtainVariablesPairs(variables);
        return !variables.equals(curTemplateVariables);
    }

    private class TemplateVariableTableModel extends AbstractTableModel{
        public int getColumnCount() {
            return 2;
        }

        public int getRowCount() {
            return curTemplateVariables.size();
        }

        public Class getColumnClass(int columnIndex) {
            return String.class;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            final Pair<String, String> pair = curTemplateVariables.get(rowIndex);
            switch (columnIndex) {
                case NAME_COLUMN: return pair.getFirst();
                case VALUE_COLUMN: return pair.getSecond();
            }
            return null;
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        }

        public String getColumnName(int columnIndex) {
            switch (columnIndex) {
                case NAME_COLUMN: return ApplicationBundle.message("column.name");
                case VALUE_COLUMN: return ApplicationBundle.message("column.value");
            }
            return null;
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    }

    private class AddValidator implements TemplateVariableEditor.Validator {

        private String errorMessage;

        public AddValidator() {
            errorMessage = "";
        }

        public boolean checkName(String name) {
            if (name.length() == 0) {
                errorMessage = "Name is empty";
                return false;
            }
            if (!name.toUpperCase().equals(name)) {
                errorMessage = "Name should be UPPER case";
                return false;
            }
            return true;
        }

        public boolean isOK(String name, String value) {
            if(!checkName(name)) return false;
            if (hasVariableWithName(name)) {
                errorMessage = ApplicationBundle.message("error.variable.already.exists", name);
                return false;
            }
            return true;
        }

        @Override
        public String getErrorMessage() {
            return errorMessage;
        }

    }

    private class EditValidator implements TemplateVariableEditor.Validator {

        private String errorMessage;
        private String originalName;

        private EditValidator(String originalName) {
            this.errorMessage = "";
            this.originalName = originalName;
        }

        public boolean checkName(String name) {
            if (name.length() == 0) {
                errorMessage = "Name is empty";
                return false;
            }
            if (!name.toUpperCase().equals(name)) {
                errorMessage = "Name should be UPPER case";
                return false;
            }
            if (!originalName.equals(name) && hasVariableWithName(name)) {
                errorMessage = ApplicationBundle.message("error.variable.already.exists", name);
                return false;
            }
            return true;


        }

        public boolean isOK(String name, String value) {
            return checkName(name);
        }

        @Override
        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
