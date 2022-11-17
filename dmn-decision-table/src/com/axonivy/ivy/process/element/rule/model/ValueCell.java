package com.axonivy.ivy.process.element.rule.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ValueCell extends Cell {

  private String value;
  public static final ValueCell NO_ASSIGNMENT = new ValueCell();

  private ValueCell() {
    this.value = null;
  }

  public ValueCell(String value) {
    this.value = value;
  }

  @JsonIgnore
  @Override
  public String getText() {
    return value;
  }

  public String getValue() {
    return value;
  }

  @JsonIgnore
  public boolean isNoAssignment() {
    return this == NO_ASSIGNMENT;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (obj.getClass() != ValueCell.class) {
      return false;
    }
    ValueCell other = (ValueCell) obj;
    return new EqualsBuilder()
            .append(value, other.value)
            .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(value).toHashCode();
  }
}
