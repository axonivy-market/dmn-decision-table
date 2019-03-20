package com.axonivy.ivy.process.element.blockchain.ui;

import java.util.Arrays;
import java.util.List;

import com.axonivy.ivy.process.element.blockchain.ProcessElementExtension;

import ch.ivyteam.ivy.designer.process.ui.inscription.masks.fw.IInscriptionEditorTab;
import ch.ivyteam.ivy.designer.process.ui.thirdparty.BpmnInscriptionEditor;
import ch.ivyteam.ivy.process.config.activity.pi.ThirdPartyProgramInterfaceConfigurator;
import ch.ivyteam.ivy.process.config.element.ElementConfigurator;
import ch.ivyteam.ivy.process.model.NodeElement;

public class BlockchainInscriptionEditor implements BpmnInscriptionEditor
{

  @Override
  public boolean isResponsible(String modElementName)
  {
    return ProcessElementExtension.ETHEREUM_ACTIVITY.equals(modElementName);
  }
  
  @Override
  public <T extends ElementConfigurator<? extends NodeElement>> List<IInscriptionEditorTab> getTabs(
          T configurator)
  {
    return getThirdpartyTabs((ThirdPartyProgramInterfaceConfigurator)configurator);
  }

  private List<IInscriptionEditorTab> getThirdpartyTabs(ThirdPartyProgramInterfaceConfigurator configurator)
  {
    BlockchainRequestUiModel requestUiModel = new BlockchainRequestUiModel(configurator);
    BlockchainResponseUiModel responseUiModel = new BlockchainResponseUiModel(configurator, requestUiModel);
    return Arrays.asList(new BlockchainRequestTab(requestUiModel), new BlockchainResponseTab(responseUiModel));
  }
}
