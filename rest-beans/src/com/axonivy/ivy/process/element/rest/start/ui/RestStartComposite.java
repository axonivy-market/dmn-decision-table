package com.axonivy.ivy.process.element.rest.start.ui;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.SharedScrolledComposite;

import ch.ivyteam.ivy.awtGuiComponents.ivvScriptTrees.IvyClassIconProvider.Mode;
import ch.ivyteam.ivy.guiComponents.swt.restricted.mapping.MappingTreeTable;
import ch.ivyteam.ivy.guiComponents.swt.tables.MappingTable;
import ch.ivyteam.swt.table.ExpandableCompositeUtil;
import ch.ivyteam.swt.table.Row;
import ch.ivyteam.ui.model.swt.UiChangeListener;

public class RestStartComposite extends SharedScrolledComposite implements Listener, UiChangeListener
{
  final ExpandableComposite propertySelector;
  final MappingTable inputParamsTable;
  final MappingTreeTable parameterMapping;
  final Text signatureText;
  final Text nameText;

  public RestStartComposite(Composite parent)
  {
    super(parent, SWT.H_SCROLL | SWT.V_SCROLL);
    setExpandHorizontal(true);
    setExpandVertical(true);

    Composite scrollContent = new Composite(this, SWT.NONE);
    scrollContent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    scrollContent.setLayout(new GridLayout(1, true));
    setContent(scrollContent);

    Group signatureComposite = new Group(scrollContent, SWT.NONE);
    signatureComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
    signatureComposite.setText("Start Signature");
    signatureComposite.setLayout(new GridLayout(2, false));

    Label signatureLabel = new Label(signatureComposite, SWT.NONE);
    signatureLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
    signatureLabel.setText("Signature");
    signatureText = new Text(signatureComposite, SWT.NONE);
    signatureText.setEnabled(false);
    signatureText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    Label nameLabel = new Label(signatureComposite, SWT.NONE);
    nameLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
    nameLabel.setText("Name");
    nameText = new Text(signatureComposite, SWT.NONE);
    nameText.addListener(SWT.Modify, this);
    nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    
    propertySelector = new ExpandableComposite(signatureComposite, SWT.NONE);
    propertySelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
    propertySelector.setText("Input Parameters");
    inputParamsTable = new MappingTable(propertySelector, SWT.DROP_DOWN);
    propertySelector.setClient(inputParamsTable);
    ExpandableCompositeUtil.wireSizeUpdates(this, propertySelector, inputParamsTable);
    inputParamsTable.addChangeListener(this);

    Group parameterComposite = new Group(scrollContent, SWT.NONE);
    parameterComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    parameterComposite.setText("Input Parameters to Process Data Mapping");
    parameterComposite.setLayout(new GridLayout(1, false));
    parameterMapping = new MappingTreeTable(parameterComposite, SWT.NONE);
    parameterMapping.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    parameterMapping.setMode(Mode.PANEL);
  }

  @Override
  public void handleEvent(Event event)
  {
    uiChanged();
  }
  
  private static String getMethodParams(List<Row> rows)
  {
    if(rows == null || rows.size() == 0)
    {
      return "";
    }
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < rows.size(); i++)
    {
      Row row = rows.get(i);
      builder.append(row.value);
      if (i+1 < rows.size())
      {
        builder.append(",");
      } 
    }
    return builder.toString();
  }

  @Override
  public void uiChanged()
  {
    signatureText.setText(nameText.getText() + "(" + getMethodParams(inputParamsTable.getItems()) + ")");
  }

  @SuppressWarnings("unused")
  public static void main(String[] args)
  {
    Display display = Display.getDefault();
    Shell shell = new Shell();

    new RestStartComposite(shell);

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
