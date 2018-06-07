package com.axonivy.ivy.process.element.blockchain.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.SharedScrolledComposite;

import ch.ivyteam.ivy.awtGuiComponents.ivvScriptTrees.IvyClassIconProvider.Mode;
import ch.ivyteam.ivy.guiComponents.swt.restricted.mapping.MappingTreeTable;
import ch.ivyteam.ivy.guiComponents.swt.tables.MappingTable;

public class BlockchainRequestComposite extends SharedScrolledComposite
{
  private MappingTable propertiesTable;
  final MappingTreeTable parameterMapping;

  public BlockchainRequestComposite(Composite parent)
  {
    super(parent, SWT.H_SCROLL | SWT.V_SCROLL);
    setExpandHorizontal(true);
    setExpandVertical(true);

    Composite scrollContent = new Composite(this, SWT.NONE);
    scrollContent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    scrollContent.setLayout(new GridLayout(1, true));
    setContent(scrollContent);

    Composite callSelector = new Composite(scrollContent, SWT.NONE);
    GridLayout gl_callSelector = new GridLayout(2, false);
    gl_callSelector.marginWidth = 0;
    callSelector.setLayout(gl_callSelector);
    callSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

    Label contractLabel = new Label(callSelector, SWT.NONE);
    contractLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    contractLabel.setText("Contract");
    Combo contractCombo = new Combo(callSelector, SWT.NONE);
    contractCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Label functionLabel = new Label(callSelector, SWT.NONE);
    functionLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    functionLabel.setText("Function");
    Combo functionCombo = new Combo(callSelector, SWT.NONE);
    functionCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    ExpandableComposite propertySelector = new ExpandableComposite(scrollContent, SWT.NONE);
    propertySelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
    propertySelector.setText("Properties");
    propertiesTable = new MappingTable(propertySelector, SWT.DROP_DOWN);
    propertySelector.setClient(propertiesTable);

    parameterMapping = new MappingTreeTable(scrollContent, SWT.NONE);
    parameterMapping.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    parameterMapping.setMode(Mode.PANEL);
  }

  public static void main(String[] args)
  {
    Display display = Display.getDefault();
    Shell shell = new Shell();

    new BlockchainRequestComposite(shell);

    shell.setLayout(new FillLayout());
    shell.setSize(600,600);
    shell.setText("Test Blockchain Request UI");
    shell.layout();
    shell.open();
    while(!shell.isDisposed())
    {
      if (!display.readAndDispatch())
      {
        display.sleep();
      }
    }
  }
}
