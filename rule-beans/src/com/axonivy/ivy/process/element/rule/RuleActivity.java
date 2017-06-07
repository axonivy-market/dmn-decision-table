package com.axonivy.ivy.process.element.rule;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.axonivy.ivy.process.element.rule.ui.RuleConfigEditor;

import ch.ivyteam.awt.swt.SwtRunnable;
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
  private static final String INPUT_DATA = "INPUT_DATA";
  
  @SuppressWarnings("restriction")
  @Override
  public CompositeObject perform(IRequestId requestId, CompositeObject in, IIvyScriptContext context) throws Exception
  {
    executeIvyScript(context, createIvyScript());
//    Rules.getInstance().engine.createRuleBase()
//    RuleEngine ruleEngine = new RuleEngine();
//    IRuleBase ruleBase = ruleEngine.createRuleBase();
//    ruleBase.loadRulesFromNamespace(getConfigurationProperty(RULE_NAMESPACE));
//    String property = getConfigurationProperty(INPUT_DATA);
//    ruleBase.createSession().execute(in.get(property));
    return in;
  }
  
  private String createIvyScript() throws IOException
  {
    
    String namespace = getConfigurationProperty(RULE_NAMESPACE);
    String outData = getConfigurationProperty(INPUT_DATA);
    StringBuilder script = new StringBuilder();
    script.append("IRuleBase ruleBase = ivy.rules.engine.createRuleBase();");
    script.append("\n");
    script.append("ruleBase.loadRulesFromNamespace(\"" + namespace + "\");");
    script.append("\n");
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
        ruleConfigEditor = new RuleConfigEditor(parent, SWT.NONE);
        GridLayout gridLayout = (GridLayout) ruleConfigEditor.getLayout();
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
<<<<<<< HEAD
        ruleConfigEditor.setRuleNamespace(ruleNamespace);
        ruleConfigEditor.setInputData(inputData);
        ruleConfigEditor.setDataVariables(env.getDataInputVariables());
=======
        decisionEditor.table.setModel(model);
        decisionEditor.setDataVariables(env.getDataInputVariables());
        decisionEditor.setScriptEngine(env.getIvyProject().getIvyScriptEngine());
        decisionEditor.tabs.setSelection(0); // select table mode
>>>>>>> branch 'master' of https://github.com/ivy-supplements/bpm-beans
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
            inputData = getBeanConfigurationProperty(INPUT_DATA);
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
            setBeanConfigurationProperty(RULE_NAMESPACE, ruleConfigEditor.getRuleNamespace());
            setBeanConfigurationProperty(INPUT_DATA, ruleConfigEditor.getInputData());
          }
        }.syncExec();
      return true;
    }
  }
}
