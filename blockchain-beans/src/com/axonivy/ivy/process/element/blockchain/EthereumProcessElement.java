package com.axonivy.ivy.process.element.blockchain;

import ch.ivyteam.ivy.bpm.exec.IBpmnProcessElement;
import ch.ivyteam.ivy.process.extension.IUserProcessExtension;

public class EthereumProcessElement implements IBpmnProcessElement
{
  public static final String ETHEREUM_ACTIVITY = "EthereumActivity";

  @Override
  public String getName()
  {
    return ETHEREUM_ACTIVITY;
  }

  @Override
  public Class<? extends IUserProcessExtension> getExecutor()
  {
    return EthereumActivity.class;
  }
}
