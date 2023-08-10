package com.axonivy.ivy.process.element.rule.ui;

import java.util.List;

import com.axonivy.ivy.process.element.rule.model.Row;
import com.axonivy.ivy.process.element.rule.model.RulesModel;
import com.axonivy.ivy.process.element.rule.model.RulesModelSerialization;
import com.fasterxml.jackson.core.JsonProcessingException;

import ch.ivyteam.ivy.designer.inscription.ui.model.UiModel;
import ch.ivyteam.ivy.process.config.activity.pi.ThirdPartyProgramInterfaceConfigurator;
import ch.ivyteam.ivy.process.model.element.activity.ThirdPartyProgramInterface;
import ch.ivyteam.ivy.process.model.element.value.bean.UserConfig;
import ch.ivyteam.log.Logger;
import ch.ivyteam.ui.model.UiTableModel;

public class DmnTableUiModel
        extends UiModel<ThirdPartyProgramInterface, ThirdPartyProgramInterfaceConfigurator> {

  private static final Logger LOGGER = Logger.getLogger(DmnTableUiModel.class);

  private final ThirdPartyProgramInterface element;
  public final RulesModel rules;
  public final UiTableModel<com.axonivy.ivy.process.element.rule.model.Row> table;

  public DmnTableUiModel(ThirdPartyProgramInterfaceConfigurator configurator) {
    super(configurator);
    element = configurator.getElement();
    rules = loadRulesModel();

    table = decisionTableModel();
    getTab().addChild(table);
  }

  private UiTableModel<Row> decisionTableModel() {
    return new UiTableModel<>(rules::getRows, this::updateRows).withDefaultValue(List.of());
  }

  private void updateRows(List<Row> rows) {
    rules.setRows(rows);
    storeRulesModel();
  }

  private RulesModel loadRulesModel() {
    try {
      return RulesModelSerialization.deserialize(element.getUserConfig().getRawValue());
    } catch (Exception ex) {
      LOGGER.error("Failed to load decision table config", ex);
      return new RulesModel();
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
