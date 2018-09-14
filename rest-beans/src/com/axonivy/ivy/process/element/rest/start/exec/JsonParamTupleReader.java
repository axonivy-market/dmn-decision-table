package com.axonivy.ivy.process.element.rest.start.exec;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.server.ContainerRequest;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.type.MapType;

import ch.ivyteam.ivy.java.IJavaConfiguration;

class JsonParamTupleReader
{
  private final Map<String, Class<?>> paramTypes;
  
  public JsonParamTupleReader(IJavaConfiguration javaConfig, Map<String, String> inputParams)
  {
    this.paramTypes = Collections.unmodifiableMap(getParamTypes(javaConfig, inputParams));
  }
  
  private static Map<String, Class<?>> getParamTypes(IJavaConfiguration javaConfig, Map<String, String> inputParams)
  {
    Map<String, Class<?>> paramTypes = new HashMap<>();
    inputParams.forEach((paramName, paramClassName) -> {
      try
      {
        Class<?> clazz = Class.forName(paramClassName, false, javaConfig.getClassLoader());
        paramTypes.put(paramName, clazz);
      }
      catch (ClassNotFoundException localClassNotFoundException) {}
    });
    return paramTypes;
  }
  
  public Map<String, Object> readJsonAsParams(ContainerRequest jerseyRequest)
  {
    try
    {
      String json = getJsonEntity(jerseyRequest);
      if(StringUtils.isEmpty(json))
      {
        return Collections.emptyMap();
      }
      String tupleBagContent = StringUtils.substringAfter(json, "{");
      tupleBagContent = StringUtils.substringBeforeLast(tupleBagContent, "}");
      ObjectMapper mapper = new ObjectMapper();
      TupleMapDeserializer custom = new TupleMapDeserializer(this.paramTypes);
      MapType paramTupleMap = mapper.getTypeFactory()
        .constructMapType(HashMap.class, String.class, Object.class)
        .withContentValueHandler(custom);
      JsonParser jsonParser = mapper.getFactory().createParser(json);
      return (Map<String, Object>)mapper.readValue(jsonParser, paramTupleMap);
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }
    return Collections.emptyMap();
  }
  
  private static String getJsonEntity(ContainerRequest jerseyContext) throws IOException
  {
      try (InputStream stream = jerseyContext.getEntityStream())
      {
        return IOUtils.toString(stream, Charset.defaultCharset());
      }
  }
  
  private static class TupleMapDeserializer extends StdDeserializer<Object>
  {
    private Map<String, Class<?>> types;
    
    public TupleMapDeserializer(Map<String, Class<?>> paramTypes)
    {
      super((JavaType) null);
      this.types = paramTypes;
    }
    
    @Override
    public Object deserialize(JsonParser jp, DeserializationContext ctxt)
      throws JsonMappingException
    {
      Class<?> valueClazz = null;
      try
      {
        valueClazz = getTargetClassOfCurrentField(jp);
        ObjectMapper valueMapper = new ObjectMapper();
        JavaType valueType = valueMapper.getTypeFactory().constructType(valueClazz);
        return valueMapper.readValue(jp, valueType);
      }
      catch (IOException ex)
      {
        throw JsonMappingException.from(jp, "Error deserializing Json input params", ex);
      }
    }
    
    private Class<?> getTargetClassOfCurrentField(JsonParser jp)
      throws IOException
    {
      String fieldName = jp.getCurrentName();
      Class<?> clazz = this.types.get(fieldName);
      if (clazz == null) {
        return Object.class;
      }
      return clazz;
    }
  }
}
