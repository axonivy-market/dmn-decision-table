package com.axonivy.ivy.process.element.rule;

import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import ch.ivyteam.ivy.process.element.IBpmnIconFactory;
import ch.ivyteam.ivy.process.element.IExtensibleProcessElementFactory;
import ch.ivyteam.ivy.process.element.IExtensibleStandardProcessElementExtension;

@SuppressWarnings("deprecation")
public class ProcessElementExtension implements IExtensibleStandardProcessElementExtension
{
  static final String DECISION_ACTIVITY = "DecisionActivity";

  @Override
  public String getName()
  {
    return "Decision Table";
  }

  @Override
  public void declareProcessElements(IExtensibleProcessElementFactory factory)
  {
    factory.declareProgramInterfaceProcessElement(DECISION_ACTIVITY, DecisionActivity.class.getName(),
            DECISION_ACTIVITY);
  }

  @Override
  public Icon createBpmnIcon(IBpmnIconFactory iconFactory, String iconName)
  {
    String img = null;
    if (DECISION_ACTIVITY.equals(iconName))
    {
      img = "DecisionActivity.png";
    }

    if (img != null)
    {
      URL iconUrl = getClass().getResource(img);
      if (iconUrl != null)
      {
        return iconFactory.createActivityBpmnIcon(new ImageIcon(iconUrl), false);
      }
    }
    return null;
  }

}
