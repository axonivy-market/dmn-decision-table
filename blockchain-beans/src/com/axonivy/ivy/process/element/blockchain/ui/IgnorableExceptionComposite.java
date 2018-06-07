package com.axonivy.ivy.process.element.blockchain.ui;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import ch.ivyteam.eclipse.util.EclipseSelectionUtil;
import ch.ivyteam.icons.Size;
import ch.ivyteam.ivy.guiComponents.swt.dialogs.error.ErrorCodeSearchDialog;
import ch.ivyteam.ivy.guiComponents.swt.dialogs.search.SelectionResult;
import ch.ivyteam.ivy.process.model.element.value.IgnoreableExceptionConfig;
import ch.ivyteam.ivy.process.model.value.ErrorCode;
import ch.ivyteam.ivy.process.model.value.EventCode;
import ch.ivyteam.ivy.project.IIvyProject;
import ch.ivyteam.swt.icons.IconFactory;
import ch.ivyteam.swt.listeners.ComboUserInputSelector;
import ch.ivyteam.swt.widgets.ExtendableComboViewer;

public class IgnorableExceptionComposite extends Composite
{
  public final ExtendableComboViewer errorComboViewer;
  private final ToolItem btnErrorCodeChooser;
  private IIvyProject project;
  
  @SuppressWarnings("unused")
  public IgnorableExceptionComposite(Composite parent, int style)
  {
    super(parent, style);
    
    GridLayout grid = new GridLayout(2, false);
    grid.marginBottom = 0;
    grid.marginWidth = 0;
    grid.marginHeight = 0;
    grid.horizontalSpacing = 0;
    grid.verticalSpacing = 0;
    setLayout(grid);
    
    errorComboViewer = new ExtendableComboViewer(this, SWT.NONE);
    
    new ComboUserInputSelector(errorComboViewer)
    {
      @Override
      protected Object toObject(String userText)
      {
        if (StringUtils.isNotBlank(userText) && !isExistingLabel(userText.trim()))
        {
          return toIgnoreableExceptionConfig(userText);
        }
        return null;
      }

      private boolean isExistingLabel(String userText)
      {
        IBaseLabelProvider baseProvider = errorComboViewer.getLabelProvider();
        if (baseProvider instanceof ILabelProvider)
        {
          ILabelProvider labels = (ILabelProvider) baseProvider;
          return Arrays.stream(getInput())
                  .map(errorCode -> labels.getText(errorCode))
                  .filter(label -> userText.equals(label))
                  .findAny().isPresent();
        }
        return false;
      }
    };
    
    Combo combo = errorComboViewer.getCombo();
    combo.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true, 1, 1));
    errorComboViewer.setContentProvider(ArrayContentProvider.getInstance());
    
    ToolBar toolbar = new ToolBar(this, SWT.HORIZONTAL);
    toolbar.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
    
    btnErrorCodeChooser = new ToolItem(toolbar, SWT.FLAT);
    Image classImg = IconFactory.get(toolbar).getIvyException(Size.SIZE_16);
    btnErrorCodeChooser.setImage(classImg);
    btnErrorCodeChooser.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        SelectionResult<EventCode<?>> result = ErrorCodeSearchDialog
                .create(toolbar.getShell(), project)
                .withFilter(ErrorCodeSearchDialog.ErrorCodeSearchFilter.createForThrower())
                .open();
        if (result != null)
        {
          if (result.isOk() && result.getSelection() instanceof ErrorCode)
          {
            ErrorCode newCode = (ErrorCode) result.getSelection();
            IgnoreableExceptionConfig config = IgnoreableExceptionConfig.createDefault(newCode);
            errorComboViewer.setSelection(new StructuredSelection(config));
          }
        }
      }
    });
  }

  public void setIvyProject(IIvyProject project)
  {
    this.project = project;
  }
  
  public void setInput(IgnoreableExceptionConfig[] values)
  {
    this.errorComboViewer.setInput(values);
  }

  private IgnoreableExceptionConfig[] getInput()
  {
    return (IgnoreableExceptionConfig[]) this.errorComboViewer.getInput();
  }
  
  public IgnoreableExceptionConfig getSelected()
  {
    String errorHandler = EclipseSelectionUtil.getFirstElement(this.errorComboViewer.getSelection());
    return toIgnoreableExceptionConfig(errorHandler);
  }
  
  private static IgnoreableExceptionConfig toIgnoreableExceptionConfig(String errorHandler)
  {
    return IgnoreableExceptionConfig.createFromErrorHandlerString(errorHandler, ErrorCode.IVY_ERROR_WEB_SERVICE_EXCEPTION);
  }
}
