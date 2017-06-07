package com.axonivy.ivy.process.element.rule.ui;

import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ch.ivyteam.ivy.designer.richdialog.ui.configeditors.SelectAttributeDialog;
import ch.ivyteam.ivy.scripting.language.IvyScriptContextFactory;
import ch.ivyteam.ivy.scripting.types.IVariable;
import ch.ivyteam.ivy.scripting.util.IvyScriptProcessVariables;
import ch.ivyteam.ivy.scripting.util.Variable;

public class RuleConfigEditor extends Composite
{
  private Text txtRuleNamespace;
  
  private IVariable[] dataVars = new IVariable[0];
  private DataMappingComposite dataMappingComposite;

  public RuleConfigEditor(Composite parent, int style)
  {
    super(parent, style);

    setLayout(new GridLayout(1, false));

    Label lblRule = new Label(this, SWT.NONE);
    lblRule.setText("Rule");

    txtRuleNamespace = new Text(this, SWT.BORDER);
    txtRuleNamespace.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Label lblData = new Label(this, SWT.NONE);
    lblData.setText("Data");

    dataMappingComposite = new DataMappingComposite(this, SWT.NONE);
    dataMappingComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

    addDataChooser();
  }

  public void setRuleNamespace(final String ruleNamespace)
  {
    txtRuleNamespace.setText(StringUtils.stripToEmpty(ruleNamespace));
  }

  public String getRuleNamespace()
  {
    return txtRuleNamespace.getText();
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
