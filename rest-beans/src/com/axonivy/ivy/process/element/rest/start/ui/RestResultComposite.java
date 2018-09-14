package com.axonivy.ivy.process.element.rest.start.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.SharedScrolledComposite;

import ch.ivyteam.ivy.awtGuiComponents.ivvScriptTrees.IvyClassIconProvider.Mode;
import ch.ivyteam.ivy.guiComponents.swt.restricted.mapping.MappingTreeTable;
import ch.ivyteam.ivy.guiComponents.swt.tables.MappingTable;
import ch.ivyteam.swt.table.ExpandableCompositeUtil;

public class RestResultComposite extends SharedScrolledComposite
{
  final ExpandableComposite propertySelector;
  final MappingTable outputParamsTable;
  final MappingTreeTable parameterMapping;

  public RestResultComposite(Composite parent)
  {
    super(parent, SWT.H_SCROLL | SWT.V_SCROLL);
    setExpandHorizontal(true);
    setExpandVertical(true);

    Composite scrollContent = new Composite(this, SWT.NONE);
    scrollContent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    scrollContent.setLayout(new GridLayout(1, true));
    setContent(scrollContent);

    Group parameterComposite = new Group(scrollContent, SWT.NONE);
    parameterComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
    parameterComposite.setText("Output");
    parameterComposite.setLayout(new GridLayout(2, false));
    
    propertySelector = new ExpandableComposite(parameterComposite, SWT.NONE);
    propertySelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
    propertySelector.setText("Output Parameters");
    outputParamsTable = new MappingTable(propertySelector, SWT.DROP_DOWN);
    propertySelector.setClient(outputParamsTable);
    ExpandableCompositeUtil.wireSizeUpdates(this, propertySelector, outputParamsTable);

    Group mappingComposite = new Group(scrollContent, SWT.NONE);
    mappingComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    mappingComposite.setText("Process Data to Output Parameters Mapping");
    mappingComposite.setLayout(new GridLayout(1, false));
    parameterMapping = new MappingTreeTable(mappingComposite, SWT.NONE);
    parameterMapping.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    parameterMapping.setMode(Mode.PANEL);
  }

  @SuppressWarnings("unused")
  public static void main(String[] args)
  {
    Display display = Display.getDefault();
    Shell shell = new Shell();

    new RestResultComposite(shell);

    shell.setLayout(new FillLayout());
    shell.setSize(600,600);
    shell.setText("Test Rest Service Start UI");
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
