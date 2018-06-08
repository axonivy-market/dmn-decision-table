package com.axonivy.ivy.process.element.blockchain.ui;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.ivyteam.ivy.components.more.Aspects;
import ch.ivyteam.ivy.datawrapper.scripting.IvyScriptInscriptionModel;
import ch.ivyteam.ivy.process.config.element.AbstractProcessElementConfigurator;
import ch.ivyteam.ivy.process.model.element.ThirdPartyElement;
import ch.ivyteam.ivy.project.IIvyProject;
import ch.ivyteam.ivy.scripting.util.Variable;

public class BlockchainRequestConfigurator extends AbstractProcessElementConfigurator<ThirdPartyElement>
{
  private static final List<String> RESPONSE_MODEL_ASPECTS = Arrays.asList(
          Aspects.ACTION_CODE,
          Aspects.ACTION_TABLE);

  public final IvyScriptInscriptionModel propertiesMappingScriptModel;
  public final IvyScriptInscriptionModel parameterMappingScriptModel;
  public final IvyScriptInscriptionModel responseScriptModel;

  public BlockchainRequestConfigurator(IIvyProject project, ThirdPartyElement processElement)
  {
    super(project, processElement);

    propertiesMappingScriptModel = IvyScriptInscriptionModel
            .create(project, processElement)
            .toModel();

    parameterMappingScriptModel = IvyScriptInscriptionModel
            .create(project, processElement)
            .outputVariablesSupplier(this::getParameterVariables)
            .toModel();

    responseScriptModel = IvyScriptInscriptionModel
            .create(project, processElement)
            .addDefaultOutputVariables()
            .additionalInputVariablesSupplier(this::getResponseVariables)
            .toModel();
  }

  @Override
  public List<Variable> findVariablesForAspect(String aspect)
  {
    if ("properties".intern().equals(aspect))
    {
      return propertiesMappingScriptModel.getVariables();
    }
    if ("inputParams".intern().equals(aspect))
    {
      return parameterMappingScriptModel.getVariables();
    }
    if (RESPONSE_MODEL_ASPECTS.contains(aspect))
    {
      return responseScriptModel.getVariables();
    }
    return null;
  }

  public List<Variable> getParameterVariables()
  {
    return Collections.emptyList();
  }

  private List<Variable> getResponseVariables()
  {
    return Collections.emptyList();
  }
}
