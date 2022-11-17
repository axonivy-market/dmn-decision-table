package com.axonivy.ivy.process.element.rule.dmn;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.axonivy.ivy.process.element.rule.model.ActionColumn;
import com.axonivy.ivy.process.element.rule.model.ColumnType;
import com.axonivy.ivy.process.element.rule.model.ConditionCell;
import com.axonivy.ivy.process.element.rule.model.ConditionColumn;
import com.axonivy.ivy.process.element.rule.model.Operator;
import com.axonivy.ivy.process.element.rule.model.Row;
import com.axonivy.ivy.process.element.rule.model.RulesModel;
import com.axonivy.ivy.process.element.rule.model.ValueCell;

@RunWith(Parameterized.class)
public class TestDmnExecutionSimple {
  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
        {20d, "male", 12.5d},
        {18d, "female", 8.5d},
        {17d, "male", 0d},
        {5d, "female", 0d},
        {5d, "", 0d},
    });
  }

  private RulesModel model;

  private Person person;
  private Double taxRate;

  public TestDmnExecutionSimple(Double age, String gender, Double taxRate) {
    this.person = new Person(age, gender);
    this.taxRate = taxRate;
  }

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
    rowModel.addCell(new ConditionCell(Operator.EQUAL, "male"));
    rowModel.addCell(new ValueCell("12.5"));
    model.addRow(rowModel);

    rowModel = new Row();
    rowModel.addCell(new ConditionCell(Operator.EQUAL_OR_GREATER, "18"));
    rowModel.addCell(new ConditionCell(Operator.EQUAL, "female"));
    rowModel.addCell(new ValueCell("8.5"));
    model.addRow(rowModel);
  }

  @Test
  public void execute() {
    VariableMap variables = Variables.putValue("person", person);
    Map<String, Object> result = decide(variables);
    assertEquals(result.get("tax.rate"), taxRate);
  }

  private Map<String, Object> decide(VariableMap variables) {
    InputStream dmnInputStream = new DmnSerializer(model).serialize();
    DmnExecutor dmnExecution = new DmnExecutor(dmnInputStream, variables);
    return dmnExecution.execute();
  }

  public static class Person {
    public Double age;
    public String gender;

    public Person(Double age, String gender) {
      this.age = age;
      this.gender = gender;
    }

    public Double getAge() {
      return age;
    }

    public String getGender() {
      return gender;
    }
  }
}
