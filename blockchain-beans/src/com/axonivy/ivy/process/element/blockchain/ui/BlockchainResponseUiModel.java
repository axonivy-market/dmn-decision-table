package com.axonivy.ivy.process.element.blockchain.ui;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import ch.ivyteam.ivy.datawrapper.scripting.IvyScriptInscriptionModel;
import ch.ivyteam.ivy.designer.process.ui.inscriptionMasks.model.UiModel;
import ch.ivyteam.ivy.process.config.activity.pi.ThirdPartyProgramInterfaceConfigurator;
import ch.ivyteam.ivy.process.model.element.activity.ThirdPartyProgramInterface;
import ch.ivyteam.ivy.process.model.element.value.Mapping;
import ch.ivyteam.ivy.process.model.element.value.Mappings;
import ch.ivyteam.ivy.process.model.value.MappingCode;
import ch.ivyteam.ivy.resource.validation.restricted.IvyValidationMessage;
import ch.ivyteam.ivy.scripting.types.IIvyClass;
import ch.ivyteam.ivy.scripting.util.Variable;
import ch.ivyteam.ivy.ui.model.UiMappingCodeModel;
import ch.ivyteam.ui.model.validation.ValidationState;

public class BlockchainResponseUiModel extends UiModel<ThirdPartyProgramInterface, ThirdPartyProgramInterfaceConfigurator>
{
  public final IvyScriptInscriptionModel responseScriptModel;
  public final UiMappingCodeModel response;

  public BlockchainResponseUiModel(ThirdPartyProgramInterfaceConfigurator configurator, BlockchainRequestUiModel requestModel)
  {
    super(configurator);

    responseScriptModel = IvyScriptInscriptionModel
            .create(configurator.project, configurator.processElement)
            .addDefaultOutputVariables()
            .additionalInputVariablesSuppliers(this::getResponseVariables)
            .toModel();
    response = create().mappingCode(
              this::getOutput,
              this::setOutput,
              responseScriptModel)
            .withValidator(this::validateResponseMappingCode)
            .withDefaultValue(new MappingCode(new Mappings(new Mapping("out", "in"))))
            .dependsOnValueOf(requestModel.functions);
    tab.addChild(response);
  }

  private MappingCode getOutput()
  {
    EthereumModel ethereum = getEthereumModel();
    Mappings mappings = BlockchainHelper.getMappings(ethereum.outputMappings);
    return new MappingCode(mappings, ethereum.outputCode);
  }

  private void setOutput(MappingCode output)
  {
    EthereumModel ethereum = getEthereumModel();
    ethereum.outputMappings = new HashMap<>();
    output.getMappings().forEach(mapping -> ethereum.outputMappings.put(mapping.getLeftSide(), mapping.getRightSide()));
    ethereum.outputCode = output.getCode();
    setEthereumModel(ethereum);
  }

  private List<Variable> getResponseVariables()
  {
    EthereumModel ethereum = getEthereumModel();
    Method chosenMethod = BlockchainHelper.loadMethod(configurator.project, ethereum.contract, ethereum.function);
    if (chosenMethod == null)
    {
      return Collections.emptyList();
    }
    List<Variable> parameterVariables = new ArrayList<>();
    Parameter[] methodParams = chosenMethod.getParameters();
    for (Parameter parameter : methodParams)
    {
      IIvyClass<?> ivyClass = configurator.project.getIvyScriptClassRepository().getIvyClassForName(parameter.getType().getName());
      parameterVariables.add(new Variable(parameter.getName(), ivyClass));
    }

    return parameterVariables;
  }

  private ValidationState validateResponseMappingCode()
  {
    return new ValidationState(new IvyValidationMessage(0, ""));
//    return modelValidation.validateWith(validator -> validator.validateResponseMappingCode());
  }

  private EthereumModel getEthereumModel()
  {
    return EthereumModel.load(model.getUserConfig());
  }

  private void setEthereumModel(EthereumModel etherum)
  {
    model.setUserConfig(EthereumModel.store(etherum));
  }
}
