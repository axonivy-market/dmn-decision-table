package com.axonivy.ivy.process.element.blockchain.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import ch.ivyteam.swt.layout.GridDataBuilder;

public class BlockchainErrorGroup extends Composite
{
  public final IgnorableExceptionComposite ignorableException;

  BlockchainErrorGroup(Composite parent, int style)
  {
    super(parent, style);
    GridLayout layout = new GridLayout(1, true);
    layout.verticalSpacing = 0;
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    setLayout(layout);

    Group grpErrors = new Group(this, SWT.NONE);
    grpErrors.setLayout(new GridLayout(4, false));
    grpErrors.setLayoutData(GridDataBuilder.create().horizontalFill().toGridData());
    grpErrors.setText("Error handling");

    Label lblError = new Label(grpErrors, SWT.NONE);
    lblError.setAlignment(SWT.LEFT);
    lblError.setText("On Error");
    ignorableException = new IgnorableExceptionComposite(grpErrors, SWT.NONE);
    ignorableException.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
  }
}
