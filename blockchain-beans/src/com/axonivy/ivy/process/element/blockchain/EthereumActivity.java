package com.axonivy.ivy.process.element.blockchain;

import java.util.HashMap;
import java.util.Map;

import ch.ivyteam.ivy.process.engine.IRequestId;
import ch.ivyteam.ivy.process.extension.impl.AbstractUserProcessExtension;
import ch.ivyteam.ivy.scripting.exceptions.IvyScriptException;
import ch.ivyteam.ivy.scripting.language.IIvyScriptContext;
import ch.ivyteam.ivy.scripting.objects.CompositeObject;
import ch.ivyteam.log.Logger;

public class EthereumActivity extends AbstractUserProcessExtension
{
  private static final Logger LOGGER = Logger.getClassLogger(EthereumActivity.class);

  @Override
  public CompositeObject perform(IRequestId requestId, CompositeObject in, IIvyScriptContext context) throws Exception
  {
    String configuration = getConfiguration();
    //RulesModel model = RulesModelSerialization.deserialize(ruleModelJson);
    Map<String, Object> result = new HashMap<>();
    if (!result .isEmpty())
    {
      mapResultToOutput(context, result);
    }
    return in;
  }

  private void mapResultToOutput(IIvyScriptContext context, Map<String, Object> result) throws IvyScriptException
  {
//    String ivyScript = OutputMappingScriptGenerator.create(result);
//    executeIvyScript(context, ivyScript);
  }

//  public static class Editor extends AbstractProcessExtensionConfigurationEditor
//  {
//    private ProcessExtensionConfigurationEditorEnvironment env;
//    private DecisionTableEditor decisionEditor;
//    private RulesModel model;
//
//    @Override
//    public void setEnvironment(IProcessExtensionConfigurationEditorEnvironment environment)
//    {
//      this.env = (ProcessExtensionConfigurationEditorEnvironment) environment;
//    }
//
//    @Override
//    public Composite getComposite(Composite parent)
//    {
//      if (decisionEditor == null)
//      {
//        decisionEditor = new DecisionTableEditor(parent, SWT.NONE);
//        GridLayout gridLayout = (GridLayout) decisionEditor.getLayout();
//        gridLayout.marginHeight = 0; // margin already provided by tab
//        gridLayout.marginWidth = 0;
//        decisionEditor.table.setModel(model);
//        decisionEditor.setDataVariables(env.getDataInputVariables());
//        decisionEditor.setProject(env.getIvyProject());
//        decisionEditor.tabs.setSelection(0); // select table mode
//      }
//      return decisionEditor;
//    }
//
//    @Override
//    protected void loadUiDataFromConfiguration()
//    {
//      try
//      {
//        String ruleModelJson = getBeanConfiguration();
//        model = RulesModelSerialization.deserialize(ruleModelJson);
//      }
//      catch (Exception ex)
//      {
//        throw new IllegalStateException("Could deserialize rules model", ex);
//      }
//    }
//
//    @Override
//    protected boolean saveUiDataToConfiguration()
//    {
//      try
//      {
//        RulesModel rulesModel = decisionEditor.table.getModel();
//        String ruleModelJson = RulesModelSerialization.serialize(rulesModel);
//        setBeanConfiguration(ruleModelJson);
//      }
//      catch (JsonProcessingException ex)
//      {
//        LOGGER.error(ex);
//        return false;
//      }
//      return true;
//    }
//  }
}
