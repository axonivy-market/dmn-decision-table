package com.axonivy.ivy.process.element.rule.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.WRAPPER_OBJECT, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value=ValueCell.class, name="valueCell"),
  @JsonSubTypes.Type(value=ConditionCell.class, name="conditionCell"),
})
public abstract class Cell
{
  @JsonIgnore
  public abstract String getText();  
  
  @Override
  public String toString()
  {
    return getText();
  }
}
