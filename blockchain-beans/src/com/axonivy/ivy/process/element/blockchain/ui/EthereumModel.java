package com.axonivy.ivy.process.element.blockchain.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.ivyteam.ivy.process.model.element.value.bean.UserConfig;

public class EthereumModel
{
  public static final ObjectMapper mapper = new ObjectMapper();

  public Map<String, String> attributes = new HashMap<String, String>();
  public Map<String, String> properties = new HashMap<String, String>();
  public String contract = "";
  public String function = "";
  public Map<String, String> outputMappings = new HashMap<String, String>();
  public String outputCode = "";

  public static EthereumModel load(UserConfig config)
  {
    return load(config.getRawValue());
  }

  public static EthereumModel load(String configuration)
  {
    if (StringUtils.isEmpty(configuration))
    {
      return new EthereumModel();
    }
    try
    {
      return mapper.readerFor(EthereumModel.class).readValue(configuration);
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
}