package com.axonivy.ivy.process.element.rule.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Column {

  private final String attributeName;
  private final ColumnType type;

  public Column(String attributeName) {
    this(attributeName, ColumnType.String);
  }

  public Column(String attributeName, ColumnType type) {
    this.attributeName = attributeName;
    this.type = type;
  }

  @JsonIgnore
  public String getText() {
    return attributeName;
  }

  public String getAttributeName() {
    return attributeName;
  }

  public ColumnType getType() {
    return type;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (obj.getClass() != this.getClass()) {
      return false;
    }
    Column other = (Column) obj;
    return new EqualsBuilder()
            .append(this.attributeName, other.attributeName)
            .append(this.type, other.type)
            .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
            .append(attributeName)
            .append(type)
            .toHashCode();
  }
}
