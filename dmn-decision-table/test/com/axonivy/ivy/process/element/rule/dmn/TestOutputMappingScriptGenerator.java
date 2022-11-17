package com.axonivy.ivy.process.element.rule.dmn;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import org.camunda.bpm.dmn.engine.impl.DmnDecisionRuleResultImpl;
import org.camunda.bpm.engine.variable.impl.value.PrimitiveTypeValueImpl.BooleanValueImpl;
import org.camunda.bpm.engine.variable.impl.value.PrimitiveTypeValueImpl.DoubleValueImpl;
import org.camunda.bpm.engine.variable.impl.value.PrimitiveTypeValueImpl.IntegerValueImpl;
import org.camunda.bpm.engine.variable.impl.value.PrimitiveTypeValueImpl.StringValueImpl;
import org.junit.Test;

public class TestOutputMappingScriptGenerator {
  @Test
  public void mapStringProperty() {
    DmnDecisionRuleResultImpl result = new DmnDecisionRuleResultImpl();
    result.putValue("out.person.name", new StringValueImpl("Alex"));
    assertIvyScript(result, "in.person.name = \"Alex\";");
  }

  @Test
  public void mapStringProperty_whichCouldBeABoolean() {
    DmnDecisionRuleResultImpl result = new DmnDecisionRuleResultImpl();
    result.putValue("out.person.name", new StringValueImpl("56"));
    assertIvyScript(result, "in.person.name = \"56\";");
  }

  @Test
  public void mapBooelanProperty() {
    DmnDecisionRuleResultImpl result = new DmnDecisionRuleResultImpl();
    result.putValue("out.person.gender", new BooleanValueImpl(true));
    assertIvyScript(result, "in.person.gender = true;");
  }

  @Test
  public void mapIntegerProperty() {
    DmnDecisionRuleResultImpl result = new DmnDecisionRuleResultImpl();
    result.putValue("out.person.age", new IntegerValueImpl(10));
    assertIvyScript(result, "in.person.age = 10;");
  }

  @Test
  public void mapDoubleProperty() {
    DmnDecisionRuleResultImpl result = new DmnDecisionRuleResultImpl();
    result.putValue("out.person.cost", new DoubleValueImpl(106.75));
    assertIvyScript(result, "in.person.cost = 106.75;");
  }

  @Test
  public void mapMultipleProperties() {
    DmnDecisionRuleResultImpl result = new DmnDecisionRuleResultImpl();
    result.putValue("out.person.name", new StringValueImpl("Peter Hochstrasser"));
    result.putValue("out.person.cost", new DoubleValueImpl(-684.7897));
    result.putValue("out.person.gender", new BooleanValueImpl(false));

    String ivyScript = OutputMappingScriptGenerator.create(result);
    assertThat(ivyScript)
            .contains("in.person.gender = false;")
            .contains("in.person.cost = -684.7897;")
            .contains("in.person.name = \"Peter Hochstrasser\";");
  }

  private void assertIvyScript(DmnDecisionRuleResultImpl result, String expectedIvyScript) {
    String ivyScript = OutputMappingScriptGenerator.create(result);
    assertEquals(ivyScript, expectedIvyScript);
  }
}
