package com.axonivy.ivy.process.element.rule;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.axonivy.ivy.process.element.rule.resource.RuleResolver;
import com.axonivy.ivy.process.element.rule.ui.RuleConfigEditor;

import ch.ivyteam.awt.swt.SwtRunnable;
import ch.ivyteam.ivy.bpm.exec.restricted.acl.scripting.IvyScriptCodeBuilder;
import ch.ivyteam.ivy.designer.process.ui.inscriptionMasks.tabs.helper.ProcessExtensionConfigurationEditorEnvironment;
import ch.ivyteam.ivy.process.engine.IRequestId;
import ch.ivyteam.ivy.process.extension.IProcessExtensionConfigurationEditorEnvironment;
import ch.ivyteam.ivy.process.extension.impl.AbstractProcessExtensionConfigurationEditor;
import ch.ivyteam.ivy.process.extension.impl.AbstractUserProcessExtension;
import ch.ivyteam.ivy.scripting.language.IIvyScriptContext;
import ch.ivyteam.ivy.scripting.objects.CompositeObject;

public class RuleActivity extends AbstractUserProcessExtension 
{
  private static final String RULE_NAMESPACE = "RULE_NAMESPACE";
  private static final String INPUT_DATA_MAPPING = "INPUT_DATA_MAPPING";

  @Override
  public CompositeObject perform(IRequestId requestId, CompositeObject in, IIvyScriptContext context)
          throws Exception
  {
    executeIvyScript(context, createIvyScript());
    return in;
  }

  private String createIvyScript() throws IOException
  {
    String namespace = getConfigurationProperty(RULE_NAMESPACE);
    String outData = getConfigurationProperty(INPUT_DATA_MAPPING);
    
    IvyScriptCodeBuilder script = new IvyScriptCodeBuilder();
    script.append("IRuleBase ruleBase = ivy.rules.engine.createRuleBase();");
    script.append("ruleBase.loadRulesFromNamespace(\"" + namespace + "\");");
    script.append("ruleBase.createSession().execute(" + outData + ");");    
    return script.toString();
  }

  public static class Editor extends AbstractProcessExtensionConfigurationEditor
  {    
    private ProcessExtensionConfigurationEditorEnvironment env;
    
    private String ruleNamespace;
    private String inputData;
    
    private RuleConfigEditor ruleConfigEditor;

    @Override
    public void setEnvironment(IProcessExtensionConfigurationEditorEnvironment environment)
    {
      this.env = (ProcessExtensionConfigurationEditorEnvironment) environment;
    }
    
    @Override
    public Composite getComposite(Composite parent)
    {
      if (ruleConfigEditor == null)
      {
        List<String> availableRuleNamespaces = new RuleResolver(env.getIvyProject()).findAvailableRulenamespaces();
        ruleConfigEditor = new RuleConfigEditor(parent, SWT.NONE);
        GridLayout gridLayout = (GridLayout) ruleConfigEditor.getLayout();
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        ruleConfigEditor.setAvailableRuleNamespace(availableRuleNamespaces);
        ruleConfigEditor.setRuleNamespace(ruleNamespace);
        ruleConfigEditor.setInputData(inputData);
        ruleConfigEditor.setDataVariables(env.getDataInputVariables());
      }
      return ruleConfigEditor;
    }

    @Override
    protected void loadUiDataFromConfiguration()
    {
      new SwtRunnable()
        {
          @Override
          public void run()
          {
            ruleNamespace = getBeanConfigurationProperty(RULE_NAMESPACE);
            inputData = getBeanConfigurationProperty(INPUT_DATA_MAPPING);
          }
        }.syncExec();
    }

    @Override
    protected boolean saveUiDataToConfiguration()
    {
      clearBeanConfiguration();
      new SwtRunnable()
        {
          @Override
          public void run()
          {
            setBeanConfigurationProperty(RULE_NAMESPACE, StringUtils.trimToEmpty(ruleConfigEditor.getRuleNamespace()));
            setBeanConfigurationProperty(INPUT_DATA_MAPPING, StringUtils.trimToEmpty(ruleConfigEditor.getInputData()));
          }
        }.syncExec();
      return true;
    }
  }
}
