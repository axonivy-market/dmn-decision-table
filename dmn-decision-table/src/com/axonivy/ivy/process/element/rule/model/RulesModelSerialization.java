package com.axonivy.ivy.process.element.rule.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class RulesModelSerialization {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  static {
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
  }

  private RulesModelSerialization() {}

  public static Map<String, String> serialize(RulesModel model) throws JsonProcessingException {
    var json = objectMapper.writeValueAsString(model);
    var tree = (ObjectNode) objectMapper.readValue(json, JsonNode.class);
    Map<String, String> conf = new HashMap<>();
    tree.fields().forEachRemaining(et -> {
      conf.put(et.getKey(), et.getValue().toString());
    });
    return conf;
  }

  public static RulesModel deserialize(String jsonModel)
          throws JsonParseException, JsonMappingException, IOException {
    if (StringUtils.isEmpty(jsonModel)) {
      return new RulesModel();
    }
    return objectMapper.readValue(jsonModel, RulesModel.class);
  }

  public static RulesModel deserialize(Map<String, String> userConf) throws Exception {
    var obj = JsonNodeFactory.instance.objectNode();
    for(var et : userConf.entrySet()) {
      obj.set(et.getKey(), read(et.getValue()));
    }
    return objectMapper.readValue(obj.toString(), RulesModel.class);
  }

  private static JsonNode read(String value) throws JsonMappingException, JsonProcessingException {
    return objectMapper.readValue(value, JsonNode.class);
  }

}
