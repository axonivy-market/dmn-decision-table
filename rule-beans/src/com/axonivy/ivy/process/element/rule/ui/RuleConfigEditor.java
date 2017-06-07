package com.axonivy.ivy.process.element.rule.ui;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import ch.ivyteam.ivy.designer.richdialog.ui.configeditors.SelectAttributeDialog;
import ch.ivyteam.ivy.scripting.language.IvyScriptContextFactory;
import ch.ivyteam.ivy.scripting.types.IVariable;
import ch.ivyteam.ivy.scripting.util.IvyScriptProcessVariables;
import ch.ivyteam.ivy.scripting.util.Variable;
import ch.ivyteam.swt.widgets.ExtendableComboViewer;

public class RuleConfigEditor extends Composite
{
  
  private IVariable[] dataVars = new IVariable[0];
  private DataMappingComposite dataMappingComposite;
  private Combo comboRuleNamespace;
  private ExtendableComboViewer extendableComboViewer;

  public RuleConfigEditor(Composite parent, int style)
  {
    super(parent, style);

    setLayout(new GridLayout(1, false));

    Label lblRule = new Label(this, SWT.NONE);
    lblRule.setText("Rule");
    
    extendableComboViewer = new ExtendableComboViewer(this, SWT.NONE);
    comboRuleNamespace = extendableComboViewer.getCombo();
    comboRuleNamespace.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    
    Label lblData = new Label(this, SWT.NONE);
    lblData.setText("Data");

    dataMappingComposite = new DataMappingComposite(this, SWT.NONE);
    dataMappingComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

    addDataChooser();
  }

  public void setAvailableRuleNamespace(List<String> availableRuleNamespaces)
  {
    initRuleNamespaces(availableRuleNamespaces);
  }

  public void setRuleNamespace(final String ruleNamespace)
  {
    List<String> ruleNamespaces = getRuleNamespaces();
    ruleNamespaces.add(ruleNamespace);
    initRuleNamespaces(ruleNamespaces);
    ruleNamespaces = getRuleNamespaces();
    for (int i = 0; i < ruleNamespaces.size(); i++)
    {
      if (ruleNamespaces.get(i).equals(ruleNamespace))
      {
        extendableComboViewer.getCombo().select(i);
      }
    }
  }

  private void initRuleNamespaces(List<String> ruleNamepsaces)
  {
    List<String> newItems = ruleNamepsaces.stream()
            .distinct()
            .sorted(String::compareToIgnoreCase)
            .collect(Collectors.toList());
    extendableComboViewer.getCombo().setItems(newItems.toArray(new String[newItems.size()]));
  }

  private List<String> getRuleNamespaces()
  {
    return Arrays.stream(extendableComboViewer.getCombo().getItems()).collect(Collectors.toList());
  }

  public String getRuleNamespace()
  {
    return extendableComboViewer.getCombo().getText();
  }

  public void setInputData(String inputData)
  {
    dataMappingComposite.text.setText(StringUtils.stripToEmpty(inputData));
  }
  
  public String getInputData()
  {
    return dataMappingComposite.text.getText();
  }
  
  public void setDataVariables(IVariable[] vars)
  {
    this.dataVars = Arrays.stream(vars)
            .filter(var -> var.getName().equals(IvyScriptProcessVariables.IN.getVariableName())) // use only IN
            .flatMap(in -> Arrays.stream(new IVariable[]{in, new Variable(IvyScriptProcessVariables.OUT.getVariableName(), in.getType())})) // duplicate in as out
            .toArray(IVariable[]::new);
  }

  private void addDataChooser()
  {
    dataMappingComposite.btnAttributeBrowser.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        Optional<String> selection = attributeSelectionDialog(IvyScriptProcessVariables.IN.getVariableName());
        if (selection.isPresent())
        {
          String attribute = selection.get();
          setInputData(attribute);
        }
      }
    });
  }
  
  private Optional<String> attributeSelectionDialog(String variableFilter)
  {
    SelectAttributeDialog dialog = SelectAttributeDialog.createAttributeBrowserDialog(this.getShell());
    dialog.create();
    try
    {
      IVariable[] vars = Arrays.stream(dataVars)
        .filter(var -> var.getName().equals(variableFilter))
        .toArray(IVariable[]::new);
      dialog.setInput(IvyScriptContextFactory.createIvyScriptContext(vars));
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    if (dialog.open() == Window.OK)
    {
      String attribute = (String) dialog.getSelection();
      return Optional.of(attribute);
    }
    return Optional.empty();
  }

}
