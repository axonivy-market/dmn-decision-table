package com.axonivy.ivy.process.element.rule;

import java.io.InputStream;
import java.util.Map;

import com.axonivy.ivy.process.element.rule.dmn.DmnExecutor;
import com.axonivy.ivy.process.element.rule.dmn.DmnSerializer;
import com.axonivy.ivy.process.element.rule.dmn.OutputMappingScriptGenerator;
import com.axonivy.ivy.process.element.rule.model.RulesModel;
import com.axonivy.ivy.process.element.rule.model.RulesModelSerialization;

import ch.ivyteam.ivy.process.engine.IRequestId;
import ch.ivyteam.ivy.process.extension.impl.AbstractUserProcessExtension;
import ch.ivyteam.ivy.scripting.exceptions.IvyScriptException;
import ch.ivyteam.ivy.scripting.language.IIvyScriptContext;
import ch.ivyteam.ivy.scripting.objects.CompositeObject;

public class DecisionActivity extends AbstractUserProcessExtension {

  @Override
  public CompositeObject perform(IRequestId requestId, CompositeObject in, IIvyScriptContext context)  throws Exception {
    String ruleModelJson = getConfiguration();
    RulesModel model = RulesModelSerialization.deserialize(ruleModelJson);
    InputStream dmnInputStream = new DmnSerializer(model).serialize();
    Map<String, Object> result = new DmnExecutor(dmnInputStream, in).execute();
    if (!result.isEmpty()) {
      mapResultToOutput(context, result);
    }
    return in;
  }

  private void mapResultToOutput(IIvyScriptContext context, Map<String, Object> result) throws IvyScriptException {
    String ivyScript = OutputMappingScriptGenerator.create(result);
    executeIvyScript(context, ivyScript);
  }

}
