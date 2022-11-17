package com.axonivy.ivy.process.element.rule.ui;

import com.axonivy.ivy.process.element.rule.model.RulesModel;
import com.axonivy.ivy.process.element.rule.model.RulesModelSerialization;
import com.fasterxml.jackson.core.JsonProcessingException;

import ch.ivyteam.ivy.designer.inscription.ui.model.UiModel;
import ch.ivyteam.ivy.process.config.activity.pi.ThirdPartyProgramInterfaceConfigurator;
import ch.ivyteam.ivy.process.model.element.activity.ThirdPartyProgramInterface;
import ch.ivyteam.ivy.process.model.element.value.bean.UserConfig;

public class DmnTableUiModel extends UiModel<ThirdPartyProgramInterface, ThirdPartyProgramInterfaceConfigurator>
{
  private final ThirdPartyProgramInterface element;
  private RulesModel rules;

  public DmnTableUiModel(ThirdPartyProgramInterfaceConfigurator configurator) {
    super(configurator);
    element = configurator.getElement();
  }

  public RulesModel loadRulesModel()
  {
    try {
      this.rules = RulesModelSerialization.deserialize(element.getUserConfig().getRawValue());
      return rules;
    } catch (Exception ex) {
      throw new RuntimeException("Failed to load decision table config", ex);
    }
  }

  public void storeRulesModel() {
    try {
      String json = RulesModelSerialization.serialize(rules);
      element.setUserConfig(new UserConfig(json));
    } catch (JsonProcessingException ex) {
      throw new RuntimeException("Failed to store decision table config", ex);
    }
  }
}
