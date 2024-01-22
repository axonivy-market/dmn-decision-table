package com.axonivy.ivy.process.element.rule.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ch.ivyteam.ivy.process.extension.ProgramConfig;

public class RulesModelSerialization {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final Set<String> KEYS = Set.of(
    "rows", "actionColumns", "conditionColumns"
  );

  static {
    MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
  }

  private RulesModelSerialization() {}

  public static Map<String, String> serialize(RulesModel model) throws JsonProcessingException {
    var json = MAPPER.writeValueAsString(model);
    var tree = (ObjectNode) MAPPER.readValue(json, JsonNode.class);
    Map<String, String> conf = new HashMap<>();
    tree.fields().forEachRemaining(et -> {
      conf.put(et.getKey(), et.getValue().toString());
    });
    return conf;
  }

  public static RulesModel deserialize(ProgramConfig program) throws Exception {
    var config = KEYS.stream()
      .filter(key -> program.get(key) != null)
      .map(key -> Map.entry(key, program.get(key)))
      .collect(Collectors.toMap(et -> et.getKey(), et -> et.getValue()));
    return deserialize(config);
  }

  public static RulesModel deserialize(Map<String, String> userConf) throws Exception {
    var obj = JsonNodeFactory.instance.objectNode();
    for(var et : userConf.entrySet()) {
      obj.set(et.getKey(), read(et.getValue()));
    }
    return MAPPER.readValue(obj.toString(), RulesModel.class);
  }

  private static JsonNode read(String value) throws JsonMappingException, JsonProcessingException {
    return MAPPER.readValue(value, JsonNode.class);
  }

}
