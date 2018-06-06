package com.axonivy.ivy.process.element.blockchain.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class BlockchainRequestComposite extends Composite
{
  public Label lblHeyThere;

  public BlockchainRequestComposite(Composite parent, int style)
  {
    super(parent, style);
    setLayout(new GridLayout(2, false));
    lblHeyThere = new Label(this, SWT.NONE);
    lblHeyThere.setText("hey there");

    Button btnNewButton = new Button(this, SWT.NONE);
    btnNewButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    btnNewButton.setText("New Button");
  }

  public static void main(String[] args)
  {
    Display display = Display.getDefault();
    Shell shell = new Shell();

    new BlockchainRequestComposite(shell, SWT.NONE);
    /*
    MappingTable restParamTable = new MappingTable(shell, SWT.DROP_DOWN);
    List<Row> params = new ArrayList<>();
    params.add(new Row("api.version", "1.1"));
    params.add(new Row("screen_name", "AxonIvy"));
    restParamTable.setItems(params);

    restParamTable.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
    */

    shell.setLayout(new FillLayout());
    shell.setSize(400,400);
    shell.setText("Test Blockchain ui");
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
