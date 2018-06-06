package com.axonivy.ivy.process.element.blockchain;

import java.util.Locale;

import ch.ivyteam.ivy.designer.process.ui.info.IProcessElementUiInformationExtension;

public class ProcessElementUiInformationExtension implements IProcessElementUiInformationExtension
{
  @Override
  public String getShortName(String processElementClassName, Locale locale)
  {
    switch (processElementClassName)
    {
      case ProcessElementExtension.ETHEREUM_ACTIVITY:
        return "Ethereum";
      default:
        return null;
    }
  }

  @Override
  public String getName(String processElementClassName, Locale locale)
  {
    switch (processElementClassName)
    {
      case ProcessElementExtension.ETHEREUM_ACTIVITY:
        return "Ethereum Activity";
      default:
        return null;
    }
  }

  @Override
  public String getDescription(String processElementClassName, Locale locale)
  {
    switch (processElementClassName)
    {
      case ProcessElementExtension.ETHEREUM_ACTIVITY:
        return "Executes an Ethereum call";
      default:
        return null;
    }
  }
}
