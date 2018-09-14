package com.axonivy.ivy.process.element.rest.start.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ch.ivyteam.ivy.process.model.element.value.Mapping;
import ch.ivyteam.ivy.process.model.element.value.Mappings;

final class RestServiceUtil
{
  private RestServiceUtil() {}

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
}
