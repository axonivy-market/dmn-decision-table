package com.axonivy.ivy.process.element.blockchain.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import ch.ivyteam.ivy.designer.process.ui.inscriptionMasks.fw.AbstractUiModelSwtInscriptionTab;
import ch.ivyteam.ivy.designer.process.ui.inscriptionMasks.fw.IInscriptionEditorTab;
import ch.ivyteam.ivy.ui.model.swt.IvySwtBinder;

public class BlockchainRequestTab extends AbstractUiModelSwtInscriptionTab<BlockchainRequestUiModel> implements IInscriptionEditorTab
{

  public BlockchainRequestTab(BlockchainRequestUiModel model)
  {
    super(model);
  }

  @Override
  public String getTabName()
  {
    return "Request";
  }

  @Override
  protected Composite createUiAndBindToModel(Composite parent, IvySwtBinder ivySwtBinder)
  {
    BlockchainRequestComposite composite = new BlockchainRequestComposite(parent, SWT.NONE);
    //ivySwtBinder.bind(model.GEt)
    //composite.label.
    return composite;
  }


}
