package com.axonivy.ivy.process.element.rule;

import java.io.InputStream;
import java.util.Optional;

import org.camunda.bpm.dmn.engine.DmnDecisionRuleResult;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.axonivy.ivy.process.element.rule.dmn.DmnExecutor;
import com.axonivy.ivy.process.element.rule.dmn.DmnSerializer;
import com.axonivy.ivy.process.element.rule.dmn.OutputMappingScriptGenerator;
import com.axonivy.ivy.process.element.rule.model.RulesModel;
import com.axonivy.ivy.process.element.rule.model.RulesModelSerialization;
import com.axonivy.ivy.process.element.rule.ui.DecisionTableEditor;
import com.fasterxml.jackson.core.JsonProcessingException;

import ch.ivyteam.ivy.designer.process.ui.inscriptionMasks.tabs.helper.ProcessExtensionConfigurationEditorEnvironment;
import ch.ivyteam.ivy.process.engine.IRequestId;
import ch.ivyteam.ivy.process.extension.IProcessExtensionConfigurationEditorEnvironment;
import ch.ivyteam.ivy.process.extension.impl.AbstractProcessExtensionConfigurationEditor;
import ch.ivyteam.ivy.process.extension.impl.AbstractUserProcessExtension;
import ch.ivyteam.ivy.scripting.exceptions.IvyScriptException;
import ch.ivyteam.ivy.scripting.language.IIvyScriptContext;
import ch.ivyteam.ivy.scripting.objects.CompositeObject;
import ch.ivyteam.log.Logger;

public class DecisionActivity extends AbstractUserProcessExtension 
{
  private static final Logger LOGGER = Logger.getClassLogger(DecisionActivity.class);

  @Override
  public CompositeObject perform(IRequestId requestId, CompositeObject in, IIvyScriptContext context) throws Exception
  {
    String ruleModelJson = getConfiguration();
    RulesModel model = RulesModelSerialization.deserialize(ruleModelJson);
    InputStream dmnInputStream = new DmnSerializer(model).serialize();
    Optional<DmnDecisionRuleResult> result = new DmnExecutor(dmnInputStream, in).execute();
    if (result.isPresent()) {
      mapResultToOutput(context, result.get());
    }
    return in;
  }

  private void mapResultToOutput(IIvyScriptContext context, DmnDecisionRuleResult result) throws IvyScriptException
  {
    String ivyScript = OutputMappingScriptGenerator.create(result);
    executeIvyScript(context, ivyScript);
  }

  public static class Editor extends AbstractProcessExtensionConfigurationEditor
  {    
    private ProcessExtensionConfigurationEditorEnvironment env;
    private DecisionTableEditor decisionEditor;
    private RulesModel model;

    @Override
    public void setEnvironment(IProcessExtensionConfigurationEditorEnvironment environment)
    {
      this.env = (ProcessExtensionConfigurationEditorEnvironment) environment;
    }
    
    @Override
    public Composite getComposite(Composite parent)
    {
      if (decisionEditor == null)
      {
        decisionEditor = new DecisionTableEditor(parent, SWT.NONE);
        GridLayout gridLayout = (GridLayout) decisionEditor.getLayout();
        gridLayout.marginHeight = 0; // margin already provided by tab
        gridLayout.marginWidth = 0;
        decisionEditor.table.setModel(model);
        decisionEditor.setDataVariables(env.getDataInputVariables());
        decisionEditor.setScriptEngine(env.getIvyProject().getIvyScriptEngine());
        decisionEditor.tabs.setSelection(0); // select table mode
      }
      return decisionEditor;
    }

    @Override
    protected void loadUiDataFromConfiguration()
    {
      try
      {
        String ruleModelJson = getBeanConfiguration();
        model = RulesModelSerialization.deserialize(ruleModelJson);
      }
      catch (Exception ex)
      {
        throw new IllegalStateException("Could deserialize rules model", ex);
      }
    }

    @Override
    protected boolean saveUiDataToConfiguration()
    {
      try
      {
        RulesModel rulesModel = decisionEditor.table.getModel();
        String ruleModelJson = RulesModelSerialization.serialize(rulesModel);
        setBeanConfiguration(ruleModelJson);
      }
      catch (JsonProcessingException ex)
      {
        LOGGER.error(ex);
        return false;
      }
      return true;
    }
  }
}
