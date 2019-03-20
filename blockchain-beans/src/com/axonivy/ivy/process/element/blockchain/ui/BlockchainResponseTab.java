package com.axonivy.ivy.process.element.blockchain.ui;

import org.eclipse.swt.widgets.Composite;

import ch.ivyteam.ivy.designer.process.ui.inscription.masks.fw.IInscriptionEditorTab;
import ch.ivyteam.ivy.designer.process.ui.inscription.model.AbstractUiModelSwtInscriptionTab;
import ch.ivyteam.ivy.ui.model.swt.IvySwtBinder;

public class BlockchainResponseTab extends AbstractUiModelSwtInscriptionTab<BlockchainResponseUiModel> implements IInscriptionEditorTab
{

  public BlockchainResponseTab(BlockchainResponseUiModel model)
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
    BlockchainResponseComposite composite = new BlockchainResponseComposite(parent);
    ivySwtBinder.bind(model.response).to(composite.responseGroup);
    return composite;
  }
}
