package com.axonivy.ivy.process.element.blockchain.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.axonivy.ivy.process.element.blockchain.EthereumProperties;

import ch.ivyteam.ivy.guiComponents.swt.tables.MappingTable;
import ch.ivyteam.swt.editors.StringComboBoxCellEditor;
import ch.ivyteam.swt.table.Row;

public class BlockchainPropertyTable extends MappingTable
{

  public BlockchainPropertyTable(Composite parent, int style)
  {
    super(parent, style);
  }

  @Override
  protected StringComboBoxCellEditor createNameEditor(int style)
  {
    return createExandableComboCellEditor(EthereumProperties.ALL_PROPERTIES);
  }
  
  public static void main(String[] args)
  {
    Display display = Display.getDefault();
    Shell shell = new Shell();
    
    BlockchainPropertyTable bcParamTable = new BlockchainPropertyTable(shell, SWT.DROP_DOWN);
    List<Row> params = new ArrayList<>();
    params.add(new Row("NetworkUrl", "\"https://rinkeby.infura.io\""));
    params.add(new Row("Password", "\"nimda\""));
    bcParamTable.setItems(params);
    
    bcParamTable.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
    shell.setLayout(new FillLayout());
    shell.setSize(400,400);
    shell.setText("Test BlockchainPropertyTable");
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
