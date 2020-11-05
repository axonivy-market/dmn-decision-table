package com.axonivy.ivy.process.element.blockchain.ui;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.axonivy.ivy.process.element.blockchain.EthereumProcessElement;

import ch.ivyteam.ivy.designer.process.ui.editor.palette.IIvyProcessPalette;
import ch.ivyteam.ivy.designer.process.ui.info.IBpmnProcessElementUi;
import ch.ivyteam.ivy.designer.process.ui.inscription.masks.fw.IInscriptionEditorTab;
import ch.ivyteam.ivy.process.config.activity.pi.ThirdPartyProgramInterfaceConfigurator;
import ch.ivyteam.ivy.process.config.element.ElementConfigurator;
import ch.ivyteam.ivy.process.model.NodeElement;

public class EthereumProcessElementUi implements IBpmnProcessElementUi
{
  private static final String BLOCKCHAIN_GROUP = "Blockchain";
  
  @Override
  public void addPaletteGroup(IIvyProcessPalette palette)
  {
    palette.addGroup(BLOCKCHAIN_GROUP, BLOCKCHAIN_GROUP, 12000);
  }

  @Override
  public void addPaletteEntry(IIvyProcessPalette palette, String name)
  {
    palette.addProcessElementEntry(BLOCKCHAIN_GROUP, name, 1000);
  }
  
  @Override
  public String getName()
  {
    return EthereumProcessElement.ETHEREUM_ACTIVITY;
  }
  
  @Override
  public String getShortName(Locale locale)
  {
    return "Ethereum";
  }

  @Override
  public String getName(Locale locale)
  {
    return "Ethereum Activity";
  }

  @Override
  public String getDescription(Locale locale)
  {
    return "Executes an Ethereum call";
  }

  @Override
  public URL getIcon()
  {
    return getClass().getResource("EthereumActivity.png");
  }
  
  @Override
  public String getHelpPath()
  {
    return "https://github.com/ivy-supplements/bpm-beans/blob/master/blockchain-beans/README.md";
  }
  
  @Override
  public List<IInscriptionEditorTab> getEditorTabs(ElementConfigurator<? extends NodeElement> configurator)
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
