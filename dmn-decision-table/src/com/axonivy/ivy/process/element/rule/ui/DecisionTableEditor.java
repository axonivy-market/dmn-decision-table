package com.axonivy.ivy.process.element.rule.ui;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import com.axonivy.ivy.process.element.rule.CitizenSample;
import com.axonivy.ivy.process.element.rule.dmn.DmnSerializer;
import com.axonivy.ivy.process.element.rule.model.ActionColumn;
import com.axonivy.ivy.process.element.rule.model.ColumnType;
import com.axonivy.ivy.process.element.rule.model.ConditionColumn;
import com.axonivy.ivy.process.element.rule.model.RulesModel;

import ch.ivyteam.icons.Size;
import ch.ivyteam.ivy.designer.ui.attribute.SelectAttributeDialog;
import ch.ivyteam.ivy.project.IIvyProject;
import ch.ivyteam.ivy.project.model.Project;
import ch.ivyteam.ivy.scripting.language.IIvyScriptContext;
import ch.ivyteam.ivy.scripting.language.IIvyScriptEngine;
import ch.ivyteam.ivy.scripting.language.IvyScriptContextFactory;
import ch.ivyteam.ivy.scripting.types.IIvyClass;
import ch.ivyteam.ivy.scripting.types.classmembers.IVariable;
import ch.ivyteam.ivy.scripting.util.IvyScriptProcessVariables;
import ch.ivyteam.ivy.scripting.util.Variable;
import ch.ivyteam.ivy.scripting.validator.IvyScriptValidator;

public class DecisionTableEditor extends Composite {

  public DecisionTableComposite table;
  public final CTabFolder tabs;
  private ColumnEditActionsComposite columnEdit;

  private IVariable[] dataVars = new IVariable[0];
  private IIvyScriptEngine scriptEngine;
  private Project project;

  public DecisionTableEditor(Composite parent, int style) {
    super(parent, style);
    setLayout(new GridLayout(1, false));

    tabs = new CTabFolder(this, SWT.BOTTOM);
    tabs.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

    createTableTab();
    createDMNtab();
  }

  private CTabItem createTableTab() {
    CTabItem tab = new CTabItem(tabs, SWT.NONE);
    tab.setText("Table");
    tab.setImage(ch.ivyteam.swt.icons.IconFactory.get(this).getTable(Size.SIZE_16));

    Group grpDecisions = new Group(tabs, SWT.NONE);
    grpDecisions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    grpDecisions.setText("Decisions");
    grpDecisions.setLayout(new GridLayout(1, false));
    tab.setControl(grpDecisions);

    columnEdit = new ColumnEditActionsComposite(grpDecisions, SWT.NONE);
    columnEdit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    addDataChooser();

    table = new DecisionTableComposite(grpDecisions, SWT.NONE);
    table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    return tab;
  }

  private CTabItem createDMNtab() {
    CTabItem dmnTab = new CTabItem(tabs, SWT.NONE);
    dmnTab.setText("DMN");
    // assign a XML or DMN icon
    TextViewer dmnViewer = new TextViewer(tabs, SWT.V_SCROLL);
    dmnViewer.setDocument(new Document());
    dmnTab.setControl(dmnViewer.getTextWidget());
    dmnTab.addListener(SWT.SELECTED, evt -> {
      dmnViewer.getDocument().set(toDMN(table.model));
    });

    tabs.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        tabs.getSelection().notifyListeners(SWT.SELECTED, new Event());
      }
    });
    return dmnTab;
  }

  private static String toDMN(RulesModel model) {
    try {
      try (InputStream is = new DmnSerializer(model).serialize()) {
        return IOUtils.toString(is, StandardCharsets.UTF_8);
      }
    } catch (Exception ex) {
      return ex.getMessage();
    }
  }

  public void setDataVariables(IVariable[] vars) {
    this.dataVars = Arrays.stream(vars)
            .filter(var -> var.getName().equals(IvyScriptProcessVariables.IN.getVariableName())) // use
                                                                                                 // only
                                                                                                 // IN
            .flatMap(in -> Arrays.stream(new IVariable[] {in,
                new Variable(IvyScriptProcessVariables.OUT.getVariableName(), in.getType())})) // duplicate
                                                                                               // in
                                                                                               // as
                                                                                               // out
            .toArray(IVariable[]::new);
  }

  private void addDataChooser() {
    columnEdit.btnAddCondition.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        Optional<String> selection = attributeSelectionDialog(IvyScriptProcessVariables.IN.getVariableName());
        if (selection.isPresent()) {
          String attribute = selection.get();
          ColumnType type = getTypeOf(attribute);
          table.addConditionColumn(new ConditionColumn(attribute, type));
          table.pack(true);
          table.getParent().layout(true);
        }
      }
    });
    columnEdit.btnAddOutput.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        Optional<String> selection = attributeSelectionDialog(
                IvyScriptProcessVariables.OUT.getVariableName());
        if (selection.isPresent()) {
          String attribute = selection.get();
          ColumnType type = getTypeOf(attribute);
          table.addActionColumn(new ActionColumn(attribute, type));
          table.pack(true);
          table.getParent().layout(true);
        }
      }
    });
  }

  private Optional<String> attributeSelectionDialog(String variableFilter) {
    var dialog = SelectAttributeDialog.createAttributeBrowserDialog(this.getShell(), project);
    dialog.create();
    try {
      var vars = Arrays.stream(dataVars)
              .filter(var -> var.getName().equals(variableFilter))
              .collect(Collectors.toList());
      dialog.setInput(vars);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    if (dialog.open() == Window.OK) {
      String attribute = (String) dialog.getSelection();
      return Optional.of(attribute);
    }
    return Optional.empty();
  }

  private ColumnType getTypeOf(String attribute) {
    IIvyClass<?> ivyClass = getIvyTypeOf(attribute);
    if (ivyClass != null) {
      if (ivyClass.getJavaClass().equals(Number.class)) {
        return ColumnType.Number;
      }
      if (ivyClass.getJavaClass().equals(Boolean.class)) {
        return ColumnType.Boolean;
      }
    }
    return ColumnType.String;
  }

  private IIvyClass<?> getIvyTypeOf(String attribute) {
    try {
      IIvyScriptContext context = IvyScriptContextFactory.createIvyScriptContext(dataVars);
      IvyScriptValidator validator = new IvyScriptValidator(scriptEngine, context);
      return validator.determineType(attribute);
    } catch (Exception ex) {
      return null;
    }
  }

  public static void main(String[] args) {
    Shell shell = new Shell();
    DecisionTableEditor editor = new DecisionTableEditor(shell, SWT.NONE);

    RulesModel model = CitizenSample.generateData();
    editor.table.setModel(model);
    editor.setDataVariables(getSampleScriptContext());

    editor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
    shell.setLayout(new FillLayout());
    shell.setSize(700, 400);
    shell.setText("Test Decision Table");
    shell.layout();
    shell.open();

    Display display = Display.getDefault();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
  }

  private static IVariable[] getSampleScriptContext() {
    return new IVariable[] {};
  }

  public void setProject(IIvyProject ivyProject) {
    scriptEngine = ivyProject.getIvyScriptEngine();
    project = ivyProject.project();
  }

}