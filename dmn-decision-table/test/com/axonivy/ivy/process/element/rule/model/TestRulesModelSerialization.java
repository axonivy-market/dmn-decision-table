package com.axonivy.ivy.process.element.rule.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestRulesModelSerialization
{

  @Test
  public void serialization() throws Exception
  {
    RulesModel model = new RulesModel();
    model.addColumn(new ConditionColumn("person.age", ColumnType.Number));
    model.addColumn(new ConditionColumn("person.gender", ColumnType.String));
    model.addColumn(new ActionColumn("tax.rate", ColumnType.String));

    Row rowModel = new Row();
    rowModel.addCell(new ConditionCell(Operator.LESS, "18"));
    rowModel.addCell(ConditionCell.NO_CONDITION);
    rowModel.addCell(new ValueCell("0"));
    model.addRow(rowModel);

    rowModel = new Row();
    rowModel.addCell(new ConditionCell(Operator.EQUAL_OR_GREATER, "18"));
    rowModel.addCell(new ConditionCell(Operator.EQUAL, "male"));
    rowModel.addCell(new ValueCell("12.5"));
    model.addRow(rowModel);

    rowModel = new Row();
    rowModel.addCell(new ConditionCell(Operator.EQUAL_OR_GREATER, "18"));
    rowModel.addCell(new ConditionCell(Operator.EQUAL, "female"));
    rowModel.addCell(new ValueCell("8.5"));
    model.addRow(rowModel);

    String json = RulesModelSerialization.serialize(model);
    RulesModel deserializedModel = RulesModelSerialization.deserialize(json);

    assertEquals(model, deserializedModel);
  }

}
