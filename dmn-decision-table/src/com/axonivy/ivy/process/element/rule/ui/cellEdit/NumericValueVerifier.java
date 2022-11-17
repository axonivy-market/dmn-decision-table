package com.axonivy.ivy.process.element.rule.ui.cellEdit;

import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;

public class NumericValueVerifier implements VerifyListener {
  @Override
  public void verifyText(VerifyEvent e) {
    e.doit = NumberUtils.isParsable(getText(e));
  }

  private String getText(VerifyEvent e) {
    if (e.getSource() instanceof Text) {
      final String old = ((Text) e.getSource()).getText();
      String current = old.substring(0, e.start) + e.text + old.substring(e.end);
      if (current.isEmpty()) {
        return current;
      }
      if (current.indexOf(".") == current.length() - 1 || current.indexOf(",") == current.length() - 1) { // allow
                                                                                                          // one
                                                                                                          // decimal
                                                                                                          // separator
                                                                                                          // at
                                                                                                          // the
                                                                                                          // end
        return current.substring(0, current.length() - 1);
      }
      return current;
    }
    return e.text;
  }
}