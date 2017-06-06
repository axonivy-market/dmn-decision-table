package com.axonivy.ivy.process.element.rule.model;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class RulesModelSerialization
{
  private static final ObjectMapper objectMapper = new ObjectMapper();

  static
  {
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
  }

  private RulesModelSerialization()
  {
  }

  public static String serialize(RulesModel model) throws JsonProcessingException
  {
    return objectMapper.writeValueAsString(model);
  }

  public static RulesModel deserialize(String jsonModel) throws JsonParseException, JsonMappingException, IOException
  {
    if (StringUtils.isEmpty(jsonModel))
    {
      return new RulesModel();
    }
    return objectMapper.readValue(jsonModel, RulesModel.class);
  }

}
