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

public class BooleanConditionEditor extends CellEditor {

  private ComboConditionComposite composite;

  public BooleanConditionEditor(Composite parent, int style) {
    super(parent, style);
  }

  @Override
  protected Control createControl(Composite parent) {
    composite = new ComboConditionComposite(parent, SWT.NONE);
    composite.operation.setContentProvider(ArrayContentProvider.getInstance());
    composite.operation.setInput(Arrays.asList(Operator.NO_CONDITION,
            Operator.EQUAL, Operator.UNEQUAL));
    composite.operation.setLabelProvider(new OperatorLabelProvider());

    composite.value.setContentProvider(ArrayContentProvider.getInstance());
    composite.value.setInput(Arrays.asList(Boolean.TRUE, Boolean.FALSE));
    return composite;
  }

  @Override
  protected void doSetFocus() {
    composite.value.getCombo().setFocus();
  }

  @Override
  protected Object doGetValue() {
    Operator op = SwtSelectionUtil.getFirstElement(composite.operation.getSelection());
    if (op == null || op == Operator.NO_CONDITION) {
      return new ConditionCell(Operator.NO_CONDITION);
    }
    Boolean value = SwtSelectionUtil.getFirstElement(composite.value.getSelection());
    return new ConditionCell(op, value.toString());
  }

  @Override
  protected void doSetValue(Object value) {
    if (value instanceof ConditionCell) {
      ConditionCell cell = (ConditionCell) value;
      composite.operation.setSelection(new StructuredSelection(cell.getOperator()));
      composite.value.setSelection(new StructuredSelection(Boolean.valueOf(cell.getFirstArgument())));
    }
  }

  @Override
  protected int getDoubleClickTimeout() { // makes me fast
    return 0;
  }

}
