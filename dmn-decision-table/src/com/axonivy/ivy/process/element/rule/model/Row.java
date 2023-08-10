package com.axonivy.ivy.process.element.rule.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Row {

  private final List<Cell> cells;

  public Row() {
    this.cells = new ArrayList<>();
  }

  public Row(Row other) {
    this.cells = new ArrayList<>(other.cells);
  }

  public List<Cell> getCells() {
    return cells;
  }

  public void addCell(Cell cell) {
    cells.add(cell);
  }

  public void addCell(int column, Cell cell) {
    cells.add(column, cell);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (obj.getClass() != Row.class) {
      return false;
    }
    Row other = (Row) obj;
    return new EqualsBuilder()
            .append(cells, other.cells)
            .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(cells).toHashCode();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("|");
    for (Cell cell : cells) {
      builder.append(cell);
      builder.append("|");
    }

    return builder.toString();
  }
}
