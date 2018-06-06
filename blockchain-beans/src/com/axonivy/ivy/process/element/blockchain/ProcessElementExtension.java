package com.axonivy.ivy.process.element.blockchain;

import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import ch.ivyteam.ivy.process.element.IBpmnIconFactory;
import ch.ivyteam.ivy.process.element.IExtensibleProcessElementFactory;
import ch.ivyteam.ivy.process.element.IExtensibleStandardProcessElementExtension;

public class ProcessElementExtension implements IExtensibleStandardProcessElementExtension
{
  public static final String ETHEREUM_ACTIVITY = "EthereumActivity";

  @Override
  public String getName()
  {
    return "Blockchain";
  }

  @Override
  public void declareProcessElements(IExtensibleProcessElementFactory factory)
  {
    factory.declareProgramInterfaceProcessElement(ETHEREUM_ACTIVITY, EthereumActivity.class.getName(), ETHEREUM_ACTIVITY);
  }

  @Override
  public Icon createBpmnIcon(IBpmnIconFactory iconFactory, String iconName)
  {
    if (ETHEREUM_ACTIVITY.equals(iconName))
    {
      URL iconUrl = getClass().getResource("EthereumActivity.png");
      if (iconUrl != null)
      {
        return iconFactory.createActivityBpmnIcon(new ImageIcon(iconUrl), false);
      }
    }
    return null;
  }
}
