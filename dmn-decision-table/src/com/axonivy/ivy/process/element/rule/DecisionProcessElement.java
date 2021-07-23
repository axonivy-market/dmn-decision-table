package com.axonivy.ivy.process.element.rule;

import ch.ivyteam.ivy.bpm.exec.IBpmnProcessElement;
import ch.ivyteam.ivy.process.extension.IUserProcessExtension;

public class DecisionProcessElement implements IBpmnProcessElement
{
  public static final String DECISION_ACTIVITY = "DecisionActivity";

  @Override
  public String getName()
  {
    return DECISION_ACTIVITY;
  }

  @Override
  public Class<? extends IUserProcessExtension> getExecutor()
  {
    return DecisionActivity.class;
  }
}
