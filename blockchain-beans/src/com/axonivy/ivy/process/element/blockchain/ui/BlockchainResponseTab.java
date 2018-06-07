package com.axonivy.ivy.process.element.blockchain.ui;

import org.eclipse.swt.widgets.Composite;

import ch.ivyteam.ivy.designer.process.ui.inscriptionMasks.fw.AbstractUiModelSwtInscriptionTab;
import ch.ivyteam.ivy.designer.process.ui.inscriptionMasks.fw.IInscriptionEditorTab;
import ch.ivyteam.ivy.ui.model.swt.IvySwtBinder;

public class BlockchainResponseTab extends AbstractUiModelSwtInscriptionTab<BlockchainRequestUiModel> implements IInscriptionEditorTab
{

  public BlockchainResponseTab(BlockchainRequestUiModel model)
  {
    super(model);
  }

  @Override
  public String getTabName()
  {
    return "Response";
  }

  @Override
  protected Composite createUiAndBindToModel(Composite parent, IvySwtBinder ivySwtBinder)
  {
    return new BlockchainResponseComposite(parent);
  }
}
