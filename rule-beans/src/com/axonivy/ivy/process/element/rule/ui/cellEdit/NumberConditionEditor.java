package com.axonivy.ivy.process.element.rule.ui.cellEdit;

import java.util.Arrays;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.axonivy.ivy.process.element.rule.model.ConditionCell;
import com.axonivy.ivy.process.element.rule.model.Operator;

import ch.ivyteam.swt.SwtSelectionUtil;

public class NumberConditionEditor extends CellEditor
{
  private StringConditionComposite composite;

  public NumberConditionEditor(Composite parent, int style)
  {
    super(parent, style);
  }
  
  @Override
  protected Control createControl(Composite parent)
  {
    composite = new StringConditionComposite(parent, SWT.NONE);
    composite.operation.setContentProvider(ArrayContentProvider.getInstance());
    composite.operation.setInput(Arrays.asList(
            Operator.NO_CONDITION,
            Operator.EQUAL, Operator.UNEQUAL,
            Operator.EQUAL_OR_GREATER, Operator.EQUAL_OR_SMALLER, 
            Operator.GREATER, Operator.LESS));
    composite.operation.setLabelProvider(new OperatorLabelProvider());
    composite.text.addVerifyListener(new NumericValueVerifier());
    return composite;
  }
  
  @Override
  protected void doSetFocus()
  {
    composite.text.setFocus();
  }

  @Override
  protected Object doGetValue()
  {
    Operator op = SwtSelectionUtil.getFirstElement(composite.operation.getSelection());
    if (op == null || op == Operator.NO_CONDITION)
    {
      return ConditionCell.NO_CONDITION;
    }
    return new ConditionCell(op, composite.text.getText());
  }

  @Override
  protected void doSetValue(Object value)
  {
    if (value instanceof ConditionCell)
    {
      ConditionCell cell = (ConditionCell) value;
      composite.operation.setSelection(new StructuredSelection(cell.getOperator()));
      composite.text.setText(cell.getFirstArgument());
    }
  }
  
  @Override
  protected int getDoubleClickTimeout()
  { // makes me fast
    return 0;
  }

}
