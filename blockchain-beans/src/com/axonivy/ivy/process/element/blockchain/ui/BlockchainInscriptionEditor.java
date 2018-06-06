package com.axonivy.ivy.process.element.blockchain.ui;

import java.util.Arrays;
import java.util.List;

import com.axonivy.ivy.process.element.blockchain.ProcessElementExtension;

import ch.ivyteam.ivy.designer.process.ui.inscriptionMasks.BpmnInscriptionEditor;
import ch.ivyteam.ivy.designer.process.ui.inscriptionMasks.fw.IInscriptionEditorTab;
import ch.ivyteam.ivy.process.model.element.ThirdPartyElement;

public class BlockchainInscriptionEditor implements BpmnInscriptionEditor
{

  @Override
  public boolean isResponsible(String modElementName)
  {
    return ProcessElementExtension.ETHEREUM_ACTIVITY.equals(modElementName);
  }

  @Override
  public List<IInscriptionEditorTab> getTabs(TabContext ctxt)
  {
    BlockchainRequestConfigurator configurator = new BlockchainRequestConfigurator(ctxt.project, (ThirdPartyElement) ctxt.element);
    BlockchainRequestUiModel uiModel = new BlockchainRequestUiModel(configurator);
    return Arrays.asList(new BlockchainRequestTab(uiModel));
  }

}
