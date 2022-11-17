package com.axonivy.ivy.process.element.rule.ui.cellEdit;

import org.eclipse.jface.viewers.LabelProvider;

import com.axonivy.ivy.process.element.rule.model.Operator;

public class OperatorLabelProvider extends LabelProvider {

  @Override
  public String getText(Object element) {
    if (element instanceof Operator) {
      return ((Operator) element).getScriptToken();
    }
    return null;
  }
}
