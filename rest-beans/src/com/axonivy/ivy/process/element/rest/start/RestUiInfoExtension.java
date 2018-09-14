package com.axonivy.ivy.process.element.rest.start;

import java.util.Locale;

import ch.ivyteam.ivy.designer.process.ui.info.IProcessElementUiInformationExtension;

public class RestUiInfoExtension implements IProcessElementUiInformationExtension
{
  @Override
  public String getShortName(String processElementClassName, Locale locale)
  {
    if (RestStartExtension.REST_START.equals(processElementClassName))
    {
        return "RestStart";
    }
    return null;
  }

  @Override
  public String getName(String processElementClassName, Locale locale)
  {
    if (RestStartExtension.REST_START.equals(processElementClassName))
    {
        return "Rest Start Element";
    }
    return null;
  }

  @Override
  public String getDescription(String processElementClassName, Locale locale)
  {
    if (RestStartExtension.REST_START.equals(processElementClassName))
    {
        return "Start a Rest Service";
    }
    return null;
  }
}
