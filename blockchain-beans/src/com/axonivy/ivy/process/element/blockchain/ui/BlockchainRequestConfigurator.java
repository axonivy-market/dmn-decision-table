package com.axonivy.ivy.process.element.blockchain.ui;

import java.util.List;

import ch.ivyteam.ivy.process.config.element.AbstractProcessElementConfigurator;
import ch.ivyteam.ivy.process.model.element.ThirdPartyElement;
import ch.ivyteam.ivy.project.IIvyProject;
import ch.ivyteam.ivy.scripting.util.Variable;

public class BlockchainRequestConfigurator extends AbstractProcessElementConfigurator<ThirdPartyElement>
{

  public BlockchainRequestConfigurator(IIvyProject project, ThirdPartyElement processElement)
  {
    super(project, processElement);
    // TODO Auto-generated constructor stub
  }

  @Override
  public List<Variable> findVariablesForAspect(String aspect)
  {
    // TODO Auto-generated method stub
    return null;
  }

}
