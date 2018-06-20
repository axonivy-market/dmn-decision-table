package com.axonivy.ivy.process.element.blockchain.ui;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.process.model.element.value.Mapping;
import ch.ivyteam.ivy.process.model.element.value.Mappings;
import ch.ivyteam.ivy.project.IIvyProject;

public class BlockchainHelper
{
  private BlockchainHelper() {}

  static Mappings getMappings(Map<String, String> mappingsMap)
  {
    if (mappingsMap == null)
    {
      return new Mappings();
    }
    List<Mapping> mappings = new ArrayList<>();
    for(Entry<String, String> entry : mappingsMap.entrySet())
    {
      mappings.add(new Mapping(entry.getKey(), entry.getValue()));
    }
    return new Mappings(mappings);
  }

  static Method loadMethod(IIvyProject project, String contract, String function)
  {
    if (!StringUtils.isEmpty(contract) && !StringUtils.isEmpty(function))
    {
      List<Method> methods = loadDeclaredMethods(project, contract);
      for (Method method : methods)
      {
        if (method.toString().equals(function))
        {
          return method;
        }
      }
    }
    return null;
  }

  static List<Method> loadDeclaredMethods(IIvyProject project, String className)
  {
    Class<?> clazz;
    try
    {
      clazz = project.getProjectClassLoader().loadClass(className);
    }
    catch (ClassNotFoundException ex)
    {
      throw new RuntimeException(ex);
    }
    Method[] methods = clazz.getDeclaredMethods();
    return Arrays.asList(methods)
            .stream()
            .filter(method -> !Modifier.isStatic(method.getModifiers()))
            .collect(Collectors.toList());
  }
}
