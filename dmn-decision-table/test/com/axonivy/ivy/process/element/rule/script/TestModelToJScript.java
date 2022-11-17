package com.axonivy.ivy.process.element.rule.script;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.axonivy.ivy.process.element.rule.model.ActionColumn;
import com.axonivy.ivy.process.element.rule.model.ColumnType;
import com.axonivy.ivy.process.element.rule.model.ConditionCell;
import com.axonivy.ivy.process.element.rule.model.ConditionColumn;
import com.axonivy.ivy.process.element.rule.model.Operator;
import com.axonivy.ivy.process.element.rule.model.Row;
import com.axonivy.ivy.process.element.rule.model.RulesModel;
import com.axonivy.ivy.process.element.rule.model.ValueCell;

public class TestModelToJScript {
  private RulesModel model;

  @Before
  public void before() {
    model = new RulesModel();
    model.addColumn(new ConditionColumn("person.age", ColumnType.Number));
    model.addColumn(new ConditionColumn("person.gender", ColumnType.String));
    model.addColumn(new ActionColumn("tax.rate", ColumnType.Number));
    Row rowModel = new Row();
    rowModel.addCell(new ConditionCell(Operator.LESS, "18"));
    rowModel.addCell(ConditionCell.NO_CONDITION);
    rowModel.addCell(new ValueCell("0"));
    model.addRow(rowModel);
    rowModel = new Row();
    rowModel.addCell(new ConditionCell(Operator.EQUAL_OR_GREATER, "18"));
    rowModel.addCell(new ConditionCell(Operator.EQUAL, "\"male\""));
    rowModel.addCell(new ValueCell("12.5"));
    model.addRow(rowModel);
    rowModel = new Row();
    rowModel.addCell(new ConditionCell(Operator.EQUAL_OR_GREATER, "18"));
    rowModel.addCell(new ConditionCell(Operator.EQUAL, "\"female\""));
    rowModel.addCell(new ValueCell("8.5"));
    model.addRow(rowModel);
  }

  @Test
  public void scriptGenerator() {
    String script = new ScriptGenerator(model).toScript();
    String expectedScript = "if ( person.age < 18 )\n" +
            "{\n" +
            "  tax.rate = 0 ;\n" +
            "}\n" +
            "else if ( person.age >= 18 && person.gender == \"male\" )\n" +
            "{\n" +
            "  tax.rate = 12.5 ;\n" +
            "}\n" +
            "else if ( person.age >= 18 && person.gender == \"female\" )\n" +
            "{\n" +
            "  tax.rate = 8.5 ;\n" +
            "}\n";
    assertEquals(expectedScript, script);
  }
}
