package com.axonivy.ivy.process.element.rule.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import ch.ivyteam.swt.icons.IconFactory;

public class DataMappingComposite extends Composite
{
  public final StyledText text;
  public final ToolItem btnAttributeBrowser;
  
  public DataMappingComposite(Composite parent, int style)
  {
    super(parent, style);
    
    GridLayout layout = new GridLayout(2, false);
    layout.horizontalSpacing = 0;
    layout.verticalSpacing = 0;
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    this.setLayout(layout);
    
    text = new StyledText(this, SWT.BORDER);
    text.setBackground(getGrey());
    text.setEnabled(false);
    text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    text.setTopMargin(2);
    
    ToolBar toolBar = new ToolBar(this, SWT.HORIZONTAL);
    toolBar.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1));
    btnAttributeBrowser = new ToolItem(toolBar, SWT.NONE);
    btnAttributeBrowser.setImage(IconFactory.get(this).getAttribute16());
  }

  private static Color getGrey()
  {
    Display display = Display.getCurrent();
    return display.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
  }

}
