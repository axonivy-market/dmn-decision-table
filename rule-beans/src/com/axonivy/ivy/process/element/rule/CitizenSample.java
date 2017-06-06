package com.axonivy.ivy.process.element.rule;

import com.axonivy.ivy.process.element.rule.model.ActionColumn;
import com.axonivy.ivy.process.element.rule.model.ColumnType;
import com.axonivy.ivy.process.element.rule.model.ConditionCell;
import com.axonivy.ivy.process.element.rule.model.ConditionColumn;
import com.axonivy.ivy.process.element.rule.model.Operator;
import com.axonivy.ivy.process.element.rule.model.Row;
import com.axonivy.ivy.process.element.rule.model.RulesModel;
import com.axonivy.ivy.process.element.rule.model.ValueCell;

public class CitizenSample
{
  private CitizenSample()
  {
  }

  public static RulesModel generateData()
  {
    RulesModel model = new RulesModel();
    addData(model);
    return model;
  }

  private static void addData(RulesModel model)
  {
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
  }

}
