package com.axonivy.ivy.process.element.rule;

import java.util.Locale;

import ch.ivyteam.ivy.designer.process.ui.info.IProcessElementUiInformationExtension;

@SuppressWarnings("deprecation")
public class ProcessElementUiInformationExtension implements IProcessElementUiInformationExtension
{
  @Override
  public String getShortName(String processElementClassName, Locale locale)
  {
    switch (processElementClassName)
    {
      case ProcessElementExtension.DECISION_ACTIVITY:
        return "Decision";
      default:
        return null;
    }
  }

  @Override
  public String getName(String processElementClassName, Locale locale)
  {
    switch (processElementClassName)
    {
      case ProcessElementExtension.DECISION_ACTIVITY:
        return "Decision Table Activity";
      default:
        return null;
    }
  }

  @Override
  public String getDescription(String processElementClassName, Locale locale)
  {
    switch (processElementClassName)
    {
      case ProcessElementExtension.DECISION_ACTIVITY:
        return "Executes a DMN table";
      default:
        return null;
    }
  }

}
