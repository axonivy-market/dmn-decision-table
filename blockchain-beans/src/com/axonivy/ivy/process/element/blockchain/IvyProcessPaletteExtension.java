package com.axonivy.ivy.process.element.blockchain;

import ch.ivyteam.ivy.designer.process.ui.editor.palette.IIvyProcessPalette;
import ch.ivyteam.ivy.designer.process.ui.editor.palette.IIvyProcessPaletteExtension;

public class IvyProcessPaletteExtension implements IIvyProcessPaletteExtension
{
  private static final String BLOCKCHAIN_GROUP = "Blockchain";

  @Override
  public void addGroups(IIvyProcessPalette palette)
  {
    palette.addGroup(BLOCKCHAIN_GROUP, "Blockchain", 12000);
  }

  @Override
  public void addEntries(IIvyProcessPalette palette)
  {
    palette.addProcessElementEntry(BLOCKCHAIN_GROUP, ProcessElementExtension.ETHEREUM_ACTIVITY, 100);
  }
}
