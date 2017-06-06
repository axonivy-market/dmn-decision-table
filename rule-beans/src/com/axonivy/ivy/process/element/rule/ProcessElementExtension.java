package com.axonivy.ivy.process.element.rule;

import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import ch.ivyteam.ivy.process.element.IBpmnIconFactory;
import ch.ivyteam.ivy.process.element.IExtensibleProcessElementFactory;
import ch.ivyteam.ivy.process.element.IExtensibleStandardProcessElementExtension;

public class ProcessElementExtension implements IExtensibleStandardProcessElementExtension
{
  static final String RULE_ACTIVITY = "RuleActivity";

  @Override
  public String getName()
  {
    return "Decision Table";
  }

  @Override
  public void declareProcessElements(IExtensibleProcessElementFactory factory)
  {
    factory.declareProgramInterfaceProcessElement(RULE_ACTIVITY, RuleActivity.class.getName(), RULE_ACTIVITY);
  }

  @Override
  public Icon createBpmnIcon(IBpmnIconFactory iconFactory, String iconName)
  {
    if (RULE_ACTIVITY.equals(iconName))
    {
      URL iconUrl = getClass().getResource("RuleActivity.png");
      if (iconUrl != null)
      {     
        return iconFactory.createActivityBpmnIcon(new ImageIcon(iconUrl), false);
      }
    }
    return null;
  }

}
