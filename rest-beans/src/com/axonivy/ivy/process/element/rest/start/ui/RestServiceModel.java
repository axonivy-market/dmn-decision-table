package com.axonivy.ivy.process.element.rest.start.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.ivyteam.ivy.process.model.element.value.bean.UserConfig;

public class RestServiceModel
{
  public static final ObjectMapper mapper = new ObjectMapper();

  public Map<String, String> inputParams = new HashMap<String, String>();
  public Map<String, String> inputToDataMappings = new HashMap<String, String>();
  public Map<String, String> outputParams = new HashMap<String, String>();
  public Map<String, String> dataToOutputMappings = new HashMap<String, String>();
  public String signature = "";
  public String outputCode = "";

  public static RestServiceModel load(UserConfig config)
  {
    return load(config.getRawValue());
  }

  public static RestServiceModel load(String configuration)
  {
    if (StringUtils.isEmpty(configuration))
    {
      return new RestServiceModel();
    }
    try
    {
      return mapper.readerFor(RestServiceModel.class).readValue(configuration);
    }
    catch (IOException ex)
    {
     return new RestServiceModel();
    }
  }

  public static UserConfig store(RestServiceModel model)
  {
    try
    {
      String json = mapper.writerFor(RestServiceModel.class).writeValueAsString(model);
      return new UserConfig(json);
    }
    catch (JsonProcessingException ex)
    {
      return new UserConfig("");
    }
  }
}