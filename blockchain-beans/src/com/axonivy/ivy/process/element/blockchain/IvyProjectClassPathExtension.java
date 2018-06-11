package com.axonivy.ivy.process.element.blockchain;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.core.IAccessRule;

import ch.ivyteam.ivy.java.IIvyProjectClassPathExtension;

public class IvyProjectClassPathExtension implements IIvyProjectClassPathExtension
{
  @Override
  public List<String> getCompileClassPathContributingBundles()
  {
    return getClassLoaderContributingBundles();
  }

  @Override
  public List<IAccessRule> getCompileClassPathAccessRules(String bundleIdentifier)
  {
    return Collections.emptyList();
  }

  @Override
  public List<String> getCompileClassPath(String bundleIdentifier)
  {
    return Collections.emptyList();
  }

  @Override
  public List<String> getClassLoaderContributingBundles()
  {
    return Arrays.asList("supplement.blockchain.beans");
  }
}
