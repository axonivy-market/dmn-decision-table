package com.axonivy.ivy.process.element.rule.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class ColumnEditActionsComposite extends Composite {

  public final Button btnAddCondition;
  public final Button btnAddOutput;

  public ColumnEditActionsComposite(Composite parent, int style) {
    super(parent, style);
    GridLayout gridLayout = new GridLayout(2, false);
    gridLayout.marginHeight = 0;
    gridLayout.marginWidth = 0;
    setLayout(gridLayout);

    btnAddCondition = new Button(this, SWT.NONE);
    btnAddCondition.setText("Add Condition");
    btnAddCondition.setImage(RuleBeansIcons.CONDITION_COLUMN.createImage());

    btnAddOutput = new Button(this, SWT.NONE);
    btnAddOutput.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
    btnAddOutput.setText("Add Output");
    btnAddOutput.setImage(RuleBeansIcons.OUTPUT_COLUMN.createImage());
  }
}
