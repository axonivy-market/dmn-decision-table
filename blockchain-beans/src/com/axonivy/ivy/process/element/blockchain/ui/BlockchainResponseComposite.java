package com.axonivy.ivy.process.element.blockchain.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.SharedScrolledComposite;

import ch.ivyteam.ivy.designer.ide.actions.AttributeBrowserAction;
import ch.ivyteam.ivy.designer.ide.actions.DatatypeBrowserAction;
import ch.ivyteam.ivy.designer.ide.actions.FunctionBrowserAction;
import ch.ivyteam.ivy.guiComponents.swt.restricted.mapping.MappingTreeWithCode;
import ch.ivyteam.swt.layout.GridDataBuilder;

public class BlockchainResponseComposite extends SharedScrolledComposite
{
  final MappingTreeWithCode responseGroup;

  public BlockchainResponseComposite(Composite parent)
  {
    super(parent, SWT.H_SCROLL | SWT.V_SCROLL);

    setExpandHorizontal(true);
    setExpandVertical(true);

    Composite scrollContent = new Composite(this, SWT.NONE);
    scrollContent.setLayout(new GridLayout(1, true));

    setContent(scrollContent);

    responseGroup = new MappingTreeWithCode(scrollContent, SWT.NONE,
            DatatypeBrowserAction.PROVIDER,
            FunctionBrowserAction.PROVIDER,
            AttributeBrowserAction.PROVIDER);
    responseGroup.setLayoutData(GridDataBuilder.create().horizontalFill().verticalFill().toGridData());
  }

  @SuppressWarnings("unused")
  public static void main(String[] args)
  {
    Display display = Display.getDefault();
    Shell shell = new Shell();

    new BlockchainResponseComposite(shell);

    shell.setLayout(new FillLayout());
    shell.setSize(600,600);
    shell.setText("Test Blockchain Response UI");
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
