package com.axonivy.ivy.process.element.rule.dmn;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;

import ch.ivyteam.ivy.scripting.objects.CompositeObject;

public class DmnExecutor {

  private InputStream dmnInputStream;
  private VariableMap variables;

  public DmnExecutor(InputStream dmnInputStream, VariableMap variables) {
    this.dmnInputStream = dmnInputStream;
    this.variables = variables;
  }

  public DmnExecutor(InputStream dmnInputStream, CompositeObject in) {
    this(dmnInputStream, Variables.putValue("in", in));
  }

  public Map<String, Object> execute() {
    DmnEngine dmnEngine = DmnEngineConfiguration.createDefaultDmnEngineConfiguration().buildEngine();
    try {
      DmnDecision decision = dmnEngine.parseDecision("decision", dmnInputStream);
      DmnDecisionTableResult result = dmnEngine.evaluateDecisionTable(decision, variables);
      if (result.getFirstResult() == null) {
        return Collections.emptyMap();
      }
      return result.getFirstResult().getEntryMap();
    } finally {
      try {
        dmnInputStream.close();
      } catch (IOException e) {
        // close silently
      }
    }
  }
}
