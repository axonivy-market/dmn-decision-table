package com.axonivy.ivy.process.element.rule.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class RulesModel {

  private List<ConditionColumn> conditionColumns = new ArrayList<>();
  private List<ActionColumn> actionColumns = new ArrayList<>();
  public List<Row> rows = new ArrayList<>();

  public List<ConditionColumn> getConditionColumns() {
    return Collections.unmodifiableList(conditionColumns);
  }

  public List<ActionColumn> getActionColumns() {
    return Collections.unmodifiableList(actionColumns);
  }

  @JsonIgnore
  public List<Column> getColumns() {
    List<Column> columns = new ArrayList<>(conditionColumns);
    columns.addAll(actionColumns);
    return columns;
  }

  public List<Row> getRows() {
    return Collections.unmodifiableList(rows);
  }

  public void addColumn(ConditionColumn conditionColumn) {
    conditionColumns.add(conditionColumn);
    for (Row row : rows) {
      if (row.getCells().size() < getNumberOfColumns()) {
        row.addCell(conditionColumns.size() - 1, ConditionCell.NO_CONDITION);
      }
    }
  }

  private int getNumberOfColumns() {
    return conditionColumns.size() + actionColumns.size();
  }

  public void addColumn(ActionColumn actionColumn) {
    actionColumns.add(actionColumn);
    for (Row row : rows) {
      if (row.getCells().size() < getNumberOfColumns()) {
        row.addCell(ValueCell.NO_ASSIGNMENT);
      }
    }
  }

  public void addRow(Row row) {
    checkRow(row);
    rows.add(row);
  }

  private void checkRow(Row row) {
    int expectedCells = getNumberOfColumns();
    if (row.getCells().size() != expectedCells) {
      throw new IllegalArgumentException(
              "Parameter row must have " + expectedCells + ". cells but has " + row.getCells().size() + ".");
    }

    long conditionCells = row.getCells().stream().filter(cell -> cell instanceof ConditionCell).count();
    if (conditionCells != conditionColumns.size()) {
      throw new IllegalArgumentException("Parameter row must have " + conditionColumns.size()
              + ". condition cells but has " + conditionCells + ".");
    }

    long actionCells = row.getCells().stream().filter(cell -> cell instanceof ValueCell).count();
    if (actionCells != actionColumns.size()) {
      throw new IllegalArgumentException("Parameter row must have " + actionColumns.size()
              + ". value cells but has " + actionCells + ".");
    }

    boolean valueCell = false;
    for (Cell cell : row.getCells()) {
      if (cell instanceof ValueCell) {
        valueCell = true;
      }
      if (cell instanceof ConditionCell && valueCell) {
        throw new IllegalArgumentException(
                "Parameter row must not have a condition cell after a value cell.");
      }
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (obj.getClass() != RulesModel.class) {
      return false;
    }
    RulesModel other = (RulesModel) obj;
    return new EqualsBuilder()
            .append(conditionColumns, other.conditionColumns)
            .append(actionColumns, other.actionColumns)
            .append(rows, other.rows)
            .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(conditionColumns).append(actionColumns).append(rows).toHashCode();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("|");
    for (ConditionColumn conditionColumn : conditionColumns) {
      builder.append(conditionColumn);
      builder.append("|");
    }
    for (ActionColumn actionColumn : actionColumns) {
      builder.append(actionColumn);
      builder.append("|");
    }
    for (Row row : rows) {
      builder.append("\n");
      builder.append(row);
    }
    return builder.toString();
  }
}
