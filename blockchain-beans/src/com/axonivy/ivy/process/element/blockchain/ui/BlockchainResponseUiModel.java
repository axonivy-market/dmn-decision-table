package com.axonivy.ivy.process.element.blockchain.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.util.StdConverter;

import ch.ivyteam.ivy.designer.process.ui.inscriptionMasks.error.IgnoreableExceptionConfigDisplayTextProvider;
import ch.ivyteam.ivy.designer.process.ui.inscriptionMasks.error.IgnoreableExceptionConfigListFactory;
import ch.ivyteam.ivy.designer.process.ui.inscriptionMasks.model.UiModel;
import ch.ivyteam.ivy.process.model.element.ThirdPartyElement;
import ch.ivyteam.ivy.process.model.element.value.IgnoreableExceptionConfig;
import ch.ivyteam.ivy.process.model.element.value.Mapping;
import ch.ivyteam.ivy.process.model.element.value.Mappings;
import ch.ivyteam.ivy.process.model.element.value.bean.UserConfig;
import ch.ivyteam.ivy.process.model.value.ErrorCode;
import ch.ivyteam.ivy.process.model.value.MappingCode;
import ch.ivyteam.ivy.resource.validation.restricted.IvyValidationMessage;
import ch.ivyteam.ivy.ui.model.UiMappingCodeModel;
import ch.ivyteam.ui.model.UiComboModel;
import ch.ivyteam.ui.model.validation.ValidationState;

public class BlockchainResponseUiModel extends UiModel<ThirdPartyElement, BlockchainRequestConfigurator>
{
  public final UiMappingCodeModel response;
  public final UiComboModel<IgnoreableExceptionConfig> webServiceError;

  public BlockchainResponseUiModel(BlockchainRequestConfigurator configurator, BlockchainRequestUiModel requestModel)
  {
    super(configurator);

    response = create().mappingCode(
              this::getOutput,
              this::setOutput,
              configurator.responseScriptModel)
            .withValidator(this::validateResponseMappingCode)
            .withDefaultValue(new MappingCode(new Mappings(new Mapping("out", "in"))))
            .dependsOnValueOf(requestModel.functions);

    webServiceError = create().combo(
              this::getBlockchainException,
              this::setBlockchainException,
              this::getBlockchainErrors)
            .withValidator(this::validateBlockchainError)
            .withDefaultValue(IgnoreableExceptionConfig.createDefault(ErrorCode.IVY_ERROR_PROGRAM_EXCEPTION))
            .withDisplayTextProvider(new IgnoreableExceptionConfigDisplayTextProvider(model));

    tab.addChild(response)
       .addChild(webServiceError);
  }

  public static class EthereumModel
  {
    public static final ObjectMapper mapper = new ObjectMapper();

    public static EthereumModel load(UserConfig config)
    {
      if (config.isEmpty())
      {
        return new EthereumModel();
      }
      try
      {
        return mapper.readerFor(EthereumModel.class).readValue(config.getRawValue());
      }
      catch (IOException ex)
      {
       return new EthereumModel();
      }
    }

    public static UserConfig store(EthereumModel model)
    {
      try
      {
        String json = mapper.writerFor(EthereumModel.class).writeValueAsString(model);
        return new UserConfig(json);
      }
      catch (JsonProcessingException ex)
      {
        return new UserConfig("");
      }
    }

    //@JsonSerialize(converter=MappingsToMap2.class)
    //@JsonSerialize(using = MappingsToMap.class, as=Map.class)
    public Map<String, String> properties;
  }

  public static class MappingsToMap2 extends StdConverter<Mappings, Map<String, String>>
  {

    @Override
    public Map<String, String> convert(Mappings mappings)
    {
      Map<String, String> map = new HashMap<>();
      mappings.asList().stream().forEach(mapping -> map.put(mapping.getLeftSide(), mapping.getRightSide()));
      return map;
    }

  }

  public static class MappingsToMap extends JsonSerializer<Map> {

    @Override
    public void serialize(Map tmpInt,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider)
                          throws IOException, JsonProcessingException {
        jsonGenerator.writeObject(tmpInt.toString());
    }
  }

  private MappingCode getOutput()
  {
//    return ((BlockchainClientCall) model).getOutput();
    return new MappingCode();
  }

  private void setOutput(MappingCode output)
  {
//    ((BlockchainClientCall) model).setOutput(output);
  }

  private IgnoreableExceptionConfig getBlockchainException()
  {
//    return ((BlockchainClientCall) model).getBlockchainException();
    return IgnoreableExceptionConfig.createDefault(ErrorCode.IVY_ERROR_PROGRAM_EXCEPTION);
  }

  private void setBlockchainException(IgnoreableExceptionConfig blockchainException)
  {
//    ((BlockchainClientCall) model).setBlockchainException(blockchainException);
  }

  private ValidationState validateResponseMappingCode()
  {
    return new ValidationState(new IvyValidationMessage(0, "bla"));
//    return modelValidation.validateWith(validator -> validator.validateResponseMappingCode());
  }

  private IgnoreableExceptionConfig[] getBlockchainErrors()
  {
    return IgnoreableExceptionConfigListFactory.withErrorCode(ErrorCode.IVY_ERROR_SCRIPT)
            .withIgnoreError()
            .withLegacyErrors(model)
            .toIgnoreableExceptionConfigs()
            .stream().toArray(IgnoreableExceptionConfig[]::new);
  }

  private ValidationState validateBlockchainError()
  {
    return new ValidationState(new IvyValidationMessage(0, "bla"));
    //return modelValidation.validateWith(validator -> validator.validateWebServiceError());
  }
}
