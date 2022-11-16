package com.axonivy.ivy.process.element.rule.ui;

import java.net.URL;
import java.util.Locale;

import com.axonivy.ivy.process.element.rule.DecisionProcessElement;

import ch.ivyteam.ivy.designer.process.ui.info.IBpmnProcessElementUi;

public class DecisionProcessElementUi implements IBpmnProcessElementUi
{
  @Override
  public String getName()
  {
    return DecisionProcessElement.DECISION_ACTIVITY;
  }

  @Override
  public String getShortName(Locale locale)
  {
    return "Decision";
  }

  @Override
  public String getName(Locale locale)
  {
    return "Decision Table Activity";
  }

  @Override
  public String getDescription(Locale locale)
  {
    return "Executes a DMN table";
  }

  @Override
  public URL getIcon()
  {
    return getClass().getResource("DecisionActivity.png");
  }

  @Override
  public boolean showDefaultTabs()
  {
    return true;
  }

  @Override
  public String getHelpPath()
  {
    return "https://market.axonivy.com/dmn-decision-table";
  }
}
