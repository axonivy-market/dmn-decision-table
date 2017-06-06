package com.axonivy.ivy.process.element.rule;

import java.util.Locale;

import ch.ivyteam.ivy.designer.process.ui.info.IProcessElementUiInformationExtension;

public class ProcessElementUiInformationExtension implements IProcessElementUiInformationExtension
{
  @Override
  public String getShortName(String processElementClassName, Locale locale)
  {
    if (ProcessElementExtension.RULE_ACTIVITY.equals(processElementClassName))
    {
      return "Decision";
    }
    return null;
  }

  @Override
  public String getName(String processElementClassName, Locale locale)
  {
    if (ProcessElementExtension.RULE_ACTIVITY.equals(processElementClassName))
    {
      return "Decision Table Activity";
    }
    return null;
  }

  @Override
  public String getDescription(String processElementClassName, Locale locale)
  {
    if (ProcessElementExtension.RULE_ACTIVITY.equals(processElementClassName))
    {
      return "Executes a DMN table";
    }
    return null;
  }

}
