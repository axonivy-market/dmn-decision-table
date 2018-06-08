package com.axonivy.ivy.process.element.blockchain.ui;

import java.util.Arrays;
import java.util.List;

import com.axonivy.ivy.process.element.blockchain.ProcessElementExtension;

import ch.ivyteam.ivy.designer.process.ui.inscriptionMasks.BpmnInscriptionEditor;
import ch.ivyteam.ivy.designer.process.ui.inscriptionMasks.fw.IInscriptionEditorTab;
import ch.ivyteam.ivy.process.config.element.pi.ThirdPartyProgramInterfaceConfigurator;

public class BlockchainInscriptionEditor implements BpmnInscriptionEditor
{

  @Override
  public boolean isResponsible(String modElementName)
  {
    return ProcessElementExtension.ETHEREUM_ACTIVITY.equals(modElementName);
  }

  @Override
  public List<IInscriptionEditorTab> getTabs(ThirdPartyProgramInterfaceConfigurator configurator)
  {
    //BlockchainRequestConfigurator configurator = new BlockchainRequestConfigurator(ctxt.project, (ThirdPartyElement) ctxt.element);
    BlockchainRequestUiModel requestUiModel = new BlockchainRequestUiModel(configurator);
    BlockchainRequestUiModel responseUiModel = new BlockchainRequestUiModel(configurator);
    return Arrays.asList(new BlockchainRequestTab(requestUiModel), new BlockchainResponseTab(responseUiModel));
  }
}
