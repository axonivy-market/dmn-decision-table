package com.axonivy.ivy.process.element.rule.ui;

import java.net.URL;
import java.util.List;

import com.axonivy.ivy.process.element.rule.DecisionProcessElement;

import ch.ivyteam.ivy.designer.inscription.ui.masks.fw.IInscriptionEditorTab;
import ch.ivyteam.ivy.designer.process.ui.info.IBpmnProcessElementUi;
import ch.ivyteam.ivy.process.config.activity.pi.ThirdPartyProgramInterfaceConfigurator;
import ch.ivyteam.ivy.process.config.element.ElementConfigurator;
import ch.ivyteam.ivy.process.model.NodeElement;

public class DecisionProcessElementUi implements IBpmnProcessElementUi {

  @Override
  public String getName() {
    return DecisionProcessElement.DECISION_ACTIVITY;
  }

  @Override
  public URL getIcon() {
    return getClass().getResource("DecisionActivity.png");
  }

  @Override
  public boolean showDefaultTabs() {
    return false;
  }

  @Override
  public String getHelpPath() {
    return "https://market.axonivy.com/dmn-decision-table";
  }

  @Override
  public List<IInscriptionEditorTab> getEditorTabs(ElementConfigurator<? extends NodeElement> configurator) {
    return getThirdpartyTabs((ThirdPartyProgramInterfaceConfigurator) configurator);
  }

  private List<IInscriptionEditorTab> getThirdpartyTabs(ThirdPartyProgramInterfaceConfigurator configurator) {
    DmnTableUiModel requestUiModel = new DmnTableUiModel(configurator);
    return List.of(new DmnDecisionTab(requestUiModel));
  }
}
