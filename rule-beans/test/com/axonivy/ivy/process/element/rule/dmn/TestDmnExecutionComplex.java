package com.axonivy.ivy.process.element.rule.dmn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
public class TestDmnExecutionComplex
{
  @Parameters
  public static Collection<Object[]> data()
  {
    return Arrays.asList(new Object[][] {
        {new Person(11d, "male", true), true, "schnipo", 2d, true},
        {new Person(12d, "male", true), true, "schnipo", 2d, true},
        {new Person(10d, "female", true), true, "fitnessmen", 2d, true},
        {new Person(13.2d, "female", true), true, "spezialmen", 2d, true},
        {new Person(65d, "female", true), true, "fitnessmen", 2d, false},
        {new Person(64d, "male", true), false, "chabis", 1d, true},
        {new Person(80d, "male", true), true, "gummelstungis", 1.5d, true},
    });
  }

  private RulesModel model;

  private Person person;
  private Boolean starter;
  private String mainCourse;
  private Double glaceBalls;
  
  private boolean match;
  
  public TestDmnExecutionComplex(Person person, Boolean starter, String mainCourse, Double glaceBalls, boolean match)
  {
    this.person = person;    
    this.starter = starter;
    this.mainCourse = mainCourse;
    this.glaceBalls = glaceBalls;
    this.match = match;
  }

  @Before
  public void before()
  {
    model = new RulesModel();

    model.addColumn(new ConditionColumn("person.age", ColumnType.Number));
    model.addColumn(new ConditionColumn("person.gender", ColumnType.String));
    model.addColumn(new ConditionColumn("person.meatLover", ColumnType.Boolean));
    
    model.addColumn(new ActionColumn("dish.starter", ColumnType.Boolean));
    model.addColumn(new ActionColumn("dish.mainCourse", ColumnType.String));
    model.addColumn(new ActionColumn("dish.glaceBalls", ColumnType.Number));

    Row rowModel = new Row();
    rowModel.addCell(new ConditionCell(Operator.EQUAL_OR_SMALLER, "12"));
    rowModel.addCell(new ConditionCell(Operator.EQUAL, "male"));
    rowModel.addCell(ConditionCell.NO_CONDITION);
    rowModel.addCell(new ValueCell("true"));
    rowModel.addCell(new ValueCell("schnipo"));
    rowModel.addCell(new ValueCell("2"));
    model.addRow(rowModel);
    
    rowModel = new Row();
    rowModel.addCell(new ConditionCell(Operator.EQUAL_OR_SMALLER, "12"));
    rowModel.addCell(new ConditionCell(Operator.EQUAL, "female"));
    rowModel.addCell(ConditionCell.NO_CONDITION);
    rowModel.addCell(new ValueCell("true"));
    rowModel.addCell(new ValueCell("fitnessmen"));
    rowModel.addCell(new ValueCell("2"));
    model.addRow(rowModel);
    
    rowModel = new Row();
    rowModel.addCell(new ConditionCell(Operator.EQUAL, "13.2"));
    rowModel.addCell(ConditionCell.NO_CONDITION);
    rowModel.addCell(ConditionCell.NO_CONDITION);
    rowModel.addCell(new ValueCell("true"));
    rowModel.addCell(new ValueCell("spezialmen"));
    rowModel.addCell(new ValueCell("2"));
    model.addRow(rowModel);
    
    rowModel = new Row();
    rowModel.addCell(new ConditionCell(Operator.GREATER, "65"));
    rowModel.addCell(ConditionCell.NO_CONDITION);
    rowModel.addCell(ConditionCell.NO_CONDITION);
    rowModel.addCell(new ValueCell("true"));
    rowModel.addCell(new ValueCell("gummelstungis"));
    rowModel.addCell(new ValueCell("1.5"));
    model.addRow(rowModel);
    
    rowModel = new Row();
    rowModel.addCell(new ConditionCell(Operator.UNEQUAL, "65"));
    rowModel.addCell(new ConditionCell(Operator.UNEQUAL, "female"));
    rowModel.addCell(ConditionCell.NO_CONDITION);
    rowModel.addCell(new ValueCell("false"));
    rowModel.addCell(new ValueCell("chabis"));
    rowModel.addCell(new ValueCell("1"));
    model.addRow(rowModel);
  }

  @Test
  public void execute()
  {
    VariableMap variables = Variables.putValue("person", person);
    Map<String, Object> r = decide(variables);
    if (match) {
      assertEquals(r.get("dish.starter"), starter);
      assertEquals(r.get("dish.mainCourse"), mainCourse);
      assertEquals(r.get("dish.glaceBalls"), glaceBalls);
    } else {
      assertTrue(r.isEmpty());
    }
  }

  private Map<String, Object> decide(VariableMap variables)
  {
    InputStream dmnInputStream = new DmnSerializer(model).serialize();
    DmnExecutor dmnExecution = new DmnExecutor(dmnInputStream, variables);
    return dmnExecution.execute();
  }

  public static class Person
  {
    public Double age;
    public String gender;
    public Boolean meatLover;

    public Person(Double age, String gender, Boolean meatLover)
    {
      this.age = age;
      this.gender = gender;
      this.meatLover = meatLover;
    }

    public Double getAge()
    {
      return age;
    }

    public String getGender()
    {
      return gender;
    }
    
    public Boolean getMeatLover()
    {
      return meatLover;
    }
  }
}
