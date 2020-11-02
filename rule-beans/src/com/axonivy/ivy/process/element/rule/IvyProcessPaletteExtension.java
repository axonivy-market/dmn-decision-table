package com.axonivy.ivy.process.element.rule;

import ch.ivyteam.ivy.designer.process.ui.editor.palette.IIvyProcessPalette;
import ch.ivyteam.ivy.designer.process.ui.editor.palette.IIvyProcessPaletteExtension;

@SuppressWarnings("deprecation")
public class IvyProcessPaletteExtension implements IIvyProcessPaletteExtension
{
  private static final String RULE_GROUP = "Rule";

  @Override
  public void addGroups(IIvyProcessPalette palette)
  {
    palette.addGroup(RULE_GROUP, "Rules", 11000);

  }

  @Override
  public void addEntries(IIvyProcessPalette palette)
  {
    palette.addProcessElementEntry(RULE_GROUP, ProcessElementExtension.DECISION_ACTIVITY, 200);
  }

}
