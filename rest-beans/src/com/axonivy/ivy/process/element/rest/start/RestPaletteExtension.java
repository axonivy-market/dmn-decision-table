package com.axonivy.ivy.process.element.rest.start;

import ch.ivyteam.ivy.designer.process.ui.editor.palette.IIvyProcessPalette;
import ch.ivyteam.ivy.designer.process.ui.editor.palette.IIvyProcessPaletteExtension;

public class RestPaletteExtension implements IIvyProcessPaletteExtension
{
  private static final String REST_GROUP = "RestWS";

  @Override
  public void addGroups(IIvyProcessPalette palette)
  {
    palette.addGroup(REST_GROUP, "RestWS", 9000);
  }

  @Override
  public void addEntries(IIvyProcessPalette palette)
  {
    palette.addProcessElementEntry(REST_GROUP, RestStartExtension.REST_START, 100);
  }
}
