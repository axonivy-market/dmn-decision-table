package com.axonivy.ivy.process.element.rule.ui;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import ch.ivyteam.ivy.designer.richdialog.ui.configeditors.SelectAttributeDialog;
import ch.ivyteam.ivy.scripting.exceptions.invocation.IvyScriptVariableAlreadyDeclaredException;
import ch.ivyteam.ivy.scripting.language.IvyScriptContextFactory;
import ch.ivyteam.ivy.scripting.types.IVariable;
import ch.ivyteam.ivy.scripting.util.IvyScriptProcessVariables;
import ch.ivyteam.log.Logger;
import ch.ivyteam.swt.SwtSelectionUtil;
import ch.ivyteam.swt.widgets.ExtendableComboViewer;

public class RuleConfigEditor extends Composite
{
  private static final Logger LOGGER = Logger.getLogger(RuleConfigEditor.class);

  private IVariable[] dataVars = new IVariable[0];
  private DataMappingComposite dataMappingComposite;
  private ExtendableComboViewer extendableComboViewer;

private IProject project;

  public RuleConfigEditor(Composite parent, int style)
  {
    super(parent, style);

    setLayout(new GridLayout(1, false));

    Label lblRule = new Label(this, SWT.NONE);
    lblRule.setText("Rule Namespace");
    
    extendableComboViewer = new ExtendableComboViewer(this, SWT.NONE).withUserInput();
    extendableComboViewer.setContentProvider(ArrayContentProvider.getInstance());
    extendableComboViewer.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    
    Label lblData = new Label(this, SWT.NONE);
    lblData.setText("Data");

    dataMappingComposite = new DataMappingComposite(this, SWT.NONE);
    dataMappingComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

    addDataChooser();
  }

  public void setAvailableRuleNamespace(List<String> availableRuleNamespaces)
  {
    List<String> namespaces = availableRuleNamespaces.stream()
            .distinct()
            .sorted(String::compareToIgnoreCase)
            .collect(Collectors.toList());
    extendableComboViewer.setInput(namespaces);
  }

  public void setRuleNamespace(final String ruleNamespace)
  {
    extendableComboViewer.setSelection(new StructuredSelection(StringUtils.trimToEmpty(ruleNamespace)), false);
  }

  public String getRuleNamespace()
  {
    return SwtSelectionUtil.getFirstElement(extendableComboViewer.getSelection());
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
            .filter(var -> var.getName().equals(IvyScriptProcessVariables.IN.getVariableName()))
            .toArray(IVariable[]::new);
  }

  private void addDataChooser()
  {
    dataMappingComposite.btnAttributeBrowser.addSelectionListener(new SelectionAdapter()
      {
        @Override
        public void widgetSelected(SelectionEvent e)
        {
          Optional<String> selection = attributeSelectionDialog();
          if (selection.isPresent())
          {
            String attribute = selection.get();
            setInputData(attribute);
          }
        }
      });
  }

  private Optional<String> attributeSelectionDialog()
  {
    SelectAttributeDialog dialog = SelectAttributeDialog.createAttributeBrowserDialog(this.getShell(), project);
    dialog.create();
    try
    {
      dialog.setInput(IvyScriptContextFactory.createIvyScriptContext(dataVars));
    }
    catch (IvyScriptVariableAlreadyDeclaredException ex)
    {
      LOGGER.error(ex);
    }
    if (dialog.open() == Window.OK)
    {
      String attribute = (String) dialog.getSelection();
      return Optional.of(attribute);
    }
    return Optional.empty();
  }

  public void setProject(IProject project) {
    this.project = project;
  }

}
