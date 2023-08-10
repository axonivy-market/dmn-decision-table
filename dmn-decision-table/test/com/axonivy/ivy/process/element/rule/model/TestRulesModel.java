package com.axonivy.ivy.process.element.rule.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.Test;

public class TestRulesModel {
  RulesModel model = new RulesModel();

  @Test
  public void addRow() {
    assertThat(model.getRows()).isEmpty();
    var row = new Row();

    model.addRow(row);

    assertThat(model.getRows()).containsExactly(row);
  }

  @Test
  public void addRow_noCells() {
    model.addColumn(new ConditionColumn("in.name", ColumnType.String));
    model.addColumn(new ActionColumn("in.name", ColumnType.String));

    var row = new Row();
    assertThatThrownBy(() -> model.addRow(row)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void addRow_noValue() {
    model.addColumn(new ConditionColumn("in.name", ColumnType.String));
    model.addColumn(new ActionColumn("in.name", ColumnType.String));

    var row = new Row();
    row.addCell(new ConditionCell(Operator.EQUAL, "Weiss"));
    row.addCell(new ConditionCell(Operator.EQUAL, "Weiss"));

    assertThatThrownBy(() -> model.addRow(row)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void addRow_noCondition() {
    model.addColumn(new ConditionColumn("in.name", ColumnType.String));
    model.addColumn(new ActionColumn("in.name", ColumnType.String));

    var row = new Row();
    row.addCell(new ValueCell("Weiss"));
    row.addCell(new ValueCell("Weiss"));

    assertThatThrownBy(() -> model.addRow(row)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void addRow_value_before_condition() {
    model.addColumn(new ConditionColumn("in.name", ColumnType.String));
    model.addColumn(new ActionColumn("in.name", ColumnType.String));
    var row = new Row();
    row.addCell(new ValueCell("Weiss"));
    row.addCell(new ConditionCell(Operator.EQUAL, "Weiss"));

    assertThatThrownBy(() -> model.addRow(row)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void addRow_withColumns() {
    assertThat(model.getRows()).isEmpty();
    model.addColumn(new ConditionColumn("in.name", ColumnType.String));
    model.addColumn(new ActionColumn("in.surname", ColumnType.String));
    var row = new Row();
    row.addCell(new ConditionCell(Operator.EQUAL, "Weiss"));
    row.addCell(new ValueCell("Reto"));

    model.addRow(row);

    assertThat(model.getRows()).containsExactly(row);
  }

  @Test
  public void addColumn_Condition() {
    model.addRow(new Row());
    model.addRow(new Row());
    assertThat(model.getColumns()).isEmpty();
    assertThat(model.getConditionColumns()).isEmpty();
    var conditionColumn = new ConditionColumn("in.name", ColumnType.String);

    model.addColumn(conditionColumn);

    assertThat(model.getColumns()).containsExactly(conditionColumn);
    assertThat(model.getConditionColumns()).containsExactly(conditionColumn);
    assertThat(model.getRows()).hasSize(2);
    assertThat(model.getRows().get(0).getCells()).containsExactly(ConditionCell.NO_CONDITION);
    assertThat(model.getRows().get(1).getCells()).containsExactly(ConditionCell.NO_CONDITION);
  }

  @Test
  public void addColumn_Action() {
    model.addRow(new Row());
    model.addRow(new Row());
    assertThat(model.getColumns()).isEmpty();
    assertThat(model.getActionColumns()).isEmpty();
    var actionColumn = new ActionColumn("out.surname", ColumnType.String);

    model.addColumn(actionColumn);

    assertThat(model.getColumns()).containsExactly(actionColumn);
    assertThat(model.getActionColumns()).containsExactly(actionColumn);
    assertThat(model.getRows()).hasSize(2);
    assertThat(model.getRows().get(0).getCells()).containsExactly(ValueCell.NO_ASSIGNMENT);
    assertThat(model.getRows().get(1).getCells()).containsExactly(ValueCell.NO_ASSIGNMENT);
  }

  @Test
  public void addColumn_multiple() {
    model.addRow(new Row());
    model.addRow(new Row());
    assertThat(model.getColumns()).isEmpty();
    assertThat(model.getActionColumns()).isEmpty();
    var actionColumn1 = new ActionColumn("out.surname", ColumnType.String);
    var actionColumn2 = new ActionColumn("out.surname", ColumnType.String);
    var actionColumn3 = new ActionColumn("out.name", ColumnType.String);
    var conditionColumn1 = new ConditionColumn("in.name", ColumnType.String);
    var conditionColumn2 = new ConditionColumn("in.name", ColumnType.String);
    var conditionColumn3 = new ConditionColumn("in.surname", ColumnType.String);

    model.addColumn(actionColumn1);
    model.addColumn(conditionColumn1);
    model.addColumn(actionColumn2);
    model.addColumn(conditionColumn2);
    model.addColumn(actionColumn3);
    model.addColumn(conditionColumn3);

    assertThat(model.getColumns())
        .containsExactly(conditionColumn1, conditionColumn2, conditionColumn3,
                         actionColumn1, actionColumn2, actionColumn3);
    assertThat(model.getConditionColumns()).containsExactly(conditionColumn1, conditionColumn2, conditionColumn3);
    assertThat(model.getActionColumns()).containsExactly(actionColumn1, actionColumn2, actionColumn3);
  }

  @Test
  public void rows() {
    Row row1 = new Row();
    ConditionCell cell11 = new ConditionCell(Operator.EQUAL, "Weiss");
    row1.addCell(cell11);
    ValueCell cell12 = new ValueCell("Reto");
    row1.addCell(cell12);
    Row row2 = new Row();
    ConditionCell cell21 = new ConditionCell(Operator.EQUAL, "Weiss");
    row2.addCell(cell21);
    ValueCell cell22 = new ValueCell("Reto");
    row2.addCell(cell22);
    var original = List.of(row1, row2);
    model.setRows(original);
    var copy = model.getRows();

    assertThat(copy).isEqualTo(original);
    assertThat(copy).isNotSameAs(original);

    assertThat(copy.get(0)).isEqualTo(original.get(0));
    assertThat(copy.get(0)).isNotSameAs(original.get(0));
    assertThat(copy.get(1)).isEqualTo(original.get(1));
    assertThat(copy.get(1)).isNotSameAs(original.get(1));

    assertThat(copy.get(0).getCells()).isEqualTo(original.get(0).getCells());
    assertThat(copy.get(0).getCells()).isNotSameAs(original.get(0).getCells());
    assertThat(copy.get(1).getCells()).isEqualTo(original.get(1).getCells());
    assertThat(copy.get(1).getCells()).isNotSameAs(original.get(1).getCells());

    assertThat(copy.get(0).getCells().get(0)).isEqualTo(original.get(0).getCells().get(0));
    assertThat(copy.get(0).getCells().get(0)).isSameAs(original.get(0).getCells().get(0));
    assertThat(copy.get(0).getCells().get(1)).isEqualTo(original.get(0).getCells().get(1));
    assertThat(copy.get(0).getCells().get(1)).isSameAs(original.get(0).getCells().get(1));

    assertThat(copy.get(1).getCells().get(0)).isEqualTo(original.get(1).getCells().get(0));
    assertThat(copy.get(1).getCells().get(0)).isSameAs(original.get(1).getCells().get(0));
    assertThat(copy.get(1).getCells().get(1)).isEqualTo(original.get(1).getCells().get(1));
    assertThat(copy.get(1).getCells().get(1)).isSameAs(original.get(1).getCells().get(1));
  }

  @Test
  public void setCell() {
    model.addRow(new Row());
    model.addRow(new Row());
    assertThat(model.getColumns()).isEmpty();
    assertThat(model.getActionColumns()).isEmpty();
    var actionColumn1 = new ActionColumn("out.surname", ColumnType.String);
    var actionColumn2 = new ActionColumn("out.surname", ColumnType.String);
    var actionColumn3 = new ActionColumn("out.name", ColumnType.String);
    var conditionColumn1 = new ConditionColumn("in.name", ColumnType.String);
    var conditionColumn2 = new ConditionColumn("in.name", ColumnType.String);
    var conditionColumn3 = new ConditionColumn("in.surname", ColumnType.String);

    model.addColumn(actionColumn1);
    model.addColumn(conditionColumn1);
    model.addColumn(actionColumn2);
    model.addColumn(conditionColumn2);
    model.addColumn(actionColumn3);
    model.addColumn(conditionColumn3);

    Row row0 = model.getRows().get(0);
    Row row1 = model.getRows().get(1);
    var valueCell = new ValueCell("Reto");
    var conditionCell = new ConditionCell(Operator.EQUAL, "Weiss");
    model.setCell(row0, actionColumn2, valueCell);
    model.setCell(row1, conditionColumn2, conditionCell);

    assertThat(row0.getCells()).containsExactly(
            ConditionCell.NO_CONDITION, ConditionCell.NO_CONDITION, ConditionCell.NO_CONDITION,
            ValueCell.NO_ASSIGNMENT, valueCell, ValueCell.NO_ASSIGNMENT);
    assertThat(row1.getCells()).containsExactly(
            ConditionCell.NO_CONDITION, conditionCell, ConditionCell.NO_CONDITION,
            ValueCell.NO_ASSIGNMENT, ValueCell.NO_ASSIGNMENT, ValueCell.NO_ASSIGNMENT);
  }

  @Test
  public void getCell() {
    model.addRow(new Row());
    model.addRow(new Row());
    assertThat(model.getColumns()).isEmpty();
    assertThat(model.getActionColumns()).isEmpty();
    var actionColumn1 = new ActionColumn("out.surname", ColumnType.String);
    var actionColumn2 = new ActionColumn("out.surname", ColumnType.String);
    var actionColumn3 = new ActionColumn("out.name", ColumnType.String);
    var conditionColumn1 = new ConditionColumn("in.name", ColumnType.String);
    var conditionColumn2 = new ConditionColumn("in.name", ColumnType.String);
    var conditionColumn3 = new ConditionColumn("in.surname", ColumnType.String);

    model.addColumn(actionColumn1);
    model.addColumn(conditionColumn1);
    model.addColumn(actionColumn2);
    model.addColumn(conditionColumn2);
    model.addColumn(actionColumn3);
    model.addColumn(conditionColumn3);

    Row row0 = model.getRows().get(0);
    Row row1 = model.getRows().get(1);
    var valueCell = new ValueCell("Reto");
    var conditionCell = new ConditionCell(Operator.EQUAL, "Weiss");
    model.setCell(row0, actionColumn2, valueCell);
    model.setCell(row1, conditionColumn2, conditionCell);

    assertThat(model.getCell(row0, conditionColumn1)).isEqualTo(ConditionCell.NO_CONDITION);
    assertThat(model.getCell(row0, conditionColumn2)).isEqualTo(ConditionCell.NO_CONDITION);
    assertThat(model.getCell(row0, conditionColumn3)).isEqualTo(ConditionCell.NO_CONDITION);
    assertThat(model.getCell(row0, actionColumn1)).isEqualTo(ValueCell.NO_ASSIGNMENT);
    assertThat(model.getCell(row0, actionColumn2)).isEqualTo(valueCell);
    assertThat(model.getCell(row0, actionColumn3)).isEqualTo(ValueCell.NO_ASSIGNMENT);

    assertThat(model.getCell(row1, conditionColumn1)).isEqualTo(ConditionCell.NO_CONDITION);
    assertThat(model.getCell(row1, conditionColumn2)).isEqualTo(conditionCell);
    assertThat(model.getCell(row1, conditionColumn3)).isEqualTo(ConditionCell.NO_CONDITION);
    assertThat(model.getCell(row1, actionColumn1)).isEqualTo(ValueCell.NO_ASSIGNMENT);
    assertThat(model.getCell(row1, actionColumn2)).isEqualTo(ValueCell.NO_ASSIGNMENT);
    assertThat(model.getCell(row1, actionColumn3)).isEqualTo(ValueCell.NO_ASSIGNMENT);
  }

}
