package com.axonivy.ivy.process.element.blockchain.ui;

import java.util.Arrays;
import java.util.List;

import com.axonivy.ivy.process.element.blockchain.ProcessElementExtension;

import ch.ivyteam.ivy.designer.process.ui.inscriptionMasks.BpmnInscriptionEditor;
import ch.ivyteam.ivy.designer.process.ui.inscriptionMasks.fw.IInscriptionEditorTab;
import ch.ivyteam.ivy.process.config.element.AbstractProcessElementConfigurator;
import ch.ivyteam.ivy.process.config.element.pi.ThirdPartyProgramInterfaceConfigurator;

@SuppressWarnings("restriction")
public class BlockchainInscriptionEditor implements BpmnInscriptionEditor
{

  @Override
  public boolean isResponsible(String modElementName)
  {
    return ProcessElementExtension.ETHEREUM_ACTIVITY.equals(modElementName);
  }
  
  @Override
  public <T extends AbstractProcessElementConfigurator<? extends NodeElement>> List<IInscriptionEditorTab> getTabs(
          T configurator)
  {
    return getTabs((ThirdPartyProgramInterfaceConfigurator)configurator);
  }

  private List<IInscriptionEditorTab> getTabs(ThirdPartyProgramInterfaceConfigurator configurator)
  {
    BlockchainRequestUiModel requestUiModel = new BlockchainRequestUiModel(configurator);
    BlockchainResponseUiModel responseUiModel = new BlockchainResponseUiModel(configurator, requestUiModel);
    return Arrays.asList(new BlockchainRequestTab(requestUiModel), new BlockchainResponseTab(responseUiModel));
  }
}
