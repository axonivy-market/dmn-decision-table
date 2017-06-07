package com.axonivy.ivy.process.element.rule.ui.cellEdit;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class ComboConditionComposite extends Composite
{
  public ComboViewer operation;
  public ComboViewer value;

  public ComboConditionComposite(Composite parent, int style)
  {
    super(parent, style);
    GridLayout gridLayout = new GridLayout(2, false);
    gridLayout.marginWidth = 0;
    gridLayout.marginHeight = 0;
    gridLayout.verticalSpacing = 0;
    gridLayout.horizontalSpacing = 0;
    setLayout(gridLayout);
    
    operation = new ComboViewer(this, SWT.NONE);
    Combo combo = operation.getCombo();
    combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    
    value = new ComboViewer(this, SWT.BORDER);
    value.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
  }
  
}
