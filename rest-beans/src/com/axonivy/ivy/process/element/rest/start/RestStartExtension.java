package com.axonivy.ivy.process.element.rest.start;

import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import ch.ivyteam.ivy.process.element.IBpmnIconFactory;
import ch.ivyteam.ivy.process.element.IExtensibleProcessElementFactory;
import ch.ivyteam.ivy.process.element.IExtensibleStandardProcessElementExtension;

public class RestStartExtension implements IExtensibleStandardProcessElementExtension
{
  public static final String REST_START = "RestStart";

  @Override
  public String getName()
  {
    return "RestStart";
  }

  @Override
  public void declareProcessElements(IExtensibleProcessElementFactory factory)
  {
    factory.declareStartEventProcessElement(REST_START, RestStartElement.class.getName(), REST_START);
  }

  @Override
  public Icon createBpmnIcon(IBpmnIconFactory iconFactory, String iconName)
  {
    if (REST_START.equals(iconName))
    {
      URL iconUrl = getClass().getResource("RestStartService.png");
      if (iconUrl != null)
      {
        return iconFactory.createStartEventBpmnIcon(new ImageIcon(iconUrl));
      }
    }
    return null;
  }
}
