package com.axonivy.ivy.process.element.rule;

import java.io.InputStream;
import java.util.Map;

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

import ch.ivyteam.ivy.designer.inscription.ui.masks.fw.ProcessExtensionConfigurationEditorEnvironment;
import ch.ivyteam.ivy.process.config.activity.pi.bean.JavaBeanConfigurator;
import ch.ivyteam.ivy.process.engine.IRequestId;
import ch.ivyteam.ivy.process.extension.ICommonProcessExtensionConfigurationEditor;
import ch.ivyteam.ivy.process.extension.IProcessExtensionConfigurationEditorEnvironment;
import ch.ivyteam.ivy.process.extension.impl.AbstractProcessExtensionConfigurationEditor;
import ch.ivyteam.ivy.process.extension.impl.AbstractUserProcessExtension;
import ch.ivyteam.ivy.scripting.exceptions.IvyScriptException;
import ch.ivyteam.ivy.scripting.language.IIvyScriptContext;
import ch.ivyteam.ivy.scripting.objects.CompositeObject;
import ch.ivyteam.ivy.scripting.types.IVariable;
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
    Map<String, Object> result = new DmnExecutor(dmnInputStream, in).execute();
    if (!result.isEmpty())
    {
      mapResultToOutput(context, result);
    }
    return in;
  }

  private void mapResultToOutput(IIvyScriptContext context, Map<String, Object> result) throws IvyScriptException
  {
    String ivyScript = OutputMappingScriptGenerator.create(result);
    executeIvyScript(context, ivyScript);
  }

  public static class Editor extends AbstractProcessExtensionConfigurationEditor implements ICommonProcessExtensionConfigurationEditor
  {
    private IProcessExtensionConfigurationEditorEnvironment env;
    private DecisionTableEditor decisionEditor;
    private RulesModel model;

    @Override
    public void setEnvironment(IProcessExtensionConfigurationEditorEnvironment environment)
    {
      this.env = environment;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getComposite(T parent) {
      if (decisionEditor == null)
      {
        if (parent instanceof Composite wrapper) {
          decisionEditor = new DecisionTableEditor(wrapper, SWT.NONE);
          GridLayout gridLayout = (GridLayout) decisionEditor.getLayout();
          gridLayout.marginHeight = 0; // margin already provided by tab
          gridLayout.marginWidth = 0;
          decisionEditor.table.setModel(model);
          JavaBeanConfigurator configurator = getConfigurator();
          decisionEditor.setDataVariables(configurator.editorScriptModel.getInputVariables().stream().toArray(IVariable[]::new));
          decisionEditor.setProject(configurator.project);
          decisionEditor.tabs.setSelection(0); // select table mode
        }
      }
      return (T) decisionEditor;
    }

    private JavaBeanConfigurator getConfigurator(){
      try {
        return (JavaBeanConfigurator) ProcessExtensionConfigurationEditorEnvironment.class.getDeclaredField("configurator").get(env);
      } catch (Exception ex) {
        throw new RuntimeException("Failed to resolve configurator ",ex);
      }
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
      if (decisionEditor != null)
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
      }
      return true;
    }
  }
}
