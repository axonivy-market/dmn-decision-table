package com.axonivy.ivy.process.element.rule.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConditionColumn extends Column {

  public ConditionColumn(
          @JsonProperty("attributeName") String attributeName,
          @JsonProperty("type") ColumnType type) {
    super(attributeName, type);
  }

  @Override
  public String toString() {
    return getAttributeName() + "?";
  }
}
