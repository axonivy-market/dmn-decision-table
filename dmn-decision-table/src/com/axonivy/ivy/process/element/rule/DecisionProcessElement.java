package com.axonivy.ivy.process.element.rule;

import ch.ivyteam.ivy.bpm.exec.IBpmnProcessElement;
import ch.ivyteam.ivy.process.extension.IUserProcessExtension;

public class DecisionProcessElement implements IBpmnProcessElement
{
  public static final String DECISION_ACTIVITY = "DecisionActivity";

  @Override
  public String getName() {
    return DECISION_ACTIVITY;
  }

  @Override
  public Class<? extends IUserProcessExtension> getExecutor() {
    return DecisionActivity.class;
  }

  @Override
  public String getLabel() {
    return "Decision Table Activity";
  }

  @Override
  public String getShortLabel() {
    return "Decision";
  }

  @Override
  public String getDescription() {
    return "Executes a DMN table";
  }

  @Override
  public String getIconId() {
    return "std:Rule";
  }

  @Override
  public ElementKind getKind() {
    return ElementKind.ACTIVITY;
  }

}
