package com.axonivy.ivy.process.element.rule.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class RuleBeansIcons
{
  public static final String PLUGIN_ID = "supplement.rule.beans";
  
  private static final ImageDescriptor NO_IMAGE = new ImageDescriptor()
  {
    @Override
    public ImageData getImageData()
    {
      return null;
    }
  };

  public static final ImageDescriptor CONDITION_COLUMN = loadIcon("cell-import.png");
  public static final ImageDescriptor OUTPUT_COLUMN = loadIcon("cell-export.png");
  
  
  private static ImageDescriptor loadIcon(String name)
  {
    try
    {
      if (!PlatformUI.isWorkbenchRunning())
      {
        return NO_IMAGE;
      }
    } catch (Throwable ex)
    {
      return NO_IMAGE;
    }
    return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, "icons/"+name);
  }
  
}
